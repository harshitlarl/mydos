package com.mydos.resources;

import com.mydos.core.User;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API resource for managing users.
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UserResource {
    
    private final SessionFactory sessionFactory;
    
    public UserResource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @GET
    @UnitOfWork
    public Response getAllUsers() {
        log.debug("Fetching all users");
        try {
            List<User> users = sessionFactory.getCurrentSession()
                .createQuery("SELECT u FROM User u WHERE u.active = :active", User.class)
                .setParameter("active", true)
                .getResultList();
            
            return Response.ok(users).build();
        } catch (Exception e) {
            log.error("Error fetching users", e);
            return Response.serverError().entity("Failed to retrieve users").build();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getUserById(@PathParam("id") Long id) {
        log.debug("Fetching user with ID: {}", id);
        try {
            User user = sessionFactory.getCurrentSession().get(User.class, id);
            
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
            
            return Response.ok(user).build();
        } catch (Exception e) {
            log.error("Error fetching user with ID: {}", id, e);
            return Response.serverError().entity("Failed to retrieve user").build();
        }
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        log.debug("Creating new user: {}", user.getUsername());
        try {
            // Set default values
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);
            
            sessionFactory.getCurrentSession().save(user);
            
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            log.error("Error creating user", e);
            return Response.serverError().entity("Failed to create user").build();
        }
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, User userUpdate) {
        log.debug("Updating user with ID: {}", id);
        try {
            User existingUser = sessionFactory.getCurrentSession().get(User.class, id);
            
            if (existingUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
            
            // Update fields
            existingUser.setUsername(userUpdate.getUsername());
            existingUser.setEmail(userUpdate.getEmail());
            existingUser.setFullName(userUpdate.getFullName());
            if (userUpdate.getPasswordHash() != null && !userUpdate.getPasswordHash().isEmpty()) {
                existingUser.setPasswordHash(userUpdate.getPasswordHash());
            }
            
            sessionFactory.getCurrentSession().update(existingUser);
            
            return Response.ok(existingUser).build();
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", id, e);
            return Response.serverError().entity("Failed to update user").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteUser(@PathParam("id") Long id) {
        log.debug("Deleting user with ID: {}", id);
        try {
            User user = sessionFactory.getCurrentSession().get(User.class, id);
            
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
            
            // Soft delete - mark as inactive instead of removing
            user.setActive(false);
            sessionFactory.getCurrentSession().update(user);
            
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", id, e);
            return Response.serverError().entity("Failed to delete user").build();
        }
    }
}