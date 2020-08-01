package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import models.Doctor;
import models.Drug;
import models.Patient;
import models.Person;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewAllDoctorsView implements Initializable, ControllerClass {


    @FXML private TableView<Doctor> table;
    @FXML private TableColumn<Doctor, String> firstNameCol;
    @FXML private TableColumn<Doctor, String> lastNameCol;
    @FXML private TableColumn<Doctor, String> emailCol;
    @FXML private Button viewBtn;
    @FXML private TextField searchTerm;

    private Person person;

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);
    }

    public void loadDoctors(KeyEvent event) throws SQLException {

        ObservableList<Doctor> doctors = FXCollections.observableArrayList();
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root","Codename48");

            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM doctors WHERE firstName LIKE '%" + searchTerm.getText() + "%' OR lastName LIKE '%" + searchTerm.getText() + "%' or email LIKE '%" + searchTerm.getText() + "%';");

            while (resultSet.next()){
                Doctor doctor = new Doctor(resultSet.getString("firstName")
                        , resultSet.getString("lastName")
                        , resultSet.getString("email")
                        , resultSet.getString("password")
                        , resultSet.getDate("birthday").toLocalDate()
                        , resultSet.getString("speciality"));

                doctors.add(doctor);
            }
            table.getItems().clear();
            table.getItems().addAll(doctors);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }finally {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
    }

    public void loadDoctorsInit() throws SQLException{
        ObservableList<Doctor> doctors = FXCollections.observableArrayList();
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root","Codename48");

            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM doctors WHERE firstName LIKE '%" + searchTerm.getText() + "%' OR lastName LIKE '%" + searchTerm.getText() + "%' or email LIKE '%" + searchTerm.getText() + "%';");

            while (resultSet.next()){
                Doctor doctor = new Doctor(resultSet.getString("firstName")
                        , resultSet.getString("lastName")
                        , resultSet.getString("email")
                        , resultSet.getString("password")
                        , resultSet.getDate("birthday").toLocalDate()
                        , resultSet.getString("speciality"));
                doctor.setImageFile(new File (resultSet.getString("imageFile")));
                doctors.add(doctor);
            }
            table.getItems().clear();
            table.getItems().addAll(doctors);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }finally {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
    }

    public void userSelected(MouseEvent event) {
        viewBtn.setDisable(false);
    }

    public void viewDoctorProfileBtnPushed(ActionEvent event) throws IOException, SQLException {
        Doctor doctor = table.getSelectionModel().getSelectedItem();

        SceneChanger sc = new SceneChanger();

        DoctorProfileView dpv = new DoctorProfileView();

        sc.openNewWindow("doctorProfileView.fxml", "Doctor Profile", doctor, dpv);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewBtn.setDisable(true);

        firstNameCol.setCellValueFactory(new PropertyValueFactory<Doctor, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Doctor, String>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<Doctor, String>("email"));

        try {
            loadDoctorsInit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
        this.person = person;
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
