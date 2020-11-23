package views;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.DB;
import models.Doctor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewDoctorView implements Initializable {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private PasswordField confirmPasswordTextField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> comboBox;
    @FXML private ImageView imageView;
    @FXML private Label errMsgLabel;

    private File imageFile;
    private File idCardFile;
    private Boolean idCardChanged;
    private Boolean imageFileChanged;
    private Doctor doctor;

    public void backBtnPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "loginView.fxml", "Login");
    }

    public void idCardBtnPushed(ActionEvent event) {
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
            idCardFile = tempImageFile;
            idCardChanged = true;
        }
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

    public boolean validPassword(){
        if (passwordTextField.getText().length() < 5) {
            errMsgLabel.setText("Password is too short");
            return false;
        }
        if (this.passwordTextField.getText().equals(this.confirmPasswordTextField.getText()))
            return true;
        else {
            errMsgLabel.setText("Passwords don't match");
            return false;
        }
    }

    public boolean validateEmail(String email){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean checkForDuplicateUser(String email) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isThereADuplicate = false;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root","root");

            String sql = "SELECT email FROM doctors WHERE doctors.email =? UNION ALL SELECT email FROM patients WHERE patients.email =?;";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, email);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                if (resultSet.getString("email").equals(email)) {
                    isThereADuplicate = true;
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return isThereADuplicate;

    }

    public boolean isFormComplete(){
        return !(firstNameTextField.getText().trim().isEmpty() && lastNameTextField.getText().trim().isEmpty() && emailTextField.getText().trim().isEmpty() && datePicker.getValue() == null && comboBox.getSelectionModel().isEmpty());
    }

    public void registerBtnPushed(ActionEvent event) {
        if (checkForDuplicateUser(emailTextField.getText())){
            errMsgLabel.setText("this email is already registered");
        } else {
            if (validPassword() && idCardChanged && isFormComplete() && validateEmail(emailTextField.getText())) {
                try {
                    if (imageFileChanged) {
                        doctor = new Doctor(firstNameTextField.getText(),
                                lastNameTextField.getText(),
                                emailTextField.getText(),
                                passwordTextField.getText(),
                                datePicker.getValue(),
                                imageFile,
                                comboBox.getSelectionModel().getSelectedItem(),
                                idCardFile);
                    } else {
                        doctor = new Doctor(firstNameTextField.getText(),
                                lastNameTextField.getText(),
                                emailTextField.getText(),
                                passwordTextField.getText(),
                                datePicker.getValue(),
                                comboBox.getSelectionModel().getSelectedItem(),
                                idCardFile);
                    }

                    doctor.insertIntoDB();
                    doctor.setId(doctor.getIDFromDB());

                    SceneChanger sc = new SceneChanger();

                    DoctorMainView dmv = new DoctorMainView();
                    sc.changeScenes(event, "doctorMainView.fxml", "Hi Doctor", doctor, dmv);
                } catch (SQLException | IOException | NoSuchAlgorithmException e) {
                    errMsgLabel.setText(e.getMessage());
                }
            } else {
                errMsgLabel.setText("Please provide all the needed information");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errMsgLabel.setText("");
        comboBox.getItems().addAll("Cardiology", "Neurology", "General practitioner");
    }
}
