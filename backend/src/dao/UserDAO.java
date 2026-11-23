package dao; // tells Java this file belongs to the "dao" package (folder)

// ============================================================
// UserDAO.java - Data Access Object for the 'users' table
//
// DAO = Data Access Object
// This class is the ONLY place where SQL queries related to
// the 'users' table are written. The rest of the application
// never writes raw SQL — it just calls methods here.
//
// NOTE: This file is set up as a foundation only.
//       Actual SQL logic will be added in the next stage.
// ============================================================

import model.User;

import java.util.List;

public class UserDAO {

    // --------------------------------------------------------
    // saveUser() — will insert a new user into the DB
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public void saveUser(User user) {
        // TODO: Write INSERT INTO users (...) VALUES (...) here
        System.out.println("UserDAO.saveUser() - Not implemented yet.");
    }

    // --------------------------------------------------------
    // getUserById() — will fetch one user by their ID
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public User getUserById(int userId) {
        // TODO: Write SELECT * FROM users WHERE user_id = ? here
        System.out.println("UserDAO.getUserById() - Not implemented yet.");
        return null;
    }

    // --------------------------------------------------------
    // getAllUsers() — will return every user in the DB
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public List<User> getAllUsers() {
        // TODO: Write SELECT * FROM users here
        System.out.println("UserDAO.getAllUsers() - Not implemented yet.");
        return null;
    }
}
