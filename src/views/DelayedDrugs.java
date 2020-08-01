package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.DB;
import models.Drug;
import models.Patient;
import models.Person;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DelayedDrugs implements ControllerClass{

    @FXML private TableView<Drug> table;
    @FXML private TableColumn<Drug, String> nameCol;
    @FXML private TableColumn<Drug, Integer> amountCol;
    @FXML private TableColumn<Drug, Integer> dateCol;
    @FXML private Button takeDrugBtn;

    private Person person;

    public void takeDrugBtnPushed(ActionEvent event) throws SQLException {
        Drug drug = this.table.getSelectionModel().getSelectedItem();
        DB db = new DB();
        db.removeDelayedDrugs(drug, person.getId());
        loadDrugs(person.getId());
    }

    public void userSelected(MouseEvent event) {
        takeDrugBtn.setDisable(false);
    }

    public void loadDrugs(int id) throws SQLException {
        ObservableList<Drug> drugs = FXCollections.observableArrayList();
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root","Codename48");

            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM delayedDrugs WHERE patientID=" + id);

            while (resultSet.next()){
                Drug drug = new Drug(resultSet.getString("drugName")
                        , resultSet.getInt("amount")
                        , resultSet.getInt("patientID"));
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

        nameCol.setCellValueFactory(new PropertyValueFactory<Drug, String>("drugName"));
        amountCol.setCellValueFactory(new PropertyValueFactory<Drug, Integer>("amount"));

        this.person = person;

        takeDrugBtn.setDisable(true);

        loadDrugs(person.getId());

    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
