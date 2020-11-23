package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

public class DoctorMainView implements ControllerClass, Initializable {

    @FXML private Circle circleImage;
    @FXML private Label doctorNameLabel;
    @FXML private VBox vbox;

    private Person person;
    private File imageFile;

    public void logoutBtnPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "loginView.fxml", "Login");
    }

    public void msgBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        viewMsgView vmv = new viewMsgView();

        sc.changeScenes(event, "viewMsgsView.fxml", "View your messages", person, vmv);
    }

    public void postsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PostsView pv = new PostsView();

        sc.changeScenes(event, "postsView.fxml", "Posts", person, pv);
    }

    public void editProfileBtnPushed(MouseEvent event) {

    }

    public void visitsBtnPushed(ActionEvent event) {
        //Nothing happens
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

        doctorNameLabel.setText("Dr." + person.getLastName());

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "SELECT * FROM visits WHERE doctorID=? AND wasReplied=false";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, person.getId());

            resultSet = preparedStatement.executeQuery();


            while (resultSet.next()){
                HBox hBox = new HBox();
                Label messageSubject = new Label();
                Label messageDescription = new Label();
                Label fromWho = new Label();
                Button button = new Button();
                VBox theOtherVBox = new VBox();

                DB f = new DB();

                int visitID = resultSet.getInt("visitID");
                Patient patient = f.getPatientObjectFromDB(resultSet.getInt("patientID"));

                button.setOnAction(event -> {
                    SceneChanger sc = new SceneChanger();
                    ReplyVisitView rvv = new ReplyVisitView();
                    try {
                        sc.changeScenes(event, "replyVisitView.fxml", "Replying to a patients visit", person, visitID, patient, rvv);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                button.setText("Reply to this visit");
                messageSubject.setText("Issue: " + resultSet.getString("issue") + "  ");
                messageDescription.setText(resultSet.getString("issueDescription") + "   ");
                fromWho.setText("From: " + " " + f.getPatientNameFromDB(resultSet.getInt("patientID")));

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
                theOtherVBox.getChildren().addAll(messageSubject, messageDescription, fromWho, button);
                hBox.getChildren().addAll(theOtherVBox);
                vbox.getChildren().addAll(hBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
