package model; // tells Java this file belongs to the "model" package (folder)

// ============================================================
// Scheme.java - Model class representing a government scheme
// Each object of this class holds details of ONE scheme,
// matching one row in the 'schemes' table.
// ============================================================

public class Scheme {

    private int schemeId;
    private String schemeName;
    private String description;
    private int minAge;
    private int maxAge;
    private String profession;    // e.g. "Farmer", "Student", "Any"
    private String state;         // e.g. "Maharashtra", "All" (for central schemes)
    private double incomeLimit;   // annual income limit in rupees; 0 = no limit
    private String educationLevel; // e.g. "10th Pass", "Graduate", "Any"
    private String category;      // e.g. "Agriculture", "Education", "Health"
    private String benefits;      // short description of what the scheme offers
    private String applicationLink; // official URL to apply

    // --------------------------------------------------------
    // Default constructor
    // --------------------------------------------------------
    public Scheme() {
    }

    // --------------------------------------------------------
    // Parameterized constructor
    // --------------------------------------------------------
    public Scheme(int schemeId, String schemeName, String description,
                  int minAge, int maxAge, String profession, String state,
                  double incomeLimit, String educationLevel, String category,
                  String benefits, String applicationLink) {
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.description = description;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.profession = profession;
        this.state = state;
        this.incomeLimit = incomeLimit;
        this.educationLevel = educationLevel;
        this.category = category;
        this.benefits = benefits;
        this.applicationLink = applicationLink;
    }

    // --------------------------------------------------------
    // Getters
    // --------------------------------------------------------
    public int getSchemeId() {
        return schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public String getDescription() {
        return description;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getProfession() {
        return profession;
    }

    public String getState() {
        return state;
    }

    public double getIncomeLimit() {
        return incomeLimit;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public String getCategory() {
        return category;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getApplicationLink() {
        return applicationLink;
    }

    // --------------------------------------------------------
    // Setters
    // --------------------------------------------------------
    public void setSchemeId(int schemeId) {
        this.schemeId = schemeId;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setIncomeLimit(double incomeLimit) {
        this.incomeLimit = incomeLimit;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public void setApplicationLink(String applicationLink) {
        this.applicationLink = applicationLink;
    }

    // --------------------------------------------------------
    // toString() — useful when printing scheme details
    // --------------------------------------------------------
    @Override
    public String toString() {
        return "Scheme{" +
                "schemeId=" + schemeId +
                ", schemeName='" + schemeName + '\'' +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", incomeLimit=" + incomeLimit +
                '}';
    }
}
