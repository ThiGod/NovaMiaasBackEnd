package com.sjsu.cmpe281.team06.NovaMiaas;

import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSMessage {
	MiaasManager miaasManager = new MiaasManager();
	HostManager hostManager = new HostManager();
	private AmazonSQS sqs;
	private String userIdFromQueue = null;
	private String eFlagFromQueue = null; 
	private String hostIdFromQueue = null; 
	private String mobileIdFromQueue = null;
	private String toDoFromQueue = null;
	
	public SQSMessage() {
		sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest1 = Region.getRegion(Regions.US_WEST_1);
		sqs.setRegion(usWest1);
	}
	
	public void createQueue() {
		System.out.println("Creating a new SQS queue called: " + MyEntity.RECEIVE_FROM_PHP_QUEUE);
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(MyEntity.RECEIVE_FROM_PHP_QUEUE);
        MyEntity.RECEIVE_FROM_PHP_QUEUE = sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
	public void sendMessageToQueue(String msg) {
		createQueue();
        System.out.println("Sending a message to queue: " + MyEntity.RECEIVE_FROM_PHP_QUEUE);
        System.out.println("Message: " + msg);
        sqs.sendMessage(new SendMessageRequest(MyEntity.RECEIVE_FROM_PHP_QUEUE, msg));
	}
	
	public String reciveMessageFromQueue() {
		String msg = null;
		String temp = null;
		System.out.println("Receiving messages from queue: " + MyEntity.RECEIVE_FROM_PHP_QUEUE);
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(MyEntity.RECEIVE_FROM_PHP_QUEUE);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            msg = message.getBody();
        }
        
        if(msg!=null) {
        	System.out.println("Message in queue: " + msg);
        	if(checkIfCurrentHost(msg)) {
        		temp = msg;  
        		System.out.println("Deleting a message.");
                String messageRecieptHandle = messages.get(0).getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(MyEntity.RECEIVE_FROM_PHP_QUEUE, messageRecieptHandle));
        	}   	 
        }
        return temp;
	}
	
	public boolean checkIfCurrentHost(String msg) {
		boolean currentHost = false;
		if(msg==null||msg.length()==0) 
			return currentHost;
		String [] temp;
		String hostId;
		temp = msg.split("/");
		hostId = temp[2];
		
		if(Integer.parseInt(hostId)==MyEntity.HOST_ID) 
			currentHost = true;
		
		return currentHost;
	}
	
	public void start(String msg) throws NumberFormatException, IOException, InterruptedException {
		if(msg==null||msg.length()==0) return;
		
		String [] temp = null;

		temp = msg.split("/");
		userIdFromQueue = temp[0];
		eFlagFromQueue = temp[1];
		hostIdFromQueue = temp[2];
		mobileIdFromQueue = temp[3];
		toDoFromQueue = temp[4];
		
		System.out.println("===================================================");
	    System.out.println("User id from queue: " + userIdFromQueue);
	    System.out.println("Emulator flag from queue: " + eFlagFromQueue);
	    System.out.println("Host id from queue: " + hostIdFromQueue);
	    System.out.println("Mobile id from queue: " + mobileIdFromQueue);
	    System.out.println("Todo from queue: " + toDoFromQueue);
	    System.out.println("===================================================");
	    
	    if(Integer.parseInt(eFlagFromQueue)==0) {
	    	if(toDoFromQueue.equalsIgnoreCase("on")) {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:
					System.out.println("Register mobile emulator to user");
					System.out.println("Power on mobile emulator");
					miaasManager.powerOnEmulator(Integer.parseInt(userIdFromQueue), Integer.parseInt(mobileIdFromQueue));
					break;
				case 1:
					System.out.println("Emulator already powered on");
					break;
				case 2:
					System.out.println("Power on mobile emulator");
					miaasManager.powerOnEmulator(Integer.parseInt(userIdFromQueue), Integer.parseInt(mobileIdFromQueue));
					break;
				default :
					break;
				}
			}
			else if(toDoFromQueue.equalsIgnoreCase("off")) {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:	
					System.out.println("Mobile emulator did not sign to any user");
					break;
				case 1:	
					System.out.println("Power off mobile emulator");
					miaasManager.powerOffEmulatorSimple(Integer.parseInt(userIdFromQueue), Integer.parseInt(mobileIdFromQueue));
					break;
				case 2:
					System.out.println("Mobile emulator already powered off");
					break;
				default :
					break;
				}
			}
			else if(toDoFromQueue.equalsIgnoreCase("ter"))  {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:
					System.out.println("Mobile did not sign to any user");
					break;
				case 1:
					System.out.println("Power off mobile emulator");
					System.out.println("Terminate mobile emulator");
					miaasManager.terEmulatorSimple(Integer.parseInt(userIdFromQueue), Integer.parseInt(mobileIdFromQueue));
					break;
				case 2:
					System.out.println("Terminate mobile emulator");
					miaasManager.terEmulatorSimple(Integer.parseInt(userIdFromQueue), Integer.parseInt(mobileIdFromQueue));
					break;
				default :
					break;
				}
			}
			else {
				System.err.println("Wrong request, please check again");
			}
	    } 
	    else {
	    	if(toDoFromQueue.equalsIgnoreCase("on")) {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:
					System.out.println("Register mobile device to user");
					System.out.println("Power on mobile device");
					break;
				case 1:
					System.out.println("Mobile device already powered on");
					break;
				case 2:
					System.out.println("Power on mobile device");
					break;
				default :
					break;
				}
			}
			else if(toDoFromQueue.equalsIgnoreCase("off")) {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:	
					System.out.println("Mobile device did not sign to any user");
					break;
				case 1:	
					System.out.println("Power off mobile device");
					break;
				case 2:
					System.out.println("Mobile device already powered off");
					break;
				default :
					break;
				}
			}
			else if(toDoFromQueue.equalsIgnoreCase("ter"))  {
				switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileIdFromQueue))) {
				case 0:
					System.out.println("Mobile device did not sign to any user");
					break;
				case 1:
					System.out.println("Power off mobile device");
					System.out.println("Terminate mobile device");
					break;
				case 2:
					System.out.println("Terminate mobile device");
					break;
				default :
					break;
				}
			}
			else {
				System.err.println("Wrong request, please check again");
			}
	    }
	}
}
