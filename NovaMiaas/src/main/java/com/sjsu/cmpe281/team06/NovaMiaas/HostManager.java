package com.sjsu.cmpe281.team06.NovaMiaas;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HostManager extends MySQLConnection {
	public HostManager() {
		
	}
	
	public void updateHostIp() {
		String m = "";
		try {
			InetAddress addr = Inet4Address.getLocalHost();
			m = addr.getHostAddress();
			System.out.println("IP: " + m);
			try {
				String sql = "UPDATE hosts SET host_ip = ? " 
						+ " WHERE id = ?";
				PreparedStatement pst = connection.prepareStatement(sql);
				pst.setString(1, m);
				pst.setInt(2, MyEntity.HOST_ID);
				pst.executeUpdate();
		        System.out.println("Updated Successfully!");
		        connection.close();
			} catch(SQLException e) {
				
			}
		} catch(UnknownHostException e) {
			
		}
	}
	public String getHostIp() {
		String[] temp;
		StringBuilder ret = new StringBuilder();
		String m = "";
		try {
            InetAddress addr = InetAddress.getLocalHost();
            temp = addr.getHostAddress().split("\\.");  
            for(int i=0; i<temp.length; i++){
            	ret.append(temp[i]).append("-");
            }
            m = ret.toString().trim();
        } catch(UnknownHostException e) {
             //throw Exception
        }
		return m;
	}
}
