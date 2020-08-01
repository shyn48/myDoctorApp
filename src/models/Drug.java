package models;

import javafx.collections.FXCollections;

import java.sql.*;
import java.time.LocalDate;

public class Drug {
    private String drugName;
    private int amount, patientID;
    private LocalDate dueDate;
    private String time;

    public Drug(String drugName, int amount, int patientID, LocalDate dueDate, String time) {
        this.drugName = drugName;
        this.amount = amount;
        this.patientID = patientID;
        this.dueDate = dueDate;
        this.time = time;
    }

    public Drug(String drugName, int amount, int patientID) {
        this.drugName = drugName;
        this.amount = amount;
        this.patientID = patientID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
}
