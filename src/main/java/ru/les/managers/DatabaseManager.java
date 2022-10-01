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
        InputStream inputStream = null;
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM toPy WHERE chatId = " + chatId);
             ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    long chId = rs.getLong(1);
                    inputStream = rs.getBinaryStream(2);
                }
            System.out.println("LOADED " + chatId + " FROM DB");
            //PreparedStatement pst2 = con.prepareStatement("DELETE FROM toPy WHERE chatId = " + chatId);
            //pst2.executeUpdate();

            System.out.println("DELETED " + chatId + " FROM DB");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(TelebotApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return inputStream;
    }
}
