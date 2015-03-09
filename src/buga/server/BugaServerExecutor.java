package buga.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import buga.util.BugaUtil;
import buga.util.SocketUtil;
import buga.util.logger.BugaLoggerImpl;

public class BugaServerExecutor {
	public static void main(String[] args) {
		
		int port = 7777;
		
		int corePoolSize 	= 5;
		int maxPoolSize 	= 10;
		int keepAliveTime	= 15;
		BlockingQueue<Runnable> queueWorker = new ArrayBlockingQueue<Runnable>(10);
		
		String saveRoot = "C:\\socketTest\\";
		
		ThreadPoolExecutor executors = 
				new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queueWorker);
		
		BugaUtil bUtil 		= new BugaUtil();		// 부가세 Util
		SocketUtil sockUtil = new SocketUtil(1024*8);
		
		try{
			ServerSocket ss = new ServerSocket(port);
			System.out.println(BugaLoggerImpl.getTime() + " 서버가 준비되었습니다");
			
			while(true){
				Socket socket = ss.accept();
				
				while(executors.getActiveCount() == executors.getMaximumPoolSize()){
					Thread.sleep(100);
				}
				
				executors.execute(new BugaServer(socket, saveRoot, bUtil, sockUtil));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
