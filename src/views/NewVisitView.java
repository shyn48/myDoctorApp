package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import models.Doctor;
import models.Patient;
import models.Person;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;

public class NewVisitView implements ControllerClass, Initializable {

    @FXML private ComboBox<String> doctorFieldComboBox;
    @FXML private ComboBox<Doctor> doctorNameComboBox;
    @FXML private TextField subjectTextField;
    @FXML private TextArea textArea;
    @FXML private Circle circleImage;
    @FXML private Label errMsgLabel;

    private Person person;
    private File imageFile;

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);
    }

    public void sendBtnPushed(ActionEvent event) throws IOException, SQLException {
        errMsgLabel.setText("");
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        Doctor selectedDoctor = doctorNameComboBox.getSelectionModel().getSelectedItem();

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "INSERT INTO visits (issue, issueDescription, patientID, doctorID) VALUES (?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            if (subjectTextField.getText().isEmpty() || textArea.getText().isEmpty()){
                errMsgLabel.setText("Please provide all the needed info");
                return;
            }

            preparedStatement.setString(1, subjectTextField.getText());
            preparedStatement.setString(2, textArea.getText());
            preparedStatement.setInt(3, person.getId());
            preparedStatement.setInt(4, selectedDoctor.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException  | NullPointerException e){
            System.out.println(e.getMessage());
            errMsgLabel.setText("Please fill out all the needed info");
            return;
        }

        SceneChanger sc = new SceneChanger();

        PatientMainView pmv = new PatientMainView();

        sc.changeScenes(event, "patientMainView.fxml", "Welcome", person, pmv);

    }

    public void getSpecialDoctorFromDB(String speciality) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ObservableList<Doctor> doctorObservableList = FXCollections.observableArrayList();;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "SELECT * FROM doctors WHERE speciality=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, speciality);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Doctor doctor = new Doctor(resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getDate("birthday").toLocalDate(),
                        resultSet.getString("speciality"));

                doctor.setId(resultSet.getInt("doctorID"));
                System.out.println(doctor.getId());
                doctorObservableList.add(doctor);
            }
            doctorNameComboBox.getItems().clear();
            doctorNameComboBox.getItems().addAll(doctorObservableList);
        } catch (SQLException  | NoSuchAlgorithmException | IOException e){
            e.printStackTrace();
        }
    }

    public void comboBoxChanged(){
        String speciality = doctorFieldComboBox.getSelectionModel().getSelectedItem();
        getSpecialDoctorFromDB(speciality);

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

    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doctorFieldComboBox.getItems().addAll("Cardiology", "Neurology", "General practitioner");
        textArea.setWrapText(true);
        errMsgLabel.setText("");

        Callback<ListView<Doctor>, ListCell<Doctor>> factory = lv -> new ListCell<Doctor>(){
            @Override
            protected void updateItem(Doctor item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFirstName() + " " + item.getLastName());
            }
        };

        doctorNameComboBox.setCellFactory(factory);
        doctorNameComboBox.setButtonCell(factory.call(null));
    }
}
