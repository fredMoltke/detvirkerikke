package dal;

import dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpls185111 implements IUserDAO {

    private String[] availableRoles = {"admin", "pharmacist", "foreman", "operator"};

    public static void main(String[] args) {
        UserDAOImpls185111 test = new UserDAOImpls185111();
        try {
            test.getUser(13);
        }catch (DALException e){
        }
    }

    @Override
    public UserDTO getUser(int userId) throws DALException {
        UserDTO userDTO = new UserDTO();
        ArrayList<String> userRoles = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185111?", "s185111", "o3H1z5Zp2TxtIQEbXvjJm")){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user WHERE user_id = " + userId + ";");
            System.out.println("Got following user from the database:");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1) + ": " + resultSet.getString(2));
                for (int i = 0; i < availableRoles.length; i++){
                    if (resultSet.getBoolean(i+6)){
                        userRoles.add(availableRoles[i]);
                    }
                }
            }

            userDTO.setUserId(userId);
            userDTO.setUserName(resultSet.getString(2));
            userDTO.setIni(resultSet.getString(3));
            userDTO.setRoles(userRoles);

        } catch (SQLException e) {
            throw new DALException("Failed to get user.", e);
        }


        return userDTO;
    }

    @Override
    public List<UserDTO> getUserList() throws DALException {
        ArrayList<UserDTO> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com:3306/?user=s185111"
                + "user=s185111&password=o3H1z5Zp2TxtIQEbXvjJm")){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            System.out.println("Got resultset from database:");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1) + ": " + resultSet.getString(2));
            }
        } catch (SQLException e) {
            throw new DALException("Failed getting user list.", e);
        }
        return userList;
    }

    @Override
    public void createUser(UserDTO user) throws DALException {

        boolean[] userRoles = getUserRoles(user);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com:3306/?user=s185111"
                + "user=s185111&password=o3H1z5Zp2TxtIQEbXvjJm")){
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (user_id, user_name, ini, is_admin, is_pharmacist," +
                    " is_foreman, is_operator) VALUES (" + user.getUserId() + ", " + user.getUserName() + ", " + user.getIni() +
                    ", " + userRoles[0] + ", " + userRoles[1] + ", " + userRoles[2] + ", " + userRoles[3] + ");");
        } catch (SQLException e) {
            throw new DALException("Failed getting user list.", e);
        }
    }

    @Override
    public void updateUser(UserDTO user) throws DALException {
        boolean[] userRoles = getUserRoles(user);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com:3306/?user=s185111"
                + "user=s185111&password=o3H1z5Zp2TxtIQEbXvjJm")){
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE users SET user_name = " + user.getUserName() + ", ini = " + user.getIni() +
                    ", is_admin = " + userRoles[0] + ", is_pharmacist = " + userRoles[1] + ", is_foreman = " + userRoles[2] +
                    ", is_operator = " + userRoles[3] + " WHERE user_id = " + user.getUserId() + ";");
        } catch (SQLException e) {
            throw new DALException("Failed updating user.", e);
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com:3306/?user=s185111"
                + "user=s185111&password=o3H1z5Zp2TxtIQEbXvjJm")){
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM users WHERE user_id = " + userId + ";");
        } catch (SQLException e) {
            throw new DALException("Failed updating user.", e);
        }
    }

    public boolean[] getUserRoles(UserDTO user){
        boolean[] userRoles = new boolean[4];
        for (int i = 0; i < userRoles.length; i++){
            if (user.getRoles().contains(availableRoles[i])){
                userRoles[i] = true;
            } else{
                userRoles[i] = false;
            }
        }
        return userRoles;
    }
}
