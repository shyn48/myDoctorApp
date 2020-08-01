package views;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDrugView implements ControllerClass, Initializable {

    @FXML private Circle circleImage;
    @FXML private TextField drugNameTextField;
    @FXML private TextField amountTextField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeTextField;

    private Person person;
    private File imageFile;

    public boolean isNumeric(String string){
        try{
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public void addBtnPushed(ActionEvent event) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        if (!validateTime(timeTextField.getText())){
            System.out.println("Please enter a valid time");
            return;
        }

        if (!isNumeric(amountTextField.getText())){
            System.out.println("Invalid number in amount");
            return;
        }

        Time time = Time.valueOf(timeTextField.getText());

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "INSERT INTO drugs (drugName, amount, drugDate, patientID, drugTime) VALUES (?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            Date dbDate = Date.valueOf(datePicker.getValue());

            preparedStatement.setString(1, drugNameTextField.getText());
            preparedStatement.setInt(2, Integer.parseInt(amountTextField.getText()));
            preparedStatement.setDate(3, dbDate);
            preparedStatement.setInt(4, person.getId());
            preparedStatement.setTime(5, time);

            preparedStatement.executeUpdate();

        } catch (SQLException | NullPointerException e){
            System.out.println(e.getMessage());
        }

        SceneChanger sc = new SceneChanger();

        DrugsTableView dtv = new DrugsTableView();

        sc.changeScenes(event, "drugsTableView.fxml", "View your drugs", person, dtv);

    }

    public void backBtnPushed(ActionEvent event) throws IOException, SQLException {
        SceneChanger sc = new SceneChanger();

        DrugsTableView dtv = new DrugsTableView();

        sc.changeScenes(event, "drugsTableView.fxml", "View your drugs", person, dtv);

    }

    public boolean validateTime(String time) {
        String regex = "^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        return  matcher.matches();
    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
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
    }
}
