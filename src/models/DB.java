package models;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DB {

    public String getDocNameFromDB(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String fullName = "";

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "SELECT firstName, lastName FROM doctors WHERE doctorID=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                fullName = resultSet.getString("firstName") + " " + resultSet.getString("lastName");
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }

        return fullName;
    }

    public String getPatientNameFromDB(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String fullName = "";

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "SELECT firstName, lastName FROM patients WHERE patientID=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                fullName = resultSet.getString("firstName") + " " + resultSet.getString("lastName");
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }

        return fullName;
    }

    public Patient getPatientObjectFromDB(int id) throws SQLException{
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String fullName = "";

        Patient patient = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "SELECT * FROM patients WHERE patientID=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                patient = new Patient(resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getDate("birthday").toLocalDate(),
                        resultSet.getString("gender"));

                patient.setImageFile(new java.io.File(resultSet.getString("imageFile")));
                patient.setWeight(resultSet.getInt("weight"));
                patient.setId(resultSet.getInt("patientID"));
            }

        } catch (SQLException | NoSuchAlgorithmException e){
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }

        return patient;
    }

    public int getIDFromDB(String email, boolean isDoctor) throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id = 0;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");
            String sql;
            if (!isDoctor){
                sql = "SELECT doctorID FROM doctors WHERE email=?";
            } else {
                sql = "SELECT patientID FROM patients WHERE email=?";
            }

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (!isDoctor){
                    id = resultSet.getInt("doctorID");
                } else {
                    id = resultSet.getInt("patientID");
                }
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }

        return id;
    }

    public void insertOutDatedDrugToDelayedDrugTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root"); Statement statement = conn.createStatement()) {

            statement.executeUpdate("INSERT INTO delayedDrugs (SELECT drugName, amount, patientID FROM drugs WHERE now() >= drugTime and curdate() >= drugDate)");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteOutDatedDrug() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root"); Statement statement = conn.createStatement()) {

            statement.executeUpdate("delete from drugs where now() >= drugTime and curdate() >= drugDate;");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeDrugFromDB(Drug drug, int id) {
        try {
            Connection conn = null;
            PreparedStatement preparedStatement = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "DELETE FROM drugs WHERE drugName=? AND patientID=?;";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, drug.getDrugName());
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeDelayedDrugs(Drug drug, int id) {
        try {
            Connection conn = null;
            PreparedStatement preparedStatement = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "root");

            String sql = "DELETE FROM delayedDrugs WHERE drugName=? AND patientID=?;";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, drug.getDrugName());
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
