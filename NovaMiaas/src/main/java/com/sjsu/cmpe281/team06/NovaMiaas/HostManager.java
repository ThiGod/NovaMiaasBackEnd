package com.sjsu.cmpe281.team06.NovaMiaas;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

public class HostManager extends MySQLConnection {
	public HostManager() {
		
	}
	
	public void updateHostIp() {
		String m = "";
		try {
			InetAddress addr = getFirstNonLoopbackAddress(true, false);
			m = addr.getHostAddress();
			System.out.println("IP: " + m);
			try {
				String query = "UPDATE hosts SET host_ip = ? " 
						+ " WHERE id = ?";
				PreparedStatement pst = connection.prepareStatement(query);
				pst.setString(1, m);
				pst.setInt(2, MyEntity.HOST_ID);
				pst.executeUpdate();
		        System.out.println("Updated Successfully!");
		        connection.close();
			} catch(SQLException e) {
				
			}
		} catch(SocketException e) {
			
		}
	}
	
	public void showIp() throws Exception {
		System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
                NetworkInterface e = n.nextElement();
                System.out.println("Interface: " + e.getName());
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();)
                {
                        InetAddress addr = a.nextElement();
                        System.out.println("  " + addr.getHostAddress());
                }
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
