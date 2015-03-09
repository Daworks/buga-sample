package buga.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import buga.util.SocketUtil;
import buga.util.logger.BugaLogger;
import buga.util.logger.BugaLoggerImpl;

public class BugaClient implements Runnable{
	
	Socket socket 			= null;
	
	String password 		= null;
	File file			= null;
	File encFile			= null;
	
	SocketUtil socketUtil 		= null;
	BugaLogger logger		= new BugaLoggerImpl();
	
	public BugaClient(Socket socket, String password, File file, File encFile, SocketUtil socketUtil){
		this.socket 	= socket;
		this.password 	= password;
		this.file 		= file;
		this.encFile 	= encFile;
		this.socketUtil = socketUtil;
	}
	
	@Override
	public void run() {
		
		try {
			checkValidation();
			
			sendMessage(file.getName());			// fileName 전송
			sendMessage("userTest");			// 유저 아이디 전송
			sendMessage(password);				// 패스워드 전송

			sendFile(file);					// text 파일 전송
			
			String resultToken = receiveMessage();		// 암호화 결과 token 받음
			
			logger.log("암호화 결과 Token => " + resultToken);
			
			if("0".equals(resultToken)){
				receiveFile(encFile);			// encrypt 파일 수신
			}
			
			closeSocket();
			
			logger.log(" 연결종료되었습니다.");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// 유효성 검증
	private void checkValidation() throws InterruptedException{
		
		if(file.isFile()){
			// 파일명에 한글 들어가면 JNA 인식 안됨
			if(file.getName().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
				logger.log("파일명에 한글이 들어가면 안됩니다."); 
				throw new InterruptedException(); 
			}
		}else{
			logger.log("파일이 아닙니다");
			throw new InterruptedException();
		}
		
		if(password.length() <= 0){
			logger.log("패스워드 길이가 0 이하 입니다.");
			throw new InterruptedException();
		}
	}
	
	// 파일 전송
	private void sendFile(File file) throws InterruptedException{
		try{
			FileInputStream fis 	= new FileInputStream(file);
			DataOutputStream dos 	= new DataOutputStream(socket.getOutputStream()); 
		
			long sendFileSize = file.length();
			socketUtil.sendMsg(sendFileSize + "", dos);
			logger.log("파일을 전송합니다 / size: " + sendFileSize);
			socketUtil.sendFile(fis, dos);			// 파일 전송
			logger.log("파일을 전송완료 / size: " + sendFileSize);
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
	
	// 소켓 닫기
	private void closeSocket(){
		try{
			socket.close();
		}catch(IOException e) {}
	}
	
	// 단일 실행 in WebApp
	public int execute(String bizNo, String userId, String password, File textFile, File encryptFile){
		try{
			sendMessage(file.getName());		// fileName 전송
			sendMessage(userId);			// 유저 아이디 전송
			sendMessage(password);			// 패스워드 전송

			sendFile(file);				// text 파일 전송
			
			String resultToken = receiveMessage();	// 암호화 결과 token 받음
			
			logger.log("[전자신고암호화] 암호화 결과 Token => " + resultToken);
			
			if("0".equals(resultToken)){
				receiveFile(encFile);		// encrypt 파일 수신
			}
			
			closeSocket();
			
			
			logger.log("[전자신고암호화]  연결종료되었습니다.");
				
			return 0;
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		
		
		return -1;
	}
	
}
