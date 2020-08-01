package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import models.Patient;
import models.Person;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class WeightChartView implements ControllerClass, Initializable {

    @FXML private BarChart<?, ?> barChart;
    @FXML private CategoryAxis months;
    @FXML private NumberAxis weight;

    private XYChart.Series currentYearSeries, previousYearSeries;

    private Patient patient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearSeries = new XYChart.Series<>();
        previousYearSeries = new XYChart.Series<>();

        months.setLabel("Months");
        weight.setLabel("Weight");

        currentYearSeries.setName(Integer.toString(LocalDate.now().getYear()));
        previousYearSeries.setName(Integer.toString(LocalDate.now().getYear() - 1));

        barChart.getData().addAll(previousYearSeries);
        barChart.getData().addAll(currentYearSeries);
    }

    private void populateSeriesFromDB(int id) throws SQLException {

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp", "root", "Codename48");

            statement = conn.createStatement();

            String sql = "SELECT YEAR(dateSubmitted), MONTHNAME(dateSubmitted), (weight), (patientID) FROM weightData WHERE patientID=" + id + " GROUP BY YEAR(dateSubmitted), MONTH(dateSubmitted) ORDER BY YEAR(dateSubmitted), MONTH(dateSubmitted);";


            resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                if (resultSet.getInt(1) == LocalDate.now().getYear())
                    currentYearSeries.getData().add(new XYChart.Data(resultSet.getString(2), resultSet.getInt(3)));
                else if (resultSet.getInt(1) == LocalDate.now().getYear() - 1)
                    previousYearSeries.getData().add(new XYChart.Data(resultSet.getString(2), resultSet.getInt(3)));
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        } finally{
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }

    }

    @Override
    public void preLoadData(Person person) throws IOException, SQLException {
        this.patient = (Patient) person;

        try {
            populateSeriesFromDB(person.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void preLoadData(Person person, int visitID, Patient patient) throws IOException {

    }
}
