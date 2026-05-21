package org.example.server.manager;

import org.example.common.User;

import java.awt.print.Paper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;

public class AuthManager {
    private DatabaseManager databaseManager;
    private String pepper;

    public AuthManager(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;

        String paperFromEnv = System.getenv("PASSWORD_PAPER");
        if (paperFromEnv != null){
            this.pepper = paperFromEnv;
        } else {
            this.pepper = "default-pepper";
        }
    }

    public User register(String login, String password){
        try{
            if (databaseManager.getUserByLogin(login) != null) {
                System.err.println("Пользователь с логином '" + login + "' уже существует");
                return null;
            }

            String salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);
            long userId = databaseManager.createUser(login, hashedPassword, salt);

            System.out.println("Пользователь '" + login + "' зарегистрирован с id =" + userId);
            return new User(userId, login, hashedPassword, salt);

        } catch (SQLException e){
            System.err.println("Ошибка при регистрации: " + e.getMessage());
            return null;
        }
    }

    public User authenticate(String login, String password) {
        try {
            User user = databaseManager.getUserByLogin(login);
            if (user == null) {
                return null;
            }

            String hashedPassword = hashPassword(password, user.getSalt());

            if (user.getPasswordHash().equals(hashedPassword)) {
                return user;
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Ошибка авторизации: " + e.getMessage());
            return null;
        }
    }


    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            String passwordWithSaltAndPepper = password + salt + pepper;

            byte[] hashBytes = md.digest(passwordWithSaltAndPepper.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 алгоритм не найден", e);
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
