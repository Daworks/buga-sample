package buga.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @author hodys
 *
 */
public class SocketUtil {
	
	int bufferSize;
	
	public SocketUtil(int bufferSize){
		this.bufferSize = bufferSize;
	}
	
	// 단문 메시지 받기
	public synchronized String recvMsg(DataInputStream dis) throws IOException{
		return dis.readUTF();
	}
	
	// 단문 메시지 보내기
	public synchronized void sendMsg(String msg, DataOutputStream dos) throws IOException{
		dos.writeUTF(msg);
		dos.flush();
	}
	
	
	// 파일 전송
	public synchronized int sendFile(FileInputStream fis, DataOutputStream dos) throws IOException{
		int totalCount = 0;
		int count = 0;
		byte[] buffer = new byte[bufferSize];
		
		while((count = fis.read(buffer)) != -1){
			totalCount = totalCount + count;
			dos.write(buffer,0,count);
		}
		dos.flush();
		return totalCount;
	}
	
	// 파일 받기
	public synchronized int recvFile(FileOutputStream fos, DataInputStream dis, long fileSize) throws IOException{
		int totalCount 	= 0;
		int count 		= 0;
		byte[] buffer = new byte[bufferSize];
		
		while(totalCount < fileSize && (count = dis.read(buffer)) > 0){
			totalCount = totalCount + count;
			fos.write(buffer,0, count);
		}
		fos.flush();
		return totalCount;
	}
}
