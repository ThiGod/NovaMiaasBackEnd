package com.sjsu.cmpe281.team06.NovaMiaas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MiaasManager extends MySQLConnection {
	Statement stmt = null;
	ResultSet rs = null;
	
	public MiaasManager () {
		
	}
	
	public void testSelectFromTableUsers(String s, String f) {	
		try {
			stmt = connection.createStatement();
		    String query = "SELECT " + s + " FROM " + f;
		    rs = stmt.executeQuery(query);
		    while(rs.next()) {
		    	int id = rs.getInt("id");
		    	String firstName = rs.getString("first_name");
		    	String lastName = rs.getString("last_name");
		    	String email = rs.getString("email");
		    	String password = rs.getString("password");
		    	boolean admin = rs.getBoolean("admin_authority");
		    	
		    	System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, email, password, admin);
		    }
		    stmt.close();
		} catch (SQLException ex){
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			
		}
	}
	
	public int checkMobileStatus(int mobileId) {
		int status = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT status FROM mobiles WHERE id=" + mobileId;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				status = rs.getInt("status");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return status;
	}
	
	public int checkCurrentEmulatorNumbers(int hostId) {
		int count  = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT COUNT(*) FROM mobiles WHERE emulator_flag=0 AND status=1 AND host_id=" + hostId;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				count = rs.getInt("COUNT(*)");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return count;
	}
	
	public String returnEmulatorIpAddress(int num) {
		String eIp = "";
		int endIp = num+1;
		if(endIp<10) {
			eIp = "192.168.56.10" + Integer.toString(endIp) + ":5555";
		} else {
			eIp = "192.168.56.1" + Integer.toString(endIp) + ":5555";
		}
		return eIp;
	}
	
	public void listAllAttachedDevices() throws Exception {
		String cmd = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "adb devices";
		} else {
			cmd = "adb devices";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		System.out.println(process.getOutputStream());
		process.waitFor();
		if (process.exitValue() == 0) {
			System.out.println("Success");
		} else {
			System.out.println("Fail");
		}
	}
	
	public void listAllUserUploadedApps(String uid) throws Exception {
		String cmd = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "cmd.exe /c cd C:/App/"+ "uid" +"& dir";
		} else {
			cmd = "cmd.exe /c cd C:/App/"+ "uid" +"& dir";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		if (process.exitValue() == 0) {
			System.out.println("Success");
		} else {
			System.out.println("Fail");
		}
	}
	
	public void installAppForDevice(String uid, String deviceName, String appName) throws Exception {
		String cmd = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "cmd.exe /c cd C:/App/" + uid + " & adb -s " + deviceName + " install " + appName;
		} else {
			cmd = "cmd.exe /c cd C:/App/" + uid + " & adb -s " + deviceName + " install " + appName;
		}
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		if (process.exitValue() == 0) {
			System.out.println("Success");
		} else {
			System.out.println("Fail");
		}
	}
	
	public void rebootDevice(String deviceName) throws Exception {
		String cmd = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "adb -s " + deviceName + " reboot";
		} else {
			cmd = "adb -s " + deviceName + " reboot";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		if (process.exitValue() == 0) {
			System.out.println("Success");
		} else {
			System.out.println("Fail");
		}
	}
	
	public void powerOnVM(String name) throws IOException, InterruptedException {
		String cmd = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "cmd.exe /c cd C:/Dev/Genymotion & player --vm-name \"" + name + "\"";
		} else {
			cmd = "cmd.exe /c cd C:/Dev/Genymotion & player --vm-name \"" + name + "\"";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		/*
		process.waitFor();
		if (process.exitValue() == 0) {
			System.out.println("Success");
		} else {
			System.out.println("Fail");
		}
		*/
	}
	
	public String getNameByIp(String ip) {
		String cmd;
		String name = "";
		cmd = "adb -s " + ip + " shell getprop ro.product.model";
		name = cmdExec(cmd);
		return name;
	}
	
	public void test() {
		try {
	        Process p = Runtime.getRuntime().exec("cmd.exe /c cd c:/App/12345 & dir");
	        read(p);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void read(Process p) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line = null;
	        while((line=input.readLine()) != null) {
	            System.out.println(line);
	        }   
	    } catch(IOException e1) {
	    	e1.printStackTrace(); 
	    }
	}
	
	public String cmdExec(String cmdLine) {
	    String line;
	    String output = "";
	    try {
	        Process p = Runtime.getRuntime().exec(cmdLine);
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while((line = input.readLine()) != null) {
	            //System.out.println(line);               
	            output += (line + '\n');
	        }            
	        input.close();           
	    } catch (Exception ex) {
	    	output = "ERROR";
	        //ex.printStackTrace();
	    } finally {
	    	return output;
	    }
	}
}
