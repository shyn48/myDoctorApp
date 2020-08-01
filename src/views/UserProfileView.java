package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

public class UserProfileView implements ControllerClass{

    @FXML private Circle circleImage;
    @FXML private Label patientNameLabel;
    @FXML private Label patientLastNameLabel;
    @FXML private Label patientAge;
    @FXML private Label patientGender;
    @FXML private Label patientWeight;

    private Patient patient;

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
    public void preLoadData(Person person) throws IOException {
        this.patient = (Patient) person;
        LocalDate now = LocalDate.now();
        System.out.println(patient.getBirthDay());
        int age = Period.between(patient.getBirthDay(), now).getYears();


        setCirclePic(patient.getImageFile(), circleImage);
        patientNameLabel.setText("First Name: " + patient.getFirstName());
        patientLastNameLabel.setText("Last Name: " + patient.getLastName());
        patientAge.setText(Integer.toString(age));
        patientGender.setText(patient.getGender());
        patientWeight.setText(Integer.toString(patient.getWeight()));

    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
