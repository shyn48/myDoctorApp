package models;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;

public class Patient extends  Person{

    String gender;
    int weight; //KG

    public Patient(String firstName, String lastName, String email, String password, LocalDate birthDay, String gender) throws NoSuchAlgorithmException {
        super(firstName, lastName, email, password, birthDay);
        this.gender = gender;
    }

    public Patient(String firstName, String lastName, String email, String password, LocalDate birthDay, File imageFile, String gender) throws NoSuchAlgorithmException, IOException {
        super(firstName, lastName, email, password, birthDay, imageFile);
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void updateWeightInDB() throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;

        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false","root", "Codename48");

            String sql = "UPDATE patients SET weight=? WHERE patientID=?";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, this.getWeight());
            ps.setInt(2, this.getId());

            ps.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();

        }
    }

    @Override
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "INSERT INTO patients (firstName, lastName, email, birthday, imageFile, password, salt, gender, weight)" + "VALUES (?,?,?,?,?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            Date db = Date.valueOf(this.getBirthDay());

            preparedStatement.setString(1, this.getFirstName());
            preparedStatement.setString(2, this.getLastName());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setDate(4, db);
            preparedStatement.setString(5, this.getImageFile().getName());
            preparedStatement.setString(6, this.getPassword());
            preparedStatement.setBlob(7, new javax.sql.rowset.serial.SerialBlob(this.getSalt()));
            preparedStatement.setString(8, gender);
            preparedStatement.setInt(9, weight);

            preparedStatement.executeUpdate();
        } catch (Exception e){
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }
    }

    @Override
    public int getIDFromDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id = 0;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "SELECT patientID FROM patients WHERE email=?";

            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, this.getEmail());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("patientID");
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

    @Override
    public void updatePerson() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "UPDATE patients SET firstName = ?, lastName = ?, email = ?, birthday = ?, imageFile = ?" + "WHERE patientID = ?";

            preparedStatement = conn.prepareStatement(sql);

            Date bd = Date.valueOf(this.getBirthDay());

            preparedStatement.setString(1, this.getFirstName());
            preparedStatement.setString(2, this.getLastName());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setDate(4, bd);
            preparedStatement.setString(5, this.getImageFile().getName());
            preparedStatement.setInt(6, this.getId());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }finally {
            if(conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }
    }

}
