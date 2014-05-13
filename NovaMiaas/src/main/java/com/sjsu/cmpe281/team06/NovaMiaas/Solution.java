package com.sjsu.cmpe281.team06.NovaMiaas;

public class Solution {
	public static MiaasManager miaasManager;
	public static SQSMessage sqsMessage;
	public static HostManager hostManager;
	
	public static void main(String[] args) throws Exception {
		miaasManager = new MiaasManager();
		sqsMessage = new SQSMessage();
		hostManager = new HostManager();
		run();
	}
	
	public static void run() throws Exception {
		hostManager.updateHostIp();
		while(true){
			sqsMessage.start(sqsMessage.reciveMessageFromQueue());
			Thread.sleep(5000);
		}
	}

}
