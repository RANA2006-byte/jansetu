package service; // tells Java this file belongs to the "service" package (folder)

// ============================================================
// RecommendationService.java - Business logic layer
//
// This class sits BETWEEN Main.java (the entry point) and
// the DAO classes (the database layer).
//
// Its job:
//   - Take a User object as input
//   - Apply matching logic to find suitable schemes
//   - Return a filtered list of Scheme objects
//
// Why a separate service class?
//   - Keeps recommendation logic in one place
//   - DAO handles DB; Service handles logic — clean separation
//
// NOTE: Recommendation logic will be implemented in next stage.
//       This is just the foundation/stub.
// ============================================================

import model.Scheme;
import model.User;

import java.util.List;

public class RecommendationService {

    // --------------------------------------------------------
    // getRecommendedSchemes()
    //   Input  : a User object with the applicant's details
    //   Output : a List of Scheme objects that match the user
    //
    // Matching will check:
    //   - age falls within scheme's minAge–maxAge range
    //   - profession matches (or scheme is open to "Any")
    //   - state matches (or scheme is "All" India level)
    //   - income is below scheme's incomeLimit
    //   - educationLevel meets the scheme's requirement
    // --------------------------------------------------------
    public List<Scheme> getRecommendedSchemes(User user) {
        // TODO: Implement matching logic in the next stage
        System.out.println("RecommendationService - Not implemented yet.");
        return null;
    }
}
