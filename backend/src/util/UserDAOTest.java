
package util;

import dao.UserDAO;
import model.User;

import java.util.List;

public class UserDAOTest {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        // TEST 1: Save a new user
        System.out.println("===== SAVE USER =====");

        User newUser = new User(
                0,
                "Rahul Sharma",
                22,
                "Punjab",
                "Student",
                300000,
                "Graduate");

        userDAO.saveUser(newUser);

        // TEST 2: Retrieve all users
        System.out.println("\n===== ALL USERS =====");

        List<User> users = userDAO.getAllUsers();

        for (User user : users) {
            System.out.println(user);
        }

        System.out.println("Total users: " + users.size());

        // TEST 3: Retrieve user by ID
        System.out.println("\n===== USER BY ID =====");

        if (!users.isEmpty()) {

            int firstUserId = users.get(0).getUserId();

            User user = userDAO.getUserById(firstUserId);

            if (user != null) {
                System.out.println(user);
            } else {
                System.out.println("User not found.");
            }

        } else {
            System.out.println("No users available.");
        }
    }
}
