package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class WeightView implements ControllerClass, Initializable {

    @FXML private Circle circleImage;
    @FXML private Label patientNameLabel;
    @FXML private DatePicker datePicker;
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label errMsgLabel;
    @FXML private Spinner<Integer> weightSpinner;

    private Patient patient;
    private File imageFile;

    public void drugBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DrugsTableView dtv = new DrugsTableView();

        sc.changeScenes(event, "drugsTableView.fxml", "View your drugs", patient, dtv);
    }

    public void logoutBtnPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "loginView.fxml", "Login");
    }

    public void msgBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc  = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome" ,patient, pmv);
    }

    public void postsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PostsViewPatient pvp = new PostsViewPatient();

        sc.changeScenes(event, "postsViewPatient.fxml", "Social Space", patient, pvp);
    }

    public void setWeightBtnPushed(ActionEvent event) throws SQLException {

        patient.setWeight( (int) weightSpinner.getValue());
        patient.updateWeightInDB();
        Connection conn = null;
        PreparedStatement ps = null;

        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false","root", "root");

            String sql = "INSERT INTO weightData (patientID, weight, dateSubmitted) VALUES (?,?,?)";

            ps = conn.prepareStatement(sql);

            Date dbDate = Date.valueOf(datePicker.getValue());

            if (datePicker.getValue() == null){
                errMsgLabel.setText("Please provide all the required info");
                return;
            }

            ps.setInt(1, patient.getId());
            ps.setInt(2, patient.getWeight());
            ps.setDate(3, dbDate);

            ps.executeUpdate();

            errMsgLabel.setText("Your weight data has been updated");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();

        }
    }

    public void visitsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        NewVisitView nvv = new NewVisitView();

        sc.changeScenes(event, "newVisitView.fxml", "New Visit", patient, nvv);
    }

    public void weightBtnPushed(ActionEvent event) {
        //Nothing happens
    }

    public void weightChartBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        WeightChartView wcv = new WeightChartView();

        sc.openNewWindow("weightChartView.fxml","See your weight chart",patient, wcv);
    }

    public void editProfileBtnPushed(MouseEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        EditProfileView epv = new EditProfileView();

        sc.changeScenes(event, "editProfileView.fxml", "Edit profile", patient, epv);
    }

    public void viewAllDoctorsBtnPushed(ActionEvent event) throws IOException, SQLException {

        SceneChanger sc = new SceneChanger();

        ViewAllDoctorsView vdv = new ViewAllDoctorsView();

        sc.changeScenes(event, "viewAllDoctorsView.fxml", "Search and view doctors profiles", patient, vdv);

    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
        this.patient = (Patient) person;

        try {
            String imgLocation = "./src/img/avatars/" + person.getImageFile().getName();
            imageFile  = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            circleImage.setFill(new ImagePattern(img));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        patientNameLabel.setText(person.getFirstName() + " " + person.getLastName());
        nameLabel.setText(person.getFirstName() + " " + person.getLastName());
        idLabel.setText(person.getEmail());
        errMsgLabel.setText("");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 200, 50);
        weightSpinner.setValueFactory(valueFactory);
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
