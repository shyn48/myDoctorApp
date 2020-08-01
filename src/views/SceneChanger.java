package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Patient;
import models.Person;


import java.io.IOException;
import java.sql.SQLException;

public class SceneChanger {

    private static Person loggedInUser;

    public void changeScenes(ActionEvent event, String viewName, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void changeScenes(MouseEvent event, String viewName, String title, Person person, ControllerClass controllerClass) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerClass = loader.getController();
        controllerClass.preLoadData(person);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void changeScenes(ActionEvent event, String viewName, String title, Person person, ControllerClass controllerClass) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerClass = loader.getController();
        controllerClass.preLoadData(person);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void changeScenes(ActionEvent event, String viewName, String title, Person person, int visitID, Patient patient, ControllerClass controllerClass) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerClass = loader.getController();
        controllerClass.preLoadData(person, visitID, patient);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }


    public void openNewWindow(String viewName, String title, Person person, ControllerClass controllerClass) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerClass = loader.getController();
        controllerClass.preLoadData(person);

        Stage stage = new Stage();

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static Person getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(Person loggedInUser) {
        SceneChanger.loggedInUser = loggedInUser;
    }

}
