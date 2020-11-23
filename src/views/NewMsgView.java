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
import models.Doctor;
import models.Patient;
import models.Person;
import models.DB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewMsgView implements ControllerClass {

    @FXML private Circle circleImage;
    @FXML private TextField subjectTextField;
    @FXML private TextField emailTextField;
    @FXML private TextArea textArea;
    @FXML private Label errMsgLabel;

    private Person person;
    private File imageFile;


    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        if (!person.getClass().getName().contains("Doctor")) {
            PatientMainView pmv = new PatientMainView();
            sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);
        } else {
            DoctorMainView dmv = new DoctorMainView();
            sc.changeScenes(event, "doctorMainView.fxml", "Welcome", person, dmv);
        }
    }

    public void sendBtnPushed(ActionEvent event) throws IOException, SQLException {
        errMsgLabel.setText("");
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        boolean isTheSenderADoctor = false;

        if (person.getClass().getName().contains("Doctor")){
            isTheSenderADoctor = true;
        }

        DB f = new DB();

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "INSERT INTO messages (subject, description, senderID, receiverID, isTheSenderADoctor) VALUES (?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            int receiverID = f.getIDFromDB(emailTextField.getText(), isTheSenderADoctor);

            System.out.println(receiverID);

            if (receiverID == 0){
                errMsgLabel.setText("User not found");
                emailTextField.setText("");
                return;
            }

            if (subjectTextField.getText().isEmpty() || textArea.getText().isEmpty()){
                errMsgLabel.setText("Please provide all the needed info");
                return;
            }

            preparedStatement.setString(1, subjectTextField.getText());
            preparedStatement.setString(2, textArea.getText());
            preparedStatement.setInt(3, person.getId());
            preparedStatement.setInt(4, receiverID);
            preparedStatement.setBoolean(5, isTheSenderADoctor);

            preparedStatement.executeUpdate();

        } catch (SQLException | NullPointerException e){
            System.out.println(e.getMessage());
            return;
        }

        SceneChanger sc = new SceneChanger();

        if (!isTheSenderADoctor){
            PatientMainView pmv = new PatientMainView();
            sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);
        } else {
            viewMsgView vmv = new viewMsgView();
            sc.changeScenes(event, "viewMsgsView.fxml", "View your messages", person, vmv);
        }

    }

    @Override
    public void preLoadData(Person person) throws IOException {
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
        }
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
