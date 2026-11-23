package model; // tells Java this file belongs to the "model" package (folder)

// ============================================================
// User.java - Model class representing a user/applicant
// A "model" class just holds data — like a template for a row
// in the users table.
// ============================================================

public class User {

    // These fields match the columns in the 'users' table
    private int userId;
    private String name;
    private int age;
    private String state;
    private String profession;
    private double income;
    private String educationLevel;

    // --------------------------------------------------------
    // Default (no-argument) constructor
    // Required to create an empty User object before filling it
    // --------------------------------------------------------
    public User() {
    }

    // --------------------------------------------------------
    // Parameterized constructor — fill all fields at once
    // --------------------------------------------------------
    public User(int userId, String name, int age, String state,
                String profession, double income, String educationLevel) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.state = state;
        this.profession = profession;
        this.income = income;
        this.educationLevel = educationLevel;
    }

    // --------------------------------------------------------
    // Getters — read a field value from outside this class
    // --------------------------------------------------------
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getState() {
        return state;
    }

    public String getProfession() {
        return profession;
    }

    public double getIncome() {
        return income;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    // --------------------------------------------------------
    // Setters — write a field value from outside this class
    // --------------------------------------------------------
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    // --------------------------------------------------------
    // toString() — handy for printing the object during testing
    // --------------------------------------------------------
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", state='" + state + '\'' +
                ", profession='" + profession + '\'' +
                ", income=" + income +
                ", educationLevel='" + educationLevel + '\'' +
                '}';
    }
}
