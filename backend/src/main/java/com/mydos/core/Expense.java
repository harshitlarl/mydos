package com.mydos.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ExpenseEntity represents a user expense in the MyDOS system.
 * This entity is stored in the SQL database.
 */
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructor with essential fields for quick creation
    public Expense(BigDecimal amount, String description, String category, LocalDateTime expenseDate, User user) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.expenseDate = expenseDate;
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