package edu.iu.registration.data.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Major major;

    public Specialization() {}

    public Specialization(String name, Major major) {
        this.name = name;
        this.major = major;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Major getMajor() {
        return major;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialization that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}