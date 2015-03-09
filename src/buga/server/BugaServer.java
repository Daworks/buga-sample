package buga.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import buga.util.BugaUtil;
import buga.util.CommonUtil;
import buga.util.SocketUtil;
import buga.util.logger.BugaLogger;
import buga.util.logger.BugaLoggerImpl;

public class BugaServer implements Runnable{
	File receiveTextFile 	= null;
	File sendEncyptFile 	= null;
	
	Socket socket 			= null;
	
	BugaUtil bUtil			= null;
	SocketUtil socketUtil	= null;
	BugaLogger logger		= new BugaLoggerImpl();		// logger
	
	String saveRoot 		= null; 
	
	public BugaServer(Socket socket, String saveRoot, BugaUtil bUtil, SocketUtil sockUtil){
		this.socket 		= socket;
		this.saveRoot 		= saveRoot;
		this.bUtil			= bUtil;
		this.socketUtil 	= sockUtil;
	}
	
	@Override
	public void run() {
		try{
			logger.log(socket.getInetAddress() + "로부터 연결 요청을 들어왔습니다");
			
			String fileName = receiveMessage();	// fileName 읽기
			String userId 	= receiveMessage();	// userId 읽기
			String password = receiveMessage();	// password 읽기
			
			long randomKey = CommonUtil.random(5);
			
			
			receiveTextFile = new File(saveRoot + userId + "_" + randomKey + "_" + fileName);
			sendEncyptFile 	= new File(saveRoot + userId + "_" + randomKey + "_" + fileName + ".enc");
			
			// 파일 받기
			receiveFile(receiveTextFile);
			
			if(receiveTextFile.exists()){
				int encryptResultCode = -1;
				
				// 파일 암호화
				encryptResultCode = bUtil.bugaFileEncrypt(0, receiveTextFile.getAbsolutePath(), sendEncyptFile.getAbsolutePath(), password, 1);	
				
				// 암호화 결과 token 발송
				sendMessage(encryptResultCode + "");	
				
				if(encryptResultCode == 0){
					logger.log("전자신고암호화가 성공적으로 처리 되었습니다");
					
					// 파일 전송
					sendFile(sendEncyptFile);
				}
			}
			
			logger.log(socket.getInetAddress() + " 의 연결이 성공적으로 종료되었습니다");
	
			System.gc();			// windows에서는 파일 삭제 안되는 버그 잇음. System.gc() 써줌
			
			logger.log("송수신 파일을 삭제합니다.");
			
			removeFile(receiveTextFile);
			removeFile(sendEncyptFile);
			
			logger.log("송수신 파일을 삭제 완료되었습니다.");
			
		}catch(InterruptedException e){
			e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	// 파일 전송
	private void sendFile(File file) throws InterruptedException{
		try{
			FileInputStream fis 	= new FileInputStream(file);
			DataOutputStream dos 	= new DataOutputStream(socket.getOutputStream()); 
		
			socketUtil.sendMsg(file.length() + "", dos);
			
			logger.log("파일을 전송합니다 / size: " + file.length());
			socketUtil.sendFile(fis, dos);			// 파일 전송
			logger.log("파일을 전송완료 / size: " + file.length());
		}catch(IOException e){
			logger.log("파일 전송 오류");
			throw new InterruptedException();
		}
	}
	
	// 파일 받기
	private void receiveFile(File destFile) throws InterruptedException{
		try{
			DataInputStream dis 	= new DataInputStream(socket.getInputStream());
			FileOutputStream fos 	= new FileOutputStream(destFile);
			
			long fileSize = Long.parseLong(socketUtil.recvMsg(dis));
			
			logger.log("파일을 수신합니다 / size: " + fileSize);
			socketUtil.recvFile(fos, dis, fileSize);			// 파일 다운
			logger.log("파일을 수신완료 / size: " + fileSize);
		}catch(IOException e){
			logger.log("파일 수신 오류");
			throw new InterruptedException();
		}
		
	}
	
	// 메시지 전송
	private void sendMessage(String msg) throws InterruptedException{
		try{
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			socketUtil.sendMsg(msg, dos);
		}catch(IOException e){
			logger.log("메시지 전송 오류");
			throw new InterruptedException();
		}
	}
	
	// 파일 삭제
	private void removeFile(File f){
		// f 랑 fileName 이 존재하고
		if(f.exists() && f.isFile()){
			f.delete();		// 삭제
			
			logger.log(f.getName() + " 파일은 삭제되었습니다   **********");
		}
	}
	
	// 메시지 받기
	private String receiveMessage() throws InterruptedException{
		try{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			return socketUtil.recvMsg(dis);
		}catch(IOException e){
			logger.log("메시지 수신 오류");
			throw new InterruptedException();
		}
	}
}
