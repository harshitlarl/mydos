./start-local.shimport React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Grid, 
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Card,
  CardContent,
  CardHeader,
  CircularProgress 
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PendingIcon from '@mui/icons-material/Pending';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import { TaskAPI, ExpenseAPI, AnalyticsAPI } from '../services/api';

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [recentTasks, setRecentTasks] = useState<any[]>([]);
  const [recentExpenses, setRecentExpenses] = useState<any[]>([]);
  const [stats, setStats] = useState({
    totalTasks: 0,
    completedTasks: 0,
    pendingTasks: 0,
    totalExpenses: 0,
  });

  // Temporary user ID - in a real app this would come from auth context
  const userId = 1;

  useEffect(() => {
    // Function to load all dashboard data
    const loadDashboardData = async () => {
      try {
        setLoading(true);
        
        // Get system stats from analytics
        const systemStats = await AnalyticsAPI.getSystemStats().catch(() => ({
          // Fallback if API fails
          activeUsers: 1,
          totalTasks: 0,
          completedTasks: 0,
          pendingTasks: 0,
          totalExpenses: 0,
        }));
        
        // Get recent tasks for the user
        const tasks = await TaskAPI.getTasks(userId).catch(() => []);
        
        // Get recent expenses for the user
        const expenses = await ExpenseAPI.getExpenses({ userId }).catch(() => []);
        
        // Update state with fetched data
        setStats({
          totalTasks: systemStats.totalTasks || tasks.length,
          completedTasks: systemStats.completedTasks || tasks.filter((t: any) => t.completed).length,
          pendingTasks: systemStats.pendingTasks || tasks.filter((t: any) => !t.completed).length,
          totalExpenses: systemStats.totalExpenses || expenses.reduce((sum: number, e: any) => sum + parseFloat(e.amount), 0),
        });
        
        // Sort and limit recent items
        const sortedTasks = [...tasks].sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        ).slice(0, 3);
        
        const sortedExpenses = [...expenses].sort((a, b) => 
          new Date(b.expenseDate).getTime() - new Date(a.expenseDate).getTime()
        ).slice(0, 3);
        
        setRecentTasks(sortedTasks);
        setRecentExpenses(sortedExpenses);
        
        // Record dashboard view activity
        await AnalyticsAPI.recordActivity({
          userId,
          username: "active_user",
          activityType: "DASHBOARD_VIEW",
          description: "User viewed dashboard",
          timestamp: new Date().toISOString()
        }).catch(error => console.log("Failed to record activity", error));
        
      } catch (err) {
        console.error("Error loading dashboard data", err);
        setError("Failed to load dashboard data. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, [userId]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Dashboard</Typography>
      
      <Grid container spacing={3}>
        {/* Stats Cards */}
        <Grid item xs={12} md={6} lg={3}>
          <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140 }}>
            <Typography component="h2" variant="h6" color="primary" gutterBottom>
              Total Tasks
            </Typography>
            <Typography component="p" variant="h4">
              {stats.totalTasks}
            </Typography>
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140 }}>
            <Typography component="h2" variant="h6" color="primary" gutterBottom>
              Completed Tasks
            </Typography>
            <Typography component="p" variant="h4">
              {stats.completedTasks}
            </Typography>
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140 }}>
            <Typography component="h2" variant="h6" color="primary" gutterBottom>
              Pending Tasks
            </Typography>
            <Typography component="p" variant="h4">
              {stats.pendingTasks}
            </Typography>
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140 }}>
            <Typography component="h2" variant="h6" color="primary" gutterBottom>
              Total Expenses
            </Typography>
            <Typography component="p" variant="h4">
              ${stats.totalExpenses.toFixed(2)}
            </Typography>
          </Paper>
        </Grid>
        
        {/* Recent Tasks */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader title="Recent Tasks" />
            <Divider />
            <CardContent>
              {recentTasks.length > 0 ? (
                <List>
                  {recentTasks.map((task) => (
                    <React.Fragment key={task.id}>
                      <ListItem>
                        <ListItemIcon>
                          {task.completed ? <CheckCircleIcon color="success" /> : <PendingIcon color="warning" />}
                        </ListItemIcon>
                        <ListItemText
                          primary={task.title}
                          secondary={task.completed ? 'Completed' : `Due: ${new Date(task.dueDate).toLocaleDateString()}`}
                        />
                      </ListItem>
                      <Divider variant="inset" component="li" />
                    </React.Fragment>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="textSecondary" align="center">
                  No tasks yet. Create your first task!
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
        
        {/* Recent Expenses */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader title="Recent Expenses" />
            <Divider />
            <CardContent>
              {recentExpenses.length > 0 ? (
                <List>
                  {recentExpenses.map((expense) => (
                    <React.Fragment key={expense.id}>
                      <ListItem>
                        <ListItemIcon>
                          <AttachMoneyIcon color="primary" />
                        </ListItemIcon>
                        <ListItemText
                          primary={expense.description}
                          secondary={`${expense.category} - $${parseFloat(expense.amount).toFixed(2)}`}
                        />
                      </ListItem>
                      <Divider variant="inset" component="li" />
                    </React.Fragment>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="textSecondary" align="center">
                  No expenses yet. Add your first expense!
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;