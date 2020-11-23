package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.DB;
import models.Drug;
import models.Patient;
import models.Person;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DrugsTableView implements ControllerClass, Initializable {

        @FXML private TableView<Drug> table;
        @FXML private TableColumn<Drug, String> nameCol;
        @FXML private TableColumn<Drug, Integer> amountCol;
        @FXML private TableColumn<Drug, LocalDate> dateCol;
        @FXML private TableColumn<Drug, String> timeCol;

        @FXML private Button takeDrugBtn;

        private Person person;

        public void addDrugBtnPushed(ActionEvent event) throws IOException, SQLException {
            SceneChanger sc = new SceneChanger();
            AddDrugView adv = new AddDrugView();
            sc.changeScenes(event, "addDrugView.fxml", "Add a new drug", person, adv);
        }

        public void userSelected(MouseEvent event) {
            takeDrugBtn.setDisable(false);
        }

        public void takeDrugBtnPushed(ActionEvent event) throws SQLException {
            Drug drug = this.table.getSelectionModel().getSelectedItem();
            DB db = new DB();
            db.removeDrugFromDB(drug, person.getId());
            loadDrugs(person.getId());
        }

        public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);

    }

        public void delayedDrugsBtnPushed(ActionEvent event) throws IOException, SQLException {
            SceneChanger sc = new SceneChanger();

            DelayedDrugs dd = new DelayedDrugs();

            sc.openNewWindow("delayedDrugs.fxml", "View your delayed drugs", person, dd);
        }

        private void loadDrugs(int id) throws SQLException {
        ObservableList<Drug> drugs = FXCollections.observableArrayList();
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root","root");

            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM drugs WHERE patientID=" + id);

            while (resultSet.next()){
                Drug drug = new Drug(resultSet.getString("drugName")
                        , resultSet.getInt("amount")
                        , resultSet.getInt("patientID")
                        , resultSet.getDate("drugDate").toLocalDate()
                        , resultSet.getTime("drugTime").toString());
                drugs.add(drug);
            }
            table.getItems().clear();
            table.getItems().addAll(drugs);

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

        @Override
        public void preLoadData(Person person) throws IOException, SQLException {
            this.person = person;
            loadDrugs(person.getId());
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
        takeDrugBtn.setDisable(true);

        nameCol.setCellValueFactory(new PropertyValueFactory<Drug, String>("drugName"));
        amountCol.setCellValueFactory(new PropertyValueFactory<Drug, Integer>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<Drug, LocalDate>("dueDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<Drug, String>("time"));

        DB db = new DB();
        db.insertOutDatedDrugToDelayedDrugTable();
        db.deleteOutDatedDrug();
    }

        @Override
        public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

        }
}
