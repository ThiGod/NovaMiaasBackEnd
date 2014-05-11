package com.sjsu.cmpe281.team06.NovaMiaas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
	public Connection connection;
	public MySQLConnection() {
		//System.out.println("-------- MySQL JDBC Connection Testing ------------");
		
		try {
			Class.forName(MyEntity.SQL_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
	 	
		//System.out.println("MySQL JDBC Driver Registered!");
		
		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://54.200.72.126/"+MyEntity.SQL_NAME, MyEntity.SQL_USERNAME, MyEntity.SQL_PASSWORD);
	 
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	 
		if (connection != null) {
			//System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
}
