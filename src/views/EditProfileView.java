package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class EditProfileView implements ControllerClass {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private DatePicker datePicker;
    @FXML private ImageView imageView;
    @FXML private Label errMsgLabel;

    private Patient patient;
    private File imageFile;
    private boolean imageFileChanged;

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome", patient, pmv);

    }

    public void profilePicBtnPushed(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");

        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);

        String userDirString = System.getProperty("user.home")+"\\Pictures";
        File userDir = new File(userDirString);

        if(!userDir.canRead())
            userDir = new File(System.getProperty("user.home"));


        fileChooser.setInitialDirectory(userDir);

        File tempImageFile = fileChooser.showOpenDialog(stage);

        if(tempImageFile != null){
            imageFile = tempImageFile;
            if(imageFile.isFile()){
                try{
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(img);
                    imageFileChanged = true;
                }catch(IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public boolean isFormComplete(){
        return !(firstNameTextField.getText().trim().isEmpty() && lastNameTextField.getText().trim().isEmpty() && emailTextField.getText().trim().isEmpty() && datePicker.getValue() == null);
    }

    public void registerBtnPushed(ActionEvent event) {
        if (isFormComplete()) {
            try {
                if (imageFileChanged) {
                    patient.setFirstName(firstNameTextField.getText());
                    patient.setLastName(lastNameTextField.getText());
                    patient.setEmail(emailTextField.getText());
                    patient.setBirthDay(datePicker.getValue());
                    patient.setImageFile(imageFile);
                    patient.copyImageFile();
                } else {
                    patient.setFirstName(firstNameTextField.getText());
                    patient.setLastName(lastNameTextField.getText());
                    patient.setEmail(emailTextField.getText());
                    patient.setBirthDay(datePicker.getValue());
                }

                patient.updatePerson();

                patient.setId(patient.getIDFromDB());

                SceneChanger sc = new SceneChanger();

                PatientMainView pmv = new PatientMainView();

                sc.changeScenes(event, "patientMainView.fxml", "Welcome", patient, pmv);
            } catch (SQLException | IOException e) {
                errMsgLabel.setText(e.getMessage());
            }
        } else {
            errMsgLabel.setText("Please provide all the needed information");
        }
    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
        this.patient = (Patient) person;

        try {
            String imgLocation = "./src/img/avatars/" + person.getImageFile().getName();
            imageFile  = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        firstNameTextField.setText(person.getFirstName());
        lastNameTextField.setText(person.getLastName());
        emailTextField.setText(person.getEmail());
        datePicker.setValue(person.getBirthDay());

    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
