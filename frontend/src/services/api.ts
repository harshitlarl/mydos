import axios from 'axios';

// Create an Axios instance with default config
const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Task related API calls
export const TaskAPI = {
  // Get all tasks
  getTasks: async (userId?: number) => {
    const params = userId ? { userId } : {};
    const response = await API.get('/tasks', { params });
    return response.data;
  },
  
  // Get a single task by ID
  getTask: async (id: number) => {
    const response = await API.get(`/tasks/${id}`);
    return response.data;
  },
  
  // Create a new task
  createTask: async (task: any) => {
    const response = await API.post('/tasks', task);
    return response.data;
  },
  
  // Update an existing task
  updateTask: async (id: number, task: any) => {
    const response = await API.put(`/tasks/${id}`, task);
    return response.data;
  },
  
  // Delete a task
  deleteTask: async (id: number) => {
    const response = await API.delete(`/tasks/${id}`);
    return response.data;
  },
  
  // Mark a task as complete/incomplete
  toggleTaskComplete: async (id: number, completed: boolean) => {
    const response = await API.put(`/tasks/${id}/complete?completed=${completed}`);
    return response.data;
  },
};

// Expense related API calls
export const ExpenseAPI = {
  // Get expenses with optional filters
  getExpenses: async (params?: {
    userId?: number;
    category?: string;
    startDate?: string; 
    endDate?: string;
  }) => {
    const response = await API.get('/expenses', { params });
    return response.data;
  },
  
  // Get a single expense by ID
  getExpense: async (id: number) => {
    const response = await API.get(`/expenses/${id}`);
    return response.data;
  },
  
  // Create a new expense
  createExpense: async (expense: any) => {
    const response = await API.post('/expenses', expense);
    return response.data;
  },
  
  // Update an existing expense
  updateExpense: async (id: number, expense: any) => {
    const response = await API.put(`/expenses/${id}`, expense);
    return response.data;
  },
  
  // Delete an expense
  deleteExpense: async (id: number) => {
    const response = await API.delete(`/expenses/${id}`);
    return response.data;
  },
  
  // Get expense summary
  getExpenseSummary: async (params: {
    userId: number;
    startDate?: string;
    endDate?: string;
  }) => {
    const response = await API.get('/expenses/summary', { params });
    return response.data;
  },
};

// Analytics related API calls
export const AnalyticsAPI = {
  // Get system stats
  getSystemStats: async () => {
    const response = await API.get('/analytics/system/stats');
    return response.data;
  },
  
  // Record user activity
  recordActivity: async (activity: any) => {
    const response = await API.post('/analytics/activity', activity);
    return response.data;
  },
  
  // Get user activity logs
  getUserActivityLogs: async (userId: number, params?: {
    startDate?: string;
    endDate?: string;
  }) => {
    const response = await API.get(`/analytics/activity/user/${userId}`, { params });
    return response.data;
  },
  
  // Get analytics metrics
  getAnalyticsMetrics: async (metricType: string, params: {
    startDate: string;
    endDate: string;
  }) => {
    const response = await API.get(`/analytics/metrics/${metricType}`, { params });
    return response.data;
  },
};

export default {
  TaskAPI,
  ExpenseAPI,
  AnalyticsAPI,
};