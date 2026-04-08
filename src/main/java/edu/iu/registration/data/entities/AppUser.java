package edu.iu.registration.data.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String classYear;

    @ManyToOne(optional = false)
    private Major major;

    @ManyToOne
    private Minor minor;

    @ManyToOne
    private Specialization specialization;

    public AppUser() {
    }

    public AppUser(String username, String password, String role, String classYear,
                   Major major, Minor minor, Specialization specialization) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.classYear = classYear;
        this.major = major;
        this.minor = minor;
        this.specialization = specialization;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getClassYear() {
        return classYear;
    }

    public Major getMajor() {
        return major;
    }

    public Minor getMinor() {
        return minor;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setClassYear(String classYear) {
        this.classYear = classYear;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public void setMinor(Minor minor) {
        this.minor = minor;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser appUser)) return false;
        return Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}