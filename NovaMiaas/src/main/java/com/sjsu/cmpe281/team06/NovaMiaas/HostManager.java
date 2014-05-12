package com.sjsu.cmpe281.team06.NovaMiaas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

public class HostManager extends MySQLConnection {
	public HostManager() {
		
	}
	
	public void updateHostIp() throws IOException {
		String m = "";
		try {
			InetAddress addr = getFirstNonLoopbackAddress(true, false);
			m = addr.getHostAddress();
			//m = getUbuntuElasticIp();
			System.out.println("IP: " + m);
			try {
				String query = "UPDATE hosts SET host_ip = ? " 
						+ " WHERE id = ?";
				PreparedStatement pst = connection.prepareStatement(query);
				pst.setString(1, m);
				pst.setInt(2, MyEntity.HOST_ID);
				pst.executeUpdate();
		        System.out.println("Host IP update Successfully!");
		        connection.close();
			} catch(SQLException e) {
				
			}
		} catch(SocketException e) {
			
		}
	}
	
	public InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
	    Enumeration en = NetworkInterface.getNetworkInterfaces();
	    while (en.hasMoreElements()) {
	        NetworkInterface i = (NetworkInterface) en.nextElement();
	        for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress()) {
	                if (addr instanceof Inet4Address) {
	                    if (preferIPv6) {
	                        continue;
	                    }
	                    return addr;
	                }
	                if (addr instanceof Inet6Address) {
	                    if (preferIpv4) {
	                        continue;
	                    }
	                    return addr;
	                }
	            }
	        }
	    }
	    return null;
	}
	
	public String getUbuntuElasticIp() throws IOException {
		String elasticIp = null;
		Runtime rt = Runtime.getRuntime();
	    String[] cmd = { "/bin/sh", "-c", "ifconfig eth0 | grep \"inet addr\" | awk -F\":\" {'print $2'} | awk -F\" \" {'print $1'}" };
	    Process proc = rt.exec(cmd);
	    BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	    String line;
	    while ((line = is.readLine()) != null) {
	        elasticIp = line;
	    }
	    return elasticIp;
	}
}
