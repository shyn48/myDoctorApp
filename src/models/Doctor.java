package models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;

public class Doctor extends Person {

    String speciality;
    File idCardPic;

    public Doctor(String firstName, String lastName, String email, String password, LocalDate birthDay, String speciality) throws NoSuchAlgorithmException, IOException {
        super(firstName, lastName, email, password, birthDay);
        this.speciality = speciality;
    }

    public Doctor(String firstName, String lastName, String email, String password, LocalDate birthDay, File imageFile, String speciality, File idCardPic) throws NoSuchAlgorithmException, IOException {
        super(firstName, lastName, email, password, birthDay, imageFile);
        this.speciality = speciality;
        this.idCardPic = idCardPic;
        copyIdCardPic();
    }

    public Doctor(String firstName, String lastName, String email, String password, LocalDate birthDay, String speciality, File idCardPic) throws NoSuchAlgorithmException, IOException {
        super(firstName, lastName, email, password, birthDay);
        this.speciality = speciality;
        this.idCardPic = idCardPic;
        copyIdCardPic();
    }


    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public File getIdCardPic() {
        return idCardPic;
    }

    public void setIdCardPic(File idCardPic) {
        this.idCardPic = idCardPic;
    }

    public void copyIdCardPic() throws IOException {
        Path sourcePath = idCardPic.toPath();

        String uniqueFileName = getUniqueFileName1(idCardPic.getName());

        Path targetPath = Paths.get("./src/img/idCardPic/"+uniqueFileName);

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        idCardPic = new File(targetPath.toString());
    }

    public String getUniqueFileName1(String oldFileName){
        String newName;

        SecureRandom rng = new SecureRandom();

        //loop until we have a unique file name
        do {

            newName = "";

            for(int i = 1; i <=32; i++){
                int nextChar;

                do{
                    nextChar = rng.nextInt(123);
                }while(!validCharacterValue1(nextChar));
                newName = String.format("%s%c", newName, nextChar);
            }
            newName += oldFileName;

        } while(!uniqueFileInDirectory1(newName));

        return newName;
    }

    private boolean uniqueFileInDirectory1(String fileName) {
        File directory = new File("./src/img/idCardPic");

        File[] dir_contents = directory.listFiles();

        for (File file: dir_contents){
            if(file.getName().equals(fileName))
                return false;
        }
        return true;
    }

    public boolean validCharacterValue1(int asciiValue){
        //0-9 = ASCII range 48 to 57
        if(asciiValue >= 48 && asciiValue <= 57)
            return true;
        if(asciiValue >= 65 && asciiValue <= 90)
            return true;
        if(asciiValue >= 97 && asciiValue <= 122)
            return true;
        return false;
    }

    @Override
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "INSERT INTO doctors (firstName, lastName, email, birthday, imageFile, password, salt, cardPicture, speciality)" + "VALUES (?,?,?,?,?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);

            Date db = Date.valueOf(this.getBirthDay());

            preparedStatement.setString(1, this.getFirstName());
            preparedStatement.setString(2, this.getLastName());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setDate(4, db);
            preparedStatement.setString(5, this.getImageFile().getName());
            preparedStatement.setString(6, this.getPassword());
            preparedStatement.setBlob(7, new javax.sql.rowset.serial.SerialBlob(this.getSalt()));
            preparedStatement.setString(8, idCardPic.getName());
            preparedStatement.setString(9, speciality);

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

            String sql = "SELECT doctorID FROM doctors WHERE email=?";

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
        System.out.println(id);
        return id;
    }

    @Override
    public void updatePerson() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorApp?useSSL=false", "root", "Codename48");

            String sql = "UPDATE doctors SET firstName = ?, lastName = ?, email = ?, birthday = ?, imageFile = ?" + "WHERE doctorID = ?";

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
