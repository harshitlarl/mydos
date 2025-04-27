package com.mydos.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TaskEntity represents a user task in the MyDOS system.
 * This entity is stored in the SQL database.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "completed", nullable = false)
    private boolean completed;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructor with essential fields for quick creation
    public Task(String title, String description, LocalDateTime dueDate, User user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.priority = 1; // Default priority
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
    
    // Pre-persist hook to set creation time
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Pre-update hook to set update time
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
