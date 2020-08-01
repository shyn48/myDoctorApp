package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.DB;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewPostView implements ControllerClass {

    @FXML private Circle circleImage;
    @FXML private TextField subjectTextField;
    @FXML private TextArea textArea;
    @FXML private Label errMsgLabel;

    private Person person;
    private File imageFile;

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DoctorMainView dmv = new DoctorMainView();

        sc.changeScenes(event, "doctorMainView.fxml", "Welcome", person, dmv);
    }

    public void sendBtnPushed(ActionEvent event) throws IOException, SQLException {
        errMsgLabel.setText("");
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "INSERT INTO socialSpace (postSubject, postDescription) VALUES (?,?)";

            preparedStatement = conn.prepareStatement(sql);

            if (subjectTextField.getText().isEmpty() || textArea.getText().isEmpty()){
                errMsgLabel.setText("Please provide all the needed info");
                return;
            }

            preparedStatement.setString(1, subjectTextField.getText());
            preparedStatement.setString(2, textArea.getText());

            preparedStatement.executeUpdate();

        } catch (SQLException | NullPointerException e){
            System.out.println(e.getMessage());
            return;
        }

        SceneChanger sc = new SceneChanger();

        viewMsgView vmv = new viewMsgView();

        sc.changeScenes(event, "doctorMainView.fxml", "View your messages", person, vmv);
    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
        errMsgLabel.setText("");
        textArea.setWrapText(true);
        this.person = person;
        try {
            String imgLocation = "./src/img/avatars/" + person.getImageFile().getName();
            imageFile  = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            circleImage.setFill(new ImagePattern(img));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
