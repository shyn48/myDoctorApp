package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Patient;
import models.Person;
import models.DB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PatientMainView implements ControllerClass, Initializable {

    @FXML private Circle circleImage;
    @FXML private Label patientNameLabel;
    @FXML private VBox vbox;

    private Person person;
    private File imageFile;

    public void drugBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DrugsTableView dtv = new DrugsTableView();

        sc.changeScenes(event, "drugsTableView.fxml", "View your drugs", person, dtv);
    }

    public void weightBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        WeightView wv = new WeightView();

        sc.changeScenes(event, "weightView.fxml", "Weight management" ,person, wv);
    }

    public void logoutBtnPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "loginView.fxml", "Login");
    }

//    public void checkForAlarm() {
//        DB db = new DB();
//        while (drugTableIsNotEmpty()){
//            if (db.removeDelayedDrugs() != 0){
//                //Show alarm dialog box
//            }
//        }
//    }

    public void msgBtnPushed(ActionEvent event) {
        //Nothing happens
    }

    public void sendMsgBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        NewMsgView nmv = new NewMsgView();

        sc.changeScenes(event, "newMsgView.fxml", "New message", person, nmv);
    }

    public void postsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PostsViewPatient pvp = new PostsViewPatient();

        sc.changeScenes(event, "postsViewPatient.fxml", "Social Space", person, pvp);
    }

    public void visitsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        NewVisitView nvv = new NewVisitView();

        sc.changeScenes(event, "newVisitView.fxml", "New Visit", person, nvv);
    }

    public void viewAllDoctorsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        ViewAllDoctorsView vdv = new ViewAllDoctorsView();

        sc.changeScenes(event, "viewAllDoctorsView.fxml", "Search and view doctors profiles", person, vdv);

    }

    public void editProfileBtnPushed(MouseEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        EditProfileView epv = new EditProfileView();

        sc.changeScenes(event, "editProfileView.fxml", "Edit profile", person, epv);
    }

    @Override
    public void preLoadData(Person person) throws IOException {
        this.person = person;
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

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "SELECT * FROM messages WHERE receiverID=? and isTheSenderADoctor=true";


            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, person.getId());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                HBox hBox = new HBox();
                Label messageSubject = new Label();
                Label messageDescription = new Label();
                Label fromWho = new Label();
                VBox theOtherVBox = new VBox();

                DB f = new DB();

                messageSubject.setText("Subject: " + resultSet.getString("subject") + "  ");
                messageDescription.setText(resultSet.getString("description") + "   ");
                fromWho.setText("From: " + " " + f.getDocNameFromDB(resultSet.getInt("senderID")));

                hBox.setPrefHeight(100);
                hBox.setPrefWidth(420);
                hBox.setAlignment(Pos.CENTER_LEFT);
                messageSubject.setWrapText(true);
                messageDescription.setWrapText(true);
                fromWho.setWrapText(true);
                messageSubject.setStyle("-fx-font: 24 arial;");
                messageDescription.setStyle("-fx-font: 14 arial;");


                fromWho.setStyle("-fx-font: 14 arial;");

                messageSubject.setStyle("-fx-text-fill: white");
                messageDescription.setStyle("-fx-text-fill: white");
                fromWho.setStyle("-fx-text-fill: white");

                hBox.getStyleClass().addAll("theHBox");
                theOtherVBox.getChildren().addAll(messageSubject, messageDescription, fromWho);
                hBox.getChildren().addAll(theOtherVBox);
                vbox.getChildren().addAll(hBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("User has no messages");
        }
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        DB db = new DB();
        db.insertOutDatedDrugToDelayedDrugTable();
        db.deleteOutDatedDrug();

    }
}
