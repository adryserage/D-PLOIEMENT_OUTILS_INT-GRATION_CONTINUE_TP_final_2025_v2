package ca.college.bdeb.tp;

import java.sql.Timestamp;

/**
 * Modèle représentant un étudiant avec nom, prénom et spécialité
 */
public class Student {
    private int id;
    private String lastName;
    private String firstName;
    private String specialty;
    private Timestamp createdAt;

    public Student() {
    }

    public Student(String lastName, String firstName, String specialty) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.specialty = specialty;
    }

    public Student(int id, String lastName, String firstName, String specialty, Timestamp createdAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.specialty = specialty;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, specialty);
    }
}
