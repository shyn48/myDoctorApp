package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Doctor;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class DoctorProfileView implements ControllerClass {


    @FXML private Circle circleImage;
    @FXML private Label doctorFirstName;
    @FXML private Label doctorLastName;
    @FXML private Label doctorAge;
    @FXML private Label doctorSpeciality;

    private Doctor doctor;

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
    public void preLoadData(Person person) throws IOException, SQLException {
        this.doctor = (Doctor) person;
        LocalDate now = LocalDate.now();
        System.out.println(doctor.getBirthDay());
        int age = Period.between(doctor.getBirthDay(), now).getYears();

        setCirclePic(doctor.getImageFile(), circleImage);
        doctorFirstName.setText("First Name: " + doctor.getFirstName());
        doctorLastName.setText("Last Name: " + doctor.getLastName());
        doctorAge.setText(Integer.toString(age));
        doctorSpeciality.setText(doctor.getSpeciality());
    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
