package com.sjsu.cmpe281.team06.NovaMiaas;

public class MyEntity {
	public static final int HOST_ID = 1;
	public static final String SQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String SQL_URL = "";
	public static final String SQL_USERNAME = "";
	public static final String SQL_PASSWORD = "";
	public static final String SQL_NAME = "nova_miaas";
	public static final String UBUNTU_GENY_PATH = "/home/thigod/Documents/dev/genymotion/player --vm-name";
	public static final String UBUNTU_NEW_EMU_SH_PATH = "/home/thigod/Documents/dev/newEmulator.sh";
	public static final String WINDOWS_GENY_PATH = "cmd.exe /c cd C:/Dev/Genymotion & player --vm-name";
	public static String SEND_TO_PHP_QUEUE = "receivedfrom_Host";
	public static String RECEIVE_FROM_PHP_QUEUE = "sendto_Host";
	public static final String UBUNTU_GET_IP = "ifconfig eth0 | grep \"inet addr\" | awk -F\":\" {'print $2'} | awk -F\" \" {'print $1'}";
}
