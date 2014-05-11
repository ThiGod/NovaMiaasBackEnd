package com.sjsu.cmpe281.team06.NovaMiaas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Library;
import com.sun.jna.Native;

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
	
	public void getEmulatorIpList() {
		List<String> ipList = new ArrayList<String>();
		try {
			stmt = connection.createStatement();
		    String query = "SELECT ip FROM mobiles WHERE host_id=" + MyEntity.HOST_ID + " AND emulator_flag=0";
		    rs = stmt.executeQuery(query);
		    while(rs.next()) {
		    	ipList.add(rs.getString("ip"));
		    }
		    stmt.close();
		    
		    System.out.println(ipList);
		} catch (SQLException ex){
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	public void setRightIp() {
		
	}
	
	public void newEmulatorShCreater(int mobileId) {
		String mobileName = getEmulatorName(mobileId);
		try {
	        File statText = new File(MyEntity.UBUNTU_NEW_EMU_SH_PATH);
	        FileOutputStream is = new FileOutputStream(statText);
	        OutputStreamWriter osw = new OutputStreamWriter(is);    
	        Writer w = new BufferedWriter(osw);
	        w.write(MyEntity.UBUNTU_GENY_PATH + " \"" + mobileName + "\"");
	        w.close();
	    } catch (IOException e) {
	        System.err.println("Problem writing to the file new.sh");
	    }
		changeMod();
	}
	
	public CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);

    public void changeMod() {
        libc.chmod(MyEntity.UBUNTU_NEW_EMU_SH_PATH, 0755);
    }
	
	public int checkMobileStatus(int mobileId) {
		int status = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT status FROM mobiles WHERE id=" + mobileId;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				status = rs.getInt("status");
				//System.out.println("Status: " + status);
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return status;
	}
	
	public int getRunningEmulatorNumber() {
		int count  = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT COUNT(*) FROM mobiles WHERE emulator_flag=0 AND status!=0 AND host_id=" + MyEntity.HOST_ID;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				count = rs.getInt("COUNT(*)");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return count;
	}
	
	public int getRunningDeviceNumber() {
		int count  = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT COUNT(*) FROM mobiles WHERE emulator_flag=1 AND status!=0 AND host_id=" + MyEntity.HOST_ID;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				count = rs.getInt("COUNT(*)");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return count;
	}
	
	public String setEmulatorIpAddress(int num) {
		String eIp = "";
		int endIp = num+1;
		if(endIp<10) {
			eIp = "192.168.56.10" + Integer.toString(endIp) + ":5555";
		} else {
			eIp = "192.168.56.1" + Integer.toString(endIp) + ":5555";
		}
		return eIp;
	}
	
	public String getEmulatorName(int mobileId) {
		String name = "";
		try {
			stmt = connection.createStatement();
			String query = "SELECT name FROM mobiles WHERE id=" + mobileId;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				name = rs.getString("name");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return name;
	}
	
	public void powerOnEmulatorUpdateMySQL(int userId, int mobileId) {
		int mobileStatus = checkMobileStatus(mobileId);
		
		try {
			if(mobileStatus==2) {
				String queryTemp = "UPDATE mobiles SET status = ? " 
						+ " WHERE id = ?";
				PreparedStatement pstTemp = connection.prepareStatement(queryTemp);
				pstTemp.setInt(1, 1);
				pstTemp.setInt(2, mobileId);
				pstTemp.executeUpdate();
		        System.out.println("Table mobiles updated Successfully!");
			} else {
				String query = "UPDATE mobiles SET status = ? " 
						+ " , ip = ? "
						+ " WHERE id = ?";
				PreparedStatement pst = connection.prepareStatement(query);
				pst.setInt(1, 1);
				pst.setString(2, setEmulatorIpAddress(getRunningEmulatorNumber()));
				pst.setInt(3, mobileId);
				pst.executeUpdate();
		        System.out.println("Table mobiles updated Successfully!");
			}
			
	        String query2 = "UPDATE hosts SET used_emulator_no = ? " 
					+ " WHERE id = ?";
			PreparedStatement pst2 = connection.prepareStatement(query2);
			pst2.setInt(1, getRunningEmulatorNumber());
			pst2.setInt(2, MyEntity.HOST_ID);
			pst2.executeUpdate();
	        System.out.println("Table hosts updated Successfully!");
	        
	        String query3 = "INSERT INTO user_mobile (user_id, mobile_id)"
	                + " VALUES (?, ?)";
			PreparedStatement pst3 = connection.prepareStatement(query3);
			pst3.setInt(1, userId);
			pst3.setInt(2, mobileId);
			pst3.executeUpdate();
	        System.out.println("Table user_mobile updated Successfully!");

	        connection.close();    	        
		} catch(SQLException e) {
			
		}
	}
	
	public void powerOffEmulatorUpdateMySQL(int userId, int mobileId) {
		int tempId = getUserMobileId(userId, mobileId);
		
		try {	
			String query = "UPDATE mobiles SET status = ? " 
					+ " WHERE id = ?";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.setInt(1, 2);
			//pst.setString(2, "NULL");
			pst.setInt(2, mobileId);
			pst.executeUpdate();
	        System.out.println("Table mobiles updated Successfully!");
	        
	        String query2 = "UPDATE hosts SET used_emulator_no = ? " 
					+ " WHERE id = ?";
			PreparedStatement pst2 = connection.prepareStatement(query2);
			pst2.setInt(1, getRunningEmulatorNumber());
			pst2.setInt(2, MyEntity.HOST_ID);
			pst2.executeUpdate();
	        System.out.println("Table hosts updated Successfully!");
	        
	        if(tempId!=0) {
	        	Date date = new Date();
	        	Timestamp timestamp = new Timestamp(date.getTime());
	        	
	        	String query3 = "UPDATE user_mobile SET end_time = ? "
		                + " WHERE id = ?";
				PreparedStatement pst3 = connection.prepareStatement(query3);
				pst3.setTimestamp(1, timestamp);
				pst3.setInt(2, tempId);
				pst3.executeUpdate();
		        System.out.println("Table user_mobile updated Successfully!");
	        } else {
	        	System.err.println("Something wrong with user_mobile table");
	        }
	              
	        connection.close();            
		} catch(SQLException e) {
			
		}
	}
	
	public void terEmulatorUpdateMySQL(int userId, int mobileId) {
		try {
			int tempId = getUserMobileId(userId, mobileId);
			
			String query = "UPDATE mobiles SET status = ? " 
					+ " , ip = ? "
					+ " WHERE id = ?";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.setInt(1, 0);
			pst.setString(2, "NULL");
			pst.setInt(3, mobileId);
			pst.executeUpdate();
	        System.out.println("Table mobiles updated Successfully!");
	        
	        String query2 = "UPDATE hosts SET used_emulator_no = ? " 
					+ " WHERE id = ?";
			PreparedStatement pst2 = connection.prepareStatement(query2);
			pst2.setInt(1, getRunningEmulatorNumber());
			pst2.setInt(2, MyEntity.HOST_ID);
			pst2.executeUpdate();
	        System.out.println("Table hosts updated Successfully!");
	        
	        if(tempId!=0) {
	        	Date date = new Date();
	        	Timestamp timestamp = new Timestamp(date.getTime());
	        	
	        	String query3 = "UPDATE user_mobile SET end_time = ? "
		                + " WHERE id = ?";
				PreparedStatement pst3 = connection.prepareStatement(query3);
				pst3.setTimestamp(1, timestamp);
				pst3.setInt(2, tempId);
				pst3.executeUpdate();
		        System.out.println("Table user_mobile updated Successfully!");
	        } else {
	        	System.err.println("Something wrong with user_mobile table");
	        }
	              
	        connection.close();    
	        
		} catch(SQLException e) {
			
		}
	}
	
	public int getUserMobileId(int userId, int mobileId) {
		int id  = 0;
		try {
			stmt = connection.createStatement();
			String query = "SELECT MIN(id) FROM user_mobile WHERE user_id=" + userId
					+ " AND mobile_id=" + mobileId
					+ " AND end_time=\"0000-00-00 00:00:00\"";
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				id = rs.getInt("MIN(id)");
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return id;
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
			System.err.println("Fail");
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
			System.err.println("Fail");
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
			System.err.println("Fail");
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
			System.err.println("Fail");
		}
	}
	
	public void powerOnEmulator(int userId, int mobileId) throws IOException, InterruptedException {
		String cmd = "";
		String mobileName = getEmulatorName(mobileId);
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "cmd.exe /c cd C:/Dev/Genymotion & player --vm-name \"" + mobileName + "\"";
		} else {
			cmd = MyEntity.UBUNTU_NEW_EMU_SH_PATH;
			//cmd = "gnome-terminal && cd ~/Documents/dev/genymotion && ./player --vm-name \"" + mobileName + "\"";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		powerOnEmulatorUpdateMySQL(userId, mobileId);
		if(process.waitFor(10, TimeUnit.SECONDS)) {
			System.out.println("Success");
		} else {
			System.err.println("Fail");
		}
	}
	
	public void powerOffEmulator(int userId, int mobileId) throws IOException, InterruptedException {
		String cmd = "";
		String mobileName = getEmulatorName(mobileId);
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "vboxmanage controlvm \"" + mobileName + "\" poweroff";
		} else {
			cmd = "vboxmanage controlvm \"" + mobileName + "\" poweroff";
		}
		Process process = Runtime.getRuntime().exec(cmd);
		powerOffEmulatorUpdateMySQL(userId, mobileId);
		process.waitFor(10, TimeUnit.SECONDS);
		if(process.waitFor(10, TimeUnit.SECONDS)) {
			System.out.println("Success");
		} else {
			System.err.println("Fail");
		}
	}
	
	public void powerOffEmulatorSimple(int userId, int mobileId) {
		powerOffEmulatorUpdateMySQL(userId, mobileId);
	}
	
	public void terEmulatorSimple(int userId, int mobileId) {
		terEmulatorUpdateMySQL(userId, mobileId);
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
	    	
	    }
	    return output;
	}
}

interface CLibrary extends Library {
    public int chmod(String path, int mode);
}
