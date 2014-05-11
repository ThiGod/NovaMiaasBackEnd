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
		//String msg = "1/0/1/1/on";
		//sqsMessage.sendMessageToQueue(msg);
		while(true){
			//sqsMessage.start(sqsMessage.test());
			sqsMessage.start(sqsMessage.reciveMessageFromQueue());
			Thread.sleep(5000);
		}
	}
	
	public static void test() throws Exception {
		//miaasManager.test();
		//miaasManager.listAllUserUploadedApps("12345");
		//miaasManager.listAllAttachedDevices();
		//System.out.println(miaasManager.getNameByIp("192.168.56.101:5555"));
		//miaasManager.newEmulatorShCreater(1);
		//miaasManager.changeMod();
		miaasManager.powerOnEmulator(1, 1);
		//miaasManager.powerOffEmulatorSimple(1, 1);
		//System.out.println(miaasManager.cmdExec("cmd.exe /c cd C:/Dev/Genymotion & player --vm-name \"Google Nexus - 4.3 - 18\""));
		//System.out.println(miaasManager.cmdExec("cmd.exe /c cd c:/App/12345 & dir"));
		//miaasManager.JDBC();
		//miaasManager.testSelectFromTableUsers("*", "users");
		//hostManager.updateHostIp();
		//hostManager.showIp();
		//System.out.println(hostManager.getFirstNonLoopbackAddress(true, false).getHostAddress());
		//System.out.println(miaasManager.checkMobileStatus(10));
		//System.out.println(miaasManager.setEmulatorIpAddress(miaasManager.getCurrentEmulatorNumbers(1)));
		//System.out.println(miaasManager.getEmulatorName(1));
		//miaasManager.updateSQL();
		//miaasManager.getEmulatorIpList();
		//System.out.println(miaasManager.getUserMobileId(1, 1));
		
	}
}
