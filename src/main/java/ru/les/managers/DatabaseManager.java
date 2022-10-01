package ru.les.managers;

import ru.les.TelebotApplication;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    static String url = "jdbc:mysql://localhost/ememlesdb";
    static String user = "admin";
    static String password = "admin";

    public static void photoToPy(long chatId, InputStream inputStream) {
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

    public static InputStream photoFromDB(long chatId) {
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM toPy WHERE chatId = 796496528");
             ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    long chId = rs.getLong(1);
                    InputStream inputStream = rs.getBinaryStream(2);
                    return inputStream;
                }
            System.out.println("LOADED FROM DB");

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
