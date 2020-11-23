package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
import java.sql.*;

public class viewMsgView implements ControllerClass {

    @FXML private Circle circleImage;
    @FXML private Label doctorNameLabel;
    @FXML private VBox vbox;

    private Person person;
    private File imageFile;

    public void logoutBtnPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "loginView.fxml", "Login");
    }

    public void msgBtnPushed(ActionEvent event) {
        //Nothing happens
    }

    public void postsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PostsView pv = new PostsView();

        sc.changeScenes(event, "postsView.fxml", "Posts", person, pv);
    }

    public void sendMsgBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        NewMsgView nmv = new NewMsgView();

        sc.changeScenes(event, "newMsgView.fxml", "New message", person, nmv);
    }

    public void visitsBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DoctorMainView dmv = new DoctorMainView();

        sc.changeScenes(event, "doctorMainView.fxml","Welcome" ,person ,dmv);
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

            String sql = "SELECT * FROM messages WHERE receiverID=? and isTheSenderADoctor=false ";


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
                fromWho.setText("From: " + " " + f.getPatientNameFromDB(resultSet.getInt("senderID")));

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
}
