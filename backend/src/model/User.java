
package model;

public class User {

    private int userId;
    private int accountId;
    private String name;
    private int age;
    private String state;
    private String profession;
    private double income;
    private String educationLevel;
    private String gender;
    private String maritalStatus;

    // Default constructor
    public User() {
    }

    // Original full constructor
    public User(int userId, String name, int age, String state,
            String profession, double income,
            String educationLevel, String gender,
            String maritalStatus) {

        this.userId = userId;
        this.name = name;
        this.age = age;
        this.state = state;
        this.profession = profession;
        this.income = income;
        this.educationLevel = educationLevel;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
    }

    // Original 7-parameter constructor
    public User(int userId, String name, int age, String state,
            String profession, double income,
            String educationLevel) {

        this(userId, name, age, state, profession, income,
                educationLevel, "Other", "Unmarried");
    }

    // User ID getter and setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Account ID getter and setter
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", state='" + state + '\'' +
                ", profession='" + profession + '\'' +
                ", income=" + income +
                ", educationLevel='" + educationLevel + '\'' +
                ", gender='" + gender + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                '}';
    }
}
