import React, { useState } from 'react';
import {
  Box,
  Typography,
  Button,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Card,
  CardHeader,
  CardContent,
  CardActions,
  Checkbox,
  FormControlLabel,
  IconButton,
  Divider,
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import AddIcon from '@mui/icons-material/Add';
import { format } from 'date-fns';

interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: Date;
  completed: boolean;
}

const Tasks: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([
    {
      id: 1,
      title: 'Complete project proposal',
      description: 'Finalize the project proposal document and send it for review',
      dueDate: new Date('2025-05-05T14:00:00'),
      completed: false,
    },
    {
      id: 2,
      title: 'Update website content',
      description: 'Update the About and Services pages with new information',
      dueDate: new Date('2025-05-06T12:00:00'),
      completed: false,
    },
    {
      id: 3,
      title: 'Review client feedback',
      description: 'Go through client feedback and make necessary adjustments',
      dueDate: new Date('2025-05-02T16:30:00'),
      completed: true,
    },
  ]);

  const [open, setOpen] = useState(false);
  const [currentTask, setCurrentTask] = useState<Task>({
    id: 0,
    title: '',
    description: '',
    dueDate: new Date(),
    completed: false,
  });
  const [isEditing, setIsEditing] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setCurrentTask({
      id: 0,
      title: '',
      description: '',
      dueDate: new Date(),
      completed: false,
    });
    setIsEditing(false);
  };

  const handleSave = () => {
    if (isEditing) {
      // Update existing task
      setTasks(tasks.map(task => 
        task.id === currentTask.id ? currentTask : task
      ));
    } else {
      // Create new task
      const newTask = {
        ...currentTask,
        id: Date.now(),
      };
      setTasks([...tasks, newTask]);
    }
    handleClose();
  };

  const handleEdit = (task: Task) => {
    setCurrentTask(task);
    setIsEditing(true);
    setOpen(true);
  };

  const handleDelete = (id: number) => {
    setTasks(tasks.filter(task => task.id !== id));
  };

  const handleToggleComplete = (id: number) => {
    setTasks(tasks.map(task => 
      task.id === id ? { ...task, completed: !task.completed } : task
    ));
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" gutterBottom>Tasks</Typography>
        <Button 
          variant="contained" 
          startIcon={<AddIcon />}
          onClick={handleClickOpen}
        >
          New Task
        </Button>
      </Box>

      <Grid container spacing={3}>
        {tasks.map((task) => (
          <Grid item xs={12} md={6} lg={4} key={task.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardHeader
                title={
                  <Typography 
                    variant="h6" 
                    sx={{ 
                      textDecoration: task.completed ? 'line-through' : 'none',
                      color: task.completed ? 'text.secondary' : 'text.primary' 
                    }}
                  >
                    {task.title}
                  </Typography>
                }
                subheader={`Due: ${format(new Date(task.dueDate), 'MMM d, yyyy, h:mm a')}`}
              />
              <Divider />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  {task.description}
                </Typography>
              </CardContent>
              <CardActions sx={{ justifyContent: 'space-between' }}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={task.completed}
                      onChange={() => handleToggleComplete(task.id)}
                    />
                  }
                  label="Completed"
                />
                <Box>
                  <IconButton size="small" onClick={() => handleEdit(task)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton size="small" onClick={() => handleDelete(task.id)}>
                    <DeleteIcon />
                  </IconButton>
                </Box>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Task Form Dialog */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{isEditing ? 'Edit Task' : 'Create New Task'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="title"
            label="Task Title"
            type="text"
            fullWidth
            variant="outlined"
            value={currentTask.title}
            onChange={(e) => setCurrentTask({ ...currentTask, title: e.target.value })}
          />
          <TextField
            margin="dense"
            id="description"
            label="Description"
            type="text"
            fullWidth
            variant="outlined"
            multiline
            rows={4}
            value={currentTask.description}
            onChange={(e) => setCurrentTask({ ...currentTask, description: e.target.value })}
          />
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DateTimePicker
              label="Due Date"
              value={currentTask.dueDate}
              onChange={(newValue) => {
                if (newValue) {
                  setCurrentTask({ ...currentTask, dueDate: newValue });
                }
              }}
              sx={{ mt: 2, width: '100%' }}
            />
          </LocalizationProvider>
          {isEditing && (
            <FormControlLabel
              control={
                <Checkbox
                  checked={currentTask.completed}
                  onChange={(e) => setCurrentTask({ ...currentTask, completed: e.target.checked })}
                />
              }
              label="Completed"
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleSave}>Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Tasks;