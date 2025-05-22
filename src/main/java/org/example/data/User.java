package org.example.data;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    // May use RFC 5322 regex to validate email structure
    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private int age = -1;

    @UpdateTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {

    }

    @Override
    public String toString() {
        return "User [Id: %d| Name: %s| Age: %d| Email: %s| Last updated: %s]"
                .formatted(id, name, age, email, createdAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
