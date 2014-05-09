package com.sjsu.cmpe281.team06.NovaMiaas;

public class Solution {
	public static MiaasManager miaasManager;
	public static SQSMessage sqsMessage;
	public static HostManager hostManager;
	public static void main(String[] args) throws Exception {
		miaasManager = new MiaasManager();
		sqsMessage = new SQSMessage();
		hostManager = new HostManager();
		//run();
		test();
	}
	
	public static void run() throws Exception {
		//miaasManager.listAllAttachedDevices();
		//miaasManager.rebootDevice("192.168.56.101:5555");
		//System.out.println("Host: " + hostManager.getHostIp());
		String msg = "1/0/1/1/off";
		sqsMessage.sendMessageToQueue(msg);
		while(true){
			sqsMessage.updateSQL(sqsMessage.reciveMessageFromQueue());
			Thread.sleep(10000);
		}
	}
	
	public static void test() throws Exception {
		//miaasManager.test();
		//miaasManager.listAllUserUploadedApps("12345");
		//miaasManager.listAllAttachedDevices();
		//System.out.println(miaasManager.getNameByIp("192.168.56.101:5555"));
		//miaasManager.powerOnVM("Google Nexus - 4.3 - 18");
		//System.out.println(miaasManager.cmdExec("cmd.exe /c cd C:/Dev/Genymotion & player --vm-name \"Google Nexus - 4.3 - 18\""));
		//System.out.println(miaasManager.cmdExec("cmd.exe /c cd c:/App/12345 & dir"));
		//miaasManager.JDBC();
		//miaasManager.testSelectFromTableUsers("*", "users");
		//hostManager.updateHostIp();
		//System.out.println(miaasManager.checkMobileStatus(10));
		System.out.println(miaasManager.returnEmulatorIpAddress(miaasManager.checkCurrentEmulatorNumbers(1)));
	}
}
