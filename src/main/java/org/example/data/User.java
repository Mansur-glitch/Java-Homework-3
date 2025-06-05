package org.example.data;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age = -1;

    @Column(name = "email")
    private String email;

    @UpdateTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {

    }

    public User(int id, String name, int age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email, createdAt);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (! (other instanceof User castedOther)) return false;
        return this.id == castedOther.id
                && Objects.equals(this.name, castedOther.name)
                && this.age == castedOther.age
                && Objects.equals(this.email, castedOther.email)
                && Objects.equals(this.createdAt, castedOther.createdAt);
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
