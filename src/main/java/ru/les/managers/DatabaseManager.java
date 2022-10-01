package ru.les.managers;

import ru.les.TelebotApplication;
import java.io.InputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    static String url = "jdbc:mysql://localhost/ememlesdb";
    static String user = "admin";
    static String password = "admin";

    public synchronized static void photoToPy(long chatId, InputStream inputStream) {
        String query = "INSERT INTO toPy VALUES(?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {


            pst.setLong(1, chatId);
            pst.setBinaryStream(2, inputStream);
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized static String emotionFromPy(long chatId) {
        String emotion = "none";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT emotion FROM emotionsPy WHERE chatId = " + chatId);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                emotion = rs.getString(1);
            }
            System.out.println("LOADED " + chatId + " FROM emotionsPy");
            PreparedStatement pst2 = con.prepareStatement("DELETE FROM emotionsPy WHERE chatId = " + chatId);
            pst2.executeUpdate();
            System.out.println("DELETED " + chatId + " FROM emotionsPy");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return emotion;
    }

    public synchronized static InputStream photoFromPy(long chatId) {
        InputStream inputStream = null;
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT image FROM fromPy WHERE chatId = " + chatId);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                inputStream = rs.getBinaryStream(1);
            }
            System.out.println("LOADED " + chatId + " FROM fromPy");
            PreparedStatement pst2 = con.prepareStatement("DELETE FROM fromPy WHERE chatId = " + chatId);
            pst2.executeUpdate();
            System.out.println("DELETED " + chatId + " FROM fromPy");
            System.out.println("LOADED " + chatId + " FROM toPy");
            PreparedStatement pst3 = con.prepareStatement("DELETE FROM toPy WHERE chatId = " + chatId);
            pst3.executeUpdate();
            System.out.println("DELETED " + chatId + " FROM toPy");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return inputStream;
    }

    public synchronized static InputStream photoFromDB(long chatId) {
        InputStream inputStream = null;
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM toPy WHERE chatId = " + chatId);
             ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    long chId = rs.getLong(1);
                    inputStream = rs.getBinaryStream(2);
                }
            System.out.println("LOADED " + chatId + " FROM toPy");
            PreparedStatement pst2 = con.prepareStatement("DELETE FROM toPy WHERE chatId = " + chatId);
            pst2.executeUpdate();
            System.out.println("DELETED " + chatId + " FROM toPy");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return inputStream;
    }

    public synchronized static void userToDB(long chatId) {

        boolean isInDB = false;
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT chatId FROM users WHERE chatId = " + chatId);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                isInDB = true;
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        if (isInDB) {
            return;
        }
        String query = "INSERT INTO users VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setLong(1, chatId);
            pst.setInt(2, 0);
            pst.setString(3, "none");
            pst.setByte(4, (byte) 0);
            pst.setByte(5, (byte) 0);
            pst.setInt(6, 11111);
            pst.setBinaryStream(7, null);
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized static void updateUserNumeric(long chatId, String column, int value) {
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("UPDATE users SET " + column + " = " + value + " WHERE chatId = " + chatId)) {
            pst.executeUpdate();
            System.out.println("updated column " + column + " of " + chatId + ". new value = " + value);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized static void updateUserEmotion(long chatId, String value) {
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("UPDATE users SET dailyEmotionType = \"" + value + "\" WHERE chatId = " + chatId)) {
            pst.executeUpdate();
            System.out.println("updated dailyEmotionType of " + chatId + ". new value = " + value);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
