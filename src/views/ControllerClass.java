package views;

import models.Patient;
import models.Person;

import java.io.IOException;
import java.sql.SQLException;

public interface ControllerClass {
    public abstract void preLoadData(Person person) throws IOException, SQLException;
    public abstract void preLoadData(Person person, int visitID, Patient patient) throws IOException;
}
