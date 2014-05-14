package com.sjsu.cmpe281.team06.NovaMiaas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class MiaasManager extends MySQLConnection {
	private Statement stmt = null;
	private ResultSet rs = null;
	private AmazonSQS sqs;
	
	public MiaasManager () {
	}
	
	public List<String> getEmulatorIpList() {
		List<String> ipList = new ArrayList<String>();
		String ip = null;
		
		try {
			stmt = connection.createStatement();
		    String query = "SELECT ip FROM mobiles WHERE host_id=" + MyEntity.HOST_ID + " AND emulator_flag=0";
		    rs = stmt.executeQuery(query);
		    while(rs.next()) {
		    	ip = rs.getString("ip");
		    	if(ip != null) {
		    		ipList.add(ip);
		    	}
		    }
		    stmt.close();
		    
		    System.out.println(ipList);
		} catch (SQLException ex){
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return ipList;
	}
	
	public String setIp(List<String> ipList) {
		String ip = "";
		if(ipList==null|ipList.size()==0) {
			ip = "192.168.56.101:5555";
		} else {
			for(int i=0; i<ipList.size(); i++) {
				if(ip!=ipList.get(i)) {
					
				} else {
					ip = "192.168.56.10" + i;
				}
			}
		}	
		return ip;
	}
	
	public void get(String ip){ 
		String [] temp = null;
		temp = ip.split(":");
		String realIp = temp[0];
		String [] temp2 = null;
		temp2 = realIp.split(".");
		String last = temp2[3];
		System.out.println(last);
	}
	
	public void findLowestIp(List<String> ipList) {
		String ip = ipList.get(0);
		for (String temp:ipList){
			if(temp==ip) {
				ip = temp;
			}
		}
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
		changeMod(MyEntity.UBUNTU_NEW_EMU_SH_PATH);
	}
	
	public CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);

    public void changeMod(String path) {
        libc.chmod(path, 0755);
    }
	
	public int checkMobileStatus(int mobileId) {
		int status = 0;
		try {
			System.out.println("Start checkMobileStatus try");
			stmt = null;
			stmt = connection.createStatement();
			String query = "SELECT status FROM mobiles WHERE id=" + mobileId;
			rs = stmt.executeQuery(query);
			while(rs.next()) { 
				status = rs.getInt("status");
				System.out.println("Current Status: " + status);
			}
			stmt.close();
		} catch (SQLException ex) {
			
		}
		return status;
	}
	
	public int getRunningEmulatorNumber() {
		int count  = 0;
		try {
			stmt = null;
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
			stmt = null;
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
			stmt = null;
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
		try {
			int mobileStatus = checkMobileStatus(mobileId);
        	
			if(mobileStatus==2) {
				String queryTemp = "UPDATE mobiles SET status = ? " 
						+ " WHERE id = ?";
				PreparedStatement pstTemp = connection.prepareStatement(queryTemp);
				pstTemp.setInt(1, 1);
				pstTemp.setInt(2, mobileId);
				pstTemp.executeUpdate();
		        System.out.println("Table mobiles updated (without ip) Successfully!");
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
        	
	        Date date = new Date();
        	Timestamp timestamp = new Timestamp(date.getTime());
        	
	        String query3 = "INSERT INTO user_mobile (user_id, mobile_id, start_time)"
	                + " VALUES (?, ?, ?)";
			PreparedStatement pst3 = connection.prepareStatement(query3);
			pst3.setInt(1, userId);
			pst3.setInt(2, mobileId);
			pst3.setTimestamp(3, timestamp);
			pst3.executeUpdate();
	        System.out.println("Table user_mobile updated Successfully!");
	        
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void powerOffEmulatorUpdateMySQL(int userId, int mobileId) {	
		try {	
			int tempId = getUserMobileId(userId, mobileId);
			
			String query = "UPDATE mobiles SET status = ? " 
					+ " WHERE id = ?";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.setInt(1, 2);
			pst.setInt(2, mobileId);
			pst.executeUpdate();
	        System.out.println("Table mobiles updated (without ip) Successfully!");
	        
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
	        
		} catch(SQLException e) {
			e.printStackTrace();
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
			pst.setString(2, null);
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
           
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getUserMobileId(int userId, int mobileId) {
		int id  = 0;
		try {
			stmt = null;
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
	
	public void powerOnEmulator(int userId, int mobileId) throws IOException, InterruptedException {
		String cmd = "";
		String mobileName = getEmulatorName(mobileId);
		newEmulatorShCreater(mobileId);
		System.out.println("Mobile Name: " + mobileName);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/on/"; 
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = MyEntity.WINDOWS_GENY_PATH + " \"" + mobileName + "\"";
		} else {
			cmd = MyEntity.UBUNTU_NEW_EMU_SH_PATH;
		}
		Process process = Runtime.getRuntime().exec(cmd);
		
		if(process.waitFor(10, TimeUnit.SECONDS)) {
			System.out.println("Power on emulator -> Fail");
			sendMsg(sendMsg+"fail");
		} else {
			System.out.println("Power on emulator -> Success");
			powerOnEmulatorUpdateMySQL(userId, mobileId);
			sendMsg(sendMsg+"pass");
		}
	}
	
	public void powerOnDevice(int userId, int mobileId) throws IOException, InterruptedException {
		String mobileName = getEmulatorName(mobileId);
		System.out.println("Mobile Name: " + mobileName);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/on/"; 	
		sendMsg(sendMsg+"pass");
	}
	
	public void powerOffDevice(int userId, int mobileId) throws IOException, InterruptedException {
		String mobileName = getEmulatorName(mobileId);
		System.out.println("Mobile Name: " + mobileName);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/off/"; 	
		sendMsg(sendMsg+"pass");
	}
	
	public void terDevice(int userId, int mobileId) throws IOException, InterruptedException {
		String mobileName = getEmulatorName(mobileId);
		System.out.println("Mobile Name: " + mobileName);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/ter/"; 	
		sendMsg(sendMsg+"pass");
	}
	
	public void powerOffEmulator(int userId, int mobileId) throws IOException, InterruptedException {
		String cmd = "";
		String mobileName = getEmulatorName(mobileId);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/off/"; 
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = "vboxmanage controlvm \"" + mobileName + "\" poweroff";
		} else {
			cmd = "vboxmanage controlvm \"" + mobileName + "\" poweroff";
		}
		Process process = Runtime.getRuntime().exec(cmd);

		if(process.waitFor(5, TimeUnit.SECONDS)) {
			System.out.println("Power off emulator -> Success");
			powerOff(mobileId);
			powerOffEmulatorUpdateMySQL(userId, mobileId);
			sendMsg(sendMsg+"pass");		
		} else {
			System.out.println("Power off emulator -> Fail");
			sendMsg(sendMsg+"fail");
		}
	}
	
	public void powerOff(int mobileId) throws IOException {
		String mobileName = getEmulatorName(mobileId);
		String pfcmd = "ps -axf | grep \"/player --vm-name " + mobileName + "\"  | grep \"Sl\" |grep -v grep | awk {'print \"kill \" $1'} | sh";
		getCmdExec(pfcmd);
	}
	
	public void powerOffEmulatorSimple(int userId, int mobileId) {
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/off/"; 
		powerOffEmulatorUpdateMySQL(userId, mobileId);
		sendMsg(sendMsg+"pass");
	}
	
	public void terEmulatorSimple(int userId, int mobileId) throws IOException {
		powerOff(mobileId);
		String sendMsg = MyEntity.HOST_ID + "/" + mobileId + "/ter/"; 
		terEmulatorUpdateMySQL(userId, mobileId);
		sendMsg(sendMsg+"pass");
	}
	
	public void sendMsg(String msg) {
		sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest1 = Region.getRegion(Regions.US_WEST_1);
		sqs.setRegion(usWest1);
		System.out.println("Sending a message to queue: " + MyEntity.SEND_TO_PHP_QUEUE);
        System.out.println("Message: " + msg);
        sqs.sendMessage(new SendMessageRequest(MyEntity.SEND_TO_PHP_QUEUE, msg));
	}
	
	public String getNameByIp(String ip) {
		String cmd;
		String name = "";
		cmd = "adb -s " + ip + " shell getprop ro.product.model";
		name = getCmdExec(cmd);
		return name;
	}
	
	public String getCmdExec(String cmdLine) {
		String output = "";
		try {
			Runtime rt = Runtime.getRuntime();
		    String[] cmd = { "/bin/sh", "-c", cmdLine };
		    Process proc = rt.exec(cmd);
		    BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		    String line;
		    while ((line = is.readLine()) != null) {
		        output = line;
		    }
		} catch(Exception e) {
			output = "ERROR";
		}
	    return output;
	}
	
	
	
	public String cmdExec(String cmdLine) {
	    String line;
	    String output = "";
	    try {
	    	System.out.println("Start cmdExec try");
	        Process p = Runtime.getRuntime().exec(cmdLine);
	        System.out.println("cmd: " + cmdLine);
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while((line = input.readLine()) != null) {
	            //System.out.println(line);               
	            output += (line + '\n');
	        }            
	        input.close();           
	    } catch (Exception ex) {
	    	output = "ERROR";
	        //ex.printStackTrace();
	    }
	    return output;
	}
}

interface CLibrary extends Library {
    public int chmod(String path, int mode);
}
