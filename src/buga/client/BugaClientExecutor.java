package buga.client;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import buga.util.SocketUtil;
import buga.util.logger.BugaLoggerImpl;

public class BugaClientExecutor {
	public static void main(String[] args) {
		
		String serverIp = "127.0.0.1";		// ip
		int port 		= 7777;				// port
		String password = "888";			// password
		String filePath = "E:\\test.txt";	// file root
		
		File file 		= new File(filePath);
		File encFile 	= new File(filePath + ".enc");	// 암호화된 파일 예정 root
		try{
			if(!file.exists()) file.createNewFile();
			if(!encFile.exists()) encFile.createNewFile();
		}catch(IOException e){}
		
		
		int clientCount = 100;	// test client 갯수
		
		Thread[] threads 	= new Thread[clientCount];
		Socket[] sockets 	= new Socket[clientCount];

		try {
			System.out.println(BugaLoggerImpl.getTime() + " 서버에 연결중. 서버Ip: " + serverIp + " / port: " + port);
			
			for(int i = 0; i < threads.length; i++){
				sockets[i] = new Socket(serverIp, port);
				threads[i] = new Thread(new BugaClient(sockets[i], password, file, encFile, new SocketUtil(1024*8)));
				
				threads[i].start();
			}
		}catch (ConnectException e){
			System.out.println("서버가 닫혀 있습니다. 다시 열어 주세요.");
			e.printStackTrace();
		}catch (UnknownHostException e) {
			System.out.println("알 수 없는 host 정보입니다.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
