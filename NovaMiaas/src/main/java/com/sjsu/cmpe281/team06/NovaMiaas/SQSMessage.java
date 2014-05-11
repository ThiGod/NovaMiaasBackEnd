package com.sjsu.cmpe281.team06.NovaMiaas;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class SQSMessage {
	MiaasManager miaasManager = new MiaasManager();
	HostManager hostManager = new HostManager();
	public AmazonSQS sqs;
	public String myQueueUrl = null;
	
	public SQSMessage() {
		sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest1 = Region.getRegion(Regions.US_WEST_1);
		sqs.setRegion(usWest1);
		myQueueUrl = "connectQueue1";
	}
	
	public void createQueue() {
		System.out.println("Creating a new SQS queue called: " + myQueueUrl);
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueueUrl);
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
	public void sendMessageToQueue(String msg) {
		createQueue();
        System.out.println("Sending a message to queue: " + myQueueUrl);
        System.out.println("Message: " + msg);
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, msg));
	}
	
	public String reciveMessageFromQueue() {
		String msg = null;
		String temp = null;
		System.out.println("Receiving messages from queue: " + myQueueUrl);
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            msg = message.getBody();
        }
        
        if(msg!=null) {
        	if(checkIfCurrentHost(msg)) {
        		temp = msg;
        	}    	
        	System.out.println("Deleting a message.");
            String messageRecieptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));          
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
		
		String [] temp;
		String userId, eFlag, hostId, mobileId, toDo;
		temp = msg.split("/");
		userId = temp[0];
		eFlag = temp[1];
		hostId = temp[2];
		mobileId = temp[3];
		toDo = temp[4];
		
	    System.out.println(userId);
	    System.out.println(eFlag);
	    System.out.println(hostId);
	    System.out.println(mobileId);
	    System.out.println(toDo);
	    
		if(toDo.equalsIgnoreCase("on")) {
			switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileId))) {
				case 0:
					System.out.println("Register mobile device to user");
					System.out.println("Power on mobile device");
					miaasManager.powerOnEmulator(Integer.parseInt(userId), Integer.parseInt(mobileId));
					break;
				case 1:
					System.out.println("Mobile already powered on");
					break;
				case 2:
					System.out.println("Power on mobile device");
					miaasManager.powerOnEmulator(Integer.parseInt(userId), Integer.parseInt(mobileId));
					break;
			}
		}
		if(toDo.equalsIgnoreCase("off")) {
			switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileId))) {
				case 0:
					System.out.println("Mobile did not sign to any user");
					break;
				case 1:
					System.out.println("Power off mobile device");
					miaasManager.powerOffEmulatorSimple(Integer.parseInt(userId), Integer.parseInt(mobileId));
					break;
				case 2:
					System.out.println("Mobile already powered off");
					break;
			}
		}
		if(toDo.equalsIgnoreCase("ter")) {
			switch (miaasManager.checkMobileStatus(Integer.parseInt(mobileId))) {
				case 0:
					System.out.println("Mobile did not sign to any user");
					break;
				case 1:
					System.out.println("Power off mobile device");
					System.out.println("Terminate mobile device");
					miaasManager.terEmulatorSimple(Integer.parseInt(userId), Integer.parseInt(mobileId));
					break;
				case 2:
					System.out.println("Terminate mobile device");
					miaasManager.terEmulatorSimple(Integer.parseInt(userId), Integer.parseInt(mobileId));
					break;
			}
		}
	}
	
	public Timestamp convertStringToTimeStamp(String ts) {
		Timestamp timestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date parsedDate = (Date) dateFormat.parse(ts);
			timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	public String test() {
		String msg = null;
		String temp = null;
		System.out.println("Receiving messages from queue: " + myQueueUrl);
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            msg = message.getBody();
        }
        try {
        JSONParser parser = new JSONParser();

        ContainerFactory containerFactory = new ContainerFactory() {
            public List creatArrayContainer() {
                return new LinkedList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }                     
        };

            Map json = (Map)parser.parse(msg, containerFactory);
            Iterator iter = json.entrySet().iterator();
            System.out.println("==iterate result==");
            Object entry = json.get("Message");
            temp = entry.toString();
            //System.out.println(entry.toString());
          } catch(ParseException pe) {
            System.out.println(pe);
        }
        return temp;
	}
}
