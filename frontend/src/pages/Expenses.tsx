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
  IconButton,
  Divider,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import AddIcon from '@mui/icons-material/Add';
import { format } from 'date-fns';

interface Expense {
  id: number;
  amount: number;
  description: string;
  category: string;
  date: Date;
}

const categories = [
  'Food',
  'Transportation',
  'Entertainment',
  'Housing',
  'Utilities',
  'Healthcare',
  'Personal',
  'Education',
  'Travel',
  'Other'
];

const Expenses: React.FC = () => {
  const [expenses, setExpenses] = useState<Expense[]>([
    {
      id: 1,
      amount: 45.99,
      description: 'Grocery shopping',
      category: 'Food',
      date: new Date('2025-04-20'),
    },
    {
      id: 2,
      amount: 29.99,
      description: 'Monthly streaming subscription',
      category: 'Entertainment',
      date: new Date('2025-04-15'),
    },
    {
      id: 3,
      amount: 120.50,
      description: 'Electricity bill',
      category: 'Utilities',
      date: new Date('2025-04-10'),
    },
  ]);

  const [open, setOpen] = useState(false);
  const [currentExpense, setCurrentExpense] = useState<Expense>({
    id: 0,
    amount: 0,
    description: '',
    category: 'Other',
    date: new Date(),
  });
  const [isEditing, setIsEditing] = useState(false);

  // Calculate total expenses
  const totalExpenses = expenses.reduce((acc, expense) => acc + expense.amount, 0);

  // Calculate expenses by category
  const expensesByCategory = expenses.reduce((acc, expense) => {
    acc[expense.category] = (acc[expense.category] || 0) + expense.amount;
    return acc;
  }, {} as Record<string, number>);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setCurrentExpense({
      id: 0,
      amount: 0,
      description: '',
      category: 'Other',
      date: new Date(),
    });
    setIsEditing(false);
  };

  const handleSave = () => {
    if (isEditing) {
      // Update existing expense
      setExpenses(expenses.map(expense => 
        expense.id === currentExpense.id ? currentExpense : expense
      ));
    } else {
      // Create new expense
      const newExpense = {
        ...currentExpense,
        id: Date.now(),
      };
      setExpenses([...expenses, newExpense]);
    }
    handleClose();
  };

  const handleEdit = (expense: Expense) => {
    setCurrentExpense(expense);
    setIsEditing(true);
    setOpen(true);
  };

  const handleDelete = (id: number) => {
    setExpenses(expenses.filter(expense => expense.id !== id));
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" gutterBottom>Expenses</Typography>
        <Button 
          variant="contained" 
          startIcon={<AddIcon />}
          onClick={handleClickOpen}
        >
          New Expense
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardHeader title="Total Expenses" />
            <Divider />
            <CardContent>
              <Typography variant="h3">${totalExpenses.toFixed(2)}</Typography>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={8}>
          <Card>
            <CardHeader title="Expenses by Category" />
            <Divider />
            <CardContent>
              <TableContainer component={Paper} elevation={0}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Category</TableCell>
                      <TableCell align="right">Amount</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {Object.entries(expensesByCategory).map(([category, amount]) => (
                      <TableRow key={category}>
                        <TableCell component="th" scope="row">
                          {category}
                        </TableCell>
                        <TableCell align="right">${amount.toFixed(2)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <CardHeader title="Expense Transactions" />
        <Divider />
        <TableContainer component={Paper} elevation={0}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Category</TableCell>
                <TableCell align="right">Amount</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {expenses.map((expense) => (
                <TableRow key={expense.id}>
                  <TableCell>{format(new Date(expense.date), 'MMM d, yyyy')}</TableCell>
                  <TableCell>{expense.description}</TableCell>
                  <TableCell>{expense.category}</TableCell>
                  <TableCell align="right">${expense.amount.toFixed(2)}</TableCell>
                  <TableCell align="center">
                    <IconButton size="small" onClick={() => handleEdit(expense)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton size="small" onClick={() => handleDelete(expense.id)}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      {/* Expense Form Dialog */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{isEditing ? 'Edit Expense' : 'Add New Expense'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="amount"
            label="Amount"
            type="number"
            fullWidth
            variant="outlined"
            value={currentExpense.amount}
            onChange={(e) => setCurrentExpense({ ...currentExpense, amount: parseFloat(e.target.value) || 0 })}
          />
          <TextField
            margin="dense"
            id="description"
            label="Description"
            type="text"
            fullWidth
            variant="outlined"
            value={currentExpense.description}
            onChange={(e) => setCurrentExpense({ ...currentExpense, description: e.target.value })}
          />
          <TextField
            select
            margin="dense"
            id="category"
            label="Category"
            fullWidth
            variant="outlined"
            value={currentExpense.category}
            onChange={(e) => setCurrentExpense({ ...currentExpense, category: e.target.value })}
          >
            {categories.map((option) => (
              <MenuItem key={option} value={option}>
                {option}
              </MenuItem>
            ))}
          </TextField>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Date"
              value={currentExpense.date}
              onChange={(newValue) => {
                if (newValue) {
                  setCurrentExpense({ ...currentExpense, date: newValue });
                }
              }}
              sx={{ mt: 2, width: '100%' }}
            />
          </LocalizationProvider>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleSave}>Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Expenses;