package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.DB;
import models.Doctor;
import models.PWGenerator;
import models.Patient;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginView implements Initializable {

    @FXML private CheckBox doctorCheckBox;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errMsgLabel;


    public void loginBtnPushed(ActionEvent event) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            if(doctorCheckBox.isSelected()){
                String sql = "SELECT * FROM doctors WHERE email = ?";

                preparedStatement = conn.prepareStatement(sql);

                preparedStatement.setString(1, emailField.getText());

                resultSet = preparedStatement.executeQuery();

                String dbPassword = null;
                byte[] salt = null;
                Doctor doctor = null;
                while(resultSet.next()){
                    dbPassword = resultSet.getString("password");
                    Blob blob = resultSet.getBlob("salt");

                    doctor = new Doctor(resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getDate("birthday").toLocalDate(),
                            resultSet.getString("speciality"));

                    doctor.setIdCardPic(new File(resultSet.getString("cardPicture")));
                    doctor.setImageFile(new File(resultSet.getString("imageFile")));
                    doctor.setId(resultSet.getInt("doctorID"));

                    int blobLength = (int) blob.length();
                    salt = blob.getBytes(1, blobLength);
                }

                String enteredPW = PWGenerator.getSHA512Password(passwordField.getText(), salt);

                SceneChanger sc = new SceneChanger();

                DoctorMainView controllerClass = new DoctorMainView();

                if (enteredPW.equals(dbPassword)){
                    sc.changeScenes(event, "DoctorMainView.fxml", "Hello Doctor", doctor, controllerClass);
                } else {
                    errMsgLabel.setText("ID and Password do not match");
                }

            } else {
                String sql = "SELECT * FROM patients WHERE email=?";

                preparedStatement = conn.prepareStatement(sql);

                preparedStatement.setString(1, emailField.getText());

                resultSet = preparedStatement.executeQuery();

                String dbPassword = null;
                byte[] salt = null;
                Patient patient = null;
                while(resultSet.next()){
                    dbPassword = resultSet.getString("password");
                    Blob blob = resultSet.getBlob("salt");

                    patient = new Patient(resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getDate("birthday").toLocalDate(),
                            resultSet.getString("gender"));

                    patient.setImageFile(new File(resultSet.getString("imageFile")));
                    patient.setId(resultSet.getInt("patientID"));

                    int blobLength = (int) blob.length();
                    salt = blob.getBytes(1, blobLength);
                }

                String enteredPW = PWGenerator.getSHA512Password(passwordField.getText(), salt);

                SceneChanger sc = new SceneChanger();

                PatientMainView controllerClass = new PatientMainView();

                if (enteredPW.equals(dbPassword)){
                    sc.changeScenes(event, "patientMainView.fxml", "Hi", patient, controllerClass);
                } else {
                    errMsgLabel.setText("ID and Password do not match");
                }
            }
        } catch (SQLException | IOException | NoSuchAlgorithmException | NullPointerException e) {
            System.out.println(e.getMessage());
            errMsgLabel.setText("Something went wrong. Maybe your user doesn't exist. make sure to enter the login info correctly");
        }
    }

    public void registerBtnPushed(ActionEvent event) throws IOException {
        if (doctorCheckBox.isSelected()){
            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "newDoctorView.fxml", "Register as a new Doctor");
        } else {
            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "newUserView.fxml", "Register as a new Patient");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errMsgLabel.setText("");
    }
}
