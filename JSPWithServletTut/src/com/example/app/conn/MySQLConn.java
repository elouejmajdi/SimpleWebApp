package com.example.app.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConn {

	
	public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
		String hostName = "localhost";
		String db = "dbtutorial";
		String username = "root";
		String password = "1988";

		return getMySQLConnection(hostName, db, username, password);
	}

	public static Connection getMySQLConnection(String hostName, String db, String username, String password) throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + db;

		Connection conn = DriverManager.getConnection(connectionURL, username, password);

		return conn;
	}
}
