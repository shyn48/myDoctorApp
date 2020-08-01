package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Patient;
import models.Person;
import models.DB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class ReplyVisitView implements ControllerClass{

    private Person person;
    private int visitID;
    private Patient patient;
    private File imageFile;


    @FXML private Circle circleImage;
    @FXML private Circle patientProfile;
    @FXML private Label issueLabel;
    @FXML private Label descriptionLabel;
    @FXML private TextArea textArea;

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DoctorMainView dmv = new DoctorMainView();

        sc.changeScenes(event, "doctorMainView.fxml", "Welcome", person, dmv);
    }

    public void sendBtnPushed(ActionEvent event) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        DB f = new DB();

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "INSERT INTO messages (subject, description, senderID, receiverID, isTheSenderADoctor) VALUES (?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, "About your visit: " + issueLabel.getText());
            preparedStatement.setString(2, textArea.getText());
            preparedStatement.setInt(3, person.getId());
            preparedStatement.setInt(4, patient.getId());
            preparedStatement.setBoolean(5, true);

            preparedStatement.executeUpdate();

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }

        updateVisitStatus();

        SceneChanger sc = new SceneChanger();

        DoctorMainView dmv = new DoctorMainView();

        sc.changeScenes(event, "doctorMainView.fxml", "Welcome", person, dmv);

    }

    public void updateVisitStatus() {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "UPDATE visits SET wasReplied=? WHERE visitID=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, visitID);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showPatientProfile(MouseEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        UserProfileView upv = new UserProfileView();

        sc.openNewWindow("userProfileView.fxml", "Patient Profile", patient, upv);
    }

    public void getVisitFromDB(int visitID){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "SELECT * FROM visits WHERE visitID=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, visitID);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                issueLabel.setText(resultSet.getString("issue"));
                descriptionLabel.setText(resultSet.getString("issueDescription"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setCirclePic(File imageFile, Circle circleImage) {
        try {
            String imgLocation = "./src/img/avatars/" + imageFile.getName();
            imageFile = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            circleImage.setFill(new ImagePattern(img));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {
        this.person = person;
        this.visitID = visitID;
        this.patient = patient;

        setCirclePic(person.getImageFile(), circleImage);
        setCirclePic(patient.getImageFile(), patientProfile);
        getVisitFromDB(visitID);
    }

    @Override
    public void preLoadData(Person person) throws IOException {

    }
}
