package models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;

public abstract class Person {
    private int id;
    private String firstName, lastName, email, password;
    private LocalDate birthDay;
    private File imageFile;
    private byte[] salt;

    public Person(String firstName, String lastName, String email, String password, LocalDate birthDay) throws NoSuchAlgorithmException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
        setImageFile(new File("./src/img/default.jpg"));
        salt = PWGenerator.getSalt();
        this.password = PWGenerator.getSHA512Password(password, salt);
    }

    public Person(String firstName, String lastName, String email, String password, LocalDate birthDay, File imageFile) throws NoSuchAlgorithmException, IOException {
        this(firstName, lastName, email, password, birthDay);
        setImageFile(imageFile);
        copyImageFile();
    }

    public void copyImageFile() throws IOException {
        Path sourcePath = imageFile.toPath();

        String uniqueFileName = getUniqueFileName(imageFile.getName());

        Path targetPath = Paths.get("./src/img/avatars/"+uniqueFileName);

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        imageFile = new File(targetPath.toString());
    }

    public String getUniqueFileName(String oldFileName){
        String newName;

        SecureRandom rng = new SecureRandom();

        //loop until we have a unique file name
        do {
            newName = "";

            for(int i = 1; i <=32; i++){
                int nextChar;

                do{
                    nextChar = rng.nextInt(123);
                }while(!validCharacterValue(nextChar));
                newName = String.format("%s%c", newName, nextChar);
            }
            newName += oldFileName;

        } while(!uniqueFileInDirectory(newName));

        return newName;
    }

    private boolean uniqueFileInDirectory(String fileName) {
        File directory = new File("./src/img/avatars");

        File[] dir_contents = directory.listFiles();

        for (File file: dir_contents){
            if(file.getName().equals(fileName))
                return false;
        }
        return true;
    }

    public boolean validCharacterValue(int asciiValue){
        //0-9 = ASCII range 48 to 57
        if(asciiValue >= 48 && asciiValue <= 57)
            return true;
        if(asciiValue >= 65 && asciiValue <= 90)
            return true;
        if(asciiValue >= 97 && asciiValue <= 122)
            return true;
        return false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    protected abstract void insertIntoDB() throws SQLException;

    protected abstract int getIDFromDB() throws SQLException;

    public abstract void updatePerson() throws SQLException;
}
