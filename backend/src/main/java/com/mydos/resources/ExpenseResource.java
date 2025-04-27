package com.mydos.resources;

import com.mydos.core.Expense;
import com.mydos.core.User;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API resource for managing expenses.
 */
@Path("/api/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ExpenseResource {

    private final SessionFactory sessionFactory;
    
    public ExpenseResource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @GET
    @UnitOfWork
    public Response getExpenses(@QueryParam("userId") Long userId, 
                              @QueryParam("category") String category,
                              @QueryParam("startDate") String startDate,
                              @QueryParam("endDate") String endDate) {
        log.debug("Fetching expenses. userId: {}, category: {}, startDate: {}, endDate: {}", 
            userId, category, startDate, endDate);
        
        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT e FROM Expense e WHERE 1=1");
            Map<String, Object> parameters = new HashMap<>();
            
            if (userId != null) {
                queryBuilder.append(" AND e.user.id = :userId");
                parameters.put("userId", userId);
            }
            
            if (category != null && !category.trim().isEmpty()) {
                queryBuilder.append(" AND e.category = :category");
                parameters.put("category", category);
            }
            
            LocalDateTime parsedStartDate = null;
            LocalDateTime parsedEndDate = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    parsedStartDate = LocalDate.parse(startDate).atStartOfDay();
                    queryBuilder.append(" AND e.expenseDate >= :startDate");
                    parameters.put("startDate", parsedStartDate);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid start date format. Use yyyy-MM-dd").build();
                }
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    parsedEndDate = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
                    queryBuilder.append(" AND e.expenseDate < :endDate");
                    parameters.put("endDate", parsedEndDate);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid end date format. Use yyyy-MM-dd").build();
                }
            }
            
            queryBuilder.append(" ORDER BY e.expenseDate DESC");
            
            var query = sessionFactory.getCurrentSession()
                .createQuery(queryBuilder.toString(), Expense.class);
            
            // Set parameters
            parameters.forEach(query::setParameter);
            
            List<Expense> expenses = query.getResultList();
            
            return Response.ok(expenses).build();
        } catch (Exception e) {
            log.error("Error fetching expenses", e);
            return Response.serverError().entity("Failed to retrieve expenses").build();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getExpense(@PathParam("id") Long id) {
        log.debug("Fetching expense with ID: {}", id);
        try {
            Expense expense = sessionFactory.getCurrentSession().get(Expense.class, id);
            
            if (expense == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Expense not found").build();
            }
            
            return Response.ok(expense).build();
        } catch (Exception e) {
            log.error("Error fetching expense with ID: {}", id, e);
            return Response.serverError().entity("Failed to retrieve expense").build();
        }
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExpense(Expense expense) {
        log.debug("Creating new expense: {}", expense.getDescription());
        try {
            // Validate user exists
            User user = sessionFactory.getCurrentSession().get(User.class, expense.getUser().getId());
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
            }
            
            // Set default values
            expense.setCreatedAt(LocalDateTime.now());
            
            sessionFactory.getCurrentSession().save(expense);
            
            return Response.status(Response.Status.CREATED).entity(expense).build();
        } catch (Exception e) {
            log.error("Error creating expense", e);
            return Response.serverError().entity("Failed to create expense").build();
        }
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExpense(@PathParam("id") Long id, Expense expenseUpdate) {
        log.debug("Updating expense with ID: {}", id);
        try {
            Expense existingExpense = sessionFactory.getCurrentSession().get(Expense.class, id);
            
            if (existingExpense == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Expense not found").build();
            }
            
            // Update fields
            existingExpense.setAmount(expenseUpdate.getAmount());
            existingExpense.setDescription(expenseUpdate.getDescription());
            existingExpense.setCategory(expenseUpdate.getCategory());
            existingExpense.setExpenseDate(expenseUpdate.getExpenseDate());
            existingExpense.setPaymentMethod(expenseUpdate.getPaymentMethod());
            
            sessionFactory.getCurrentSession().update(existingExpense);
            
            return Response.ok(existingExpense).build();
        } catch (Exception e) {
            log.error("Error updating expense with ID: {}", id, e);
            return Response.serverError().entity("Failed to update expense").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteExpense(@PathParam("id") Long id) {
        log.debug("Deleting expense with ID: {}", id);
        try {
            Expense expense = sessionFactory.getCurrentSession().get(Expense.class, id);
            
            if (expense == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Expense not found").build();
            }
            
            sessionFactory.getCurrentSession().delete(expense);
            
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting expense with ID: {}", id, e);
            return Response.serverError().entity("Failed to delete expense").build();
        }
    }
    
    @GET
    @Path("/summary")
    @UnitOfWork
    public Response getExpenseSummary(@QueryParam("userId") Long userId,
                                     @QueryParam("startDate") String startDate,
                                     @QueryParam("endDate") String endDate) {
        log.debug("Calculating expense summary. userId: {}, startDate: {}, endDate: {}", 
            userId, startDate, endDate);
            
        try {
            // Validate required parameters
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("userId is required for expense summary").build();
            }
            
            // Build and execute the base query to get expenses
            List<Expense> expenses = getExpensesForSummary(userId, startDate, endDate);
            
            // Calculate summary data
            Map<String, Object> summary = new HashMap<>();
            
            // Total amount
            double totalAmount = expenses.stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();
                
            summary.put("totalAmount", totalAmount);
            
            // Expenses by category
            Map<String, Double> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                    Expense::getCategory,
                    Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
                
            summary.put("byCategory", expensesByCategory);
            
            // Add time period info to response
            summary.put("userId", userId);
            if (startDate != null) summary.put("startDate", startDate);
            if (endDate != null) summary.put("endDate", endDate);
            
            return Response.ok(summary).build();
            
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Invalid date format. Use yyyy-MM-dd").build();
        } catch (Exception e) {
            log.error("Error calculating expense summary", e);
            return Response.serverError().entity("Failed to calculate expense summary").build();
        }
    }
    
    /**
     * Helper method to get expenses for summary calculation
     */
    private List<Expense> getExpensesForSummary(Long userId, String startDateStr, String endDateStr) 
            throws DateTimeParseException {
        
        StringBuilder queryBuilder = new StringBuilder("SELECT e FROM Expense e WHERE e.user.id = :userId");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        
        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            queryBuilder.append(" AND e.expenseDate >= :startDate");
            parameters.put("startDate", startDate);
        }
        
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            LocalDateTime endDate = LocalDate.parse(endDateStr).plusDays(1).atStartOfDay();
            queryBuilder.append(" AND e.expenseDate < :endDate");
            parameters.put("endDate", endDate);
        }
        
        var query = sessionFactory.getCurrentSession()
            .createQuery(queryBuilder.toString(), Expense.class);
        
        // Set parameters
        parameters.forEach(query::setParameter);
        
        return query.getResultList();
    }
}