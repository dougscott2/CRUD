package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by DrScott on 11/3/15.
 */
public class MainTest {

    public Connection startConnection() throws SQLException{
       // Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE games");
        conn.close();
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "1234");
        User user = Main.selectUser(conn, "Alice");
        endConnection(conn);
        assertTrue(user != null);
    }

}