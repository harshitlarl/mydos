package com.mydos.resources;

import com.mydos.core.Task;
import com.mydos.core.User;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API resource for managing tasks.
 */
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TaskResource {
    
    private final SessionFactory sessionFactory;
    
    public TaskResource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @GET
    @UnitOfWork
    public Response getTasks(@QueryParam("userId") Long userId) {
        log.debug("Fetching tasks for userId: {}", userId);
        try {
            List<Task> tasks;
            if (userId != null) {
                tasks = sessionFactory.getCurrentSession()
                    .createQuery("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.dueDate", Task.class)
                    .setParameter("userId", userId)
                    .getResultList();
            } else {
                tasks = sessionFactory.getCurrentSession()
                    .createQuery("SELECT t FROM Task t ORDER BY t.dueDate", Task.class)
                    .getResultList();
            }
            
            return Response.ok(tasks).build();
        } catch (Exception e) {
            log.error("Error fetching tasks", e);
            return Response.serverError().entity("Failed to retrieve tasks").build();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getTask(@PathParam("id") Long id) {
        log.debug("Fetching task with ID: {}", id);
        try {
            Task task = sessionFactory.getCurrentSession().get(Task.class, id);
            
            if (task == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
            }
            
            return Response.ok(task).build();
        } catch (Exception e) {
            log.error("Error fetching task with ID: {}", id, e);
            return Response.serverError().entity("Failed to retrieve task").build();
        }
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(Task task) {
        log.debug("Creating new task: {}", task.getTitle());
        try {
            // Validate user exists
            User user = sessionFactory.getCurrentSession().get(User.class, task.getUser().getId());
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
            }
            
            // Set default values
            task.setCreatedAt(LocalDateTime.now());
            if (task.getPriority() == null) {
                task.setPriority(1); // Default priority
            }
            
            sessionFactory.getCurrentSession().save(task);
            
            return Response.status(Response.Status.CREATED).entity(task).build();
        } catch (Exception e) {
            log.error("Error creating task", e);
            return Response.serverError().entity("Failed to create task").build();
        }
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(@PathParam("id") Long id, Task taskUpdate) {
        log.debug("Updating task with ID: {}", id);
        try {
            Task existingTask = sessionFactory.getCurrentSession().get(Task.class, id);
            
            if (existingTask == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
            }
            
            // Update fields
            existingTask.setTitle(taskUpdate.getTitle());
            existingTask.setDescription(taskUpdate.getDescription());
            existingTask.setDueDate(taskUpdate.getDueDate());
            existingTask.setCompleted(taskUpdate.isCompleted());
            existingTask.setPriority(taskUpdate.getPriority());
            
            sessionFactory.getCurrentSession().update(existingTask);
            
            return Response.ok(existingTask).build();
        } catch (Exception e) {
            log.error("Error updating task with ID: {}", id, e);
            return Response.serverError().entity("Failed to update task").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteTask(@PathParam("id") Long id) {
        log.debug("Deleting task with ID: {}", id);
        try {
            Task task = sessionFactory.getCurrentSession().get(Task.class, id);
            
            if (task == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
            }
            
            sessionFactory.getCurrentSession().delete(task);
            
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting task with ID: {}", id, e);
            return Response.serverError().entity("Failed to delete task").build();
        }
    }

    @PUT
    @Path("/{id}/complete")
    @UnitOfWork
    public Response markTaskComplete(@PathParam("id") Long id, @QueryParam("completed") boolean completed) {
        log.debug("Marking task with ID: {} as completed: {}", id, completed);
        try {
            Task task = sessionFactory.getCurrentSession().get(Task.class, id);
            
            if (task == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
            }
            
            task.setCompleted(completed);
            sessionFactory.getCurrentSession().update(task);
            
            return Response.ok(task).build();
        } catch (Exception e) {
            log.error("Error updating task completion status with ID: {}", id, e);
            return Response.serverError().entity("Failed to update task completion status").build();
        }
    }
}