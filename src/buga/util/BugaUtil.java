package buga.util;

import buga.util.encrypt.BugaEncrypt;

import com.sun.jna.Native;

public class BugaUtil {
	private final String fcyprtEsPath = "E:/Workspace/refactoringTest/jna-sample/src/native/fcrypt_es.dll";
	private final String magicCryptoPath = "E:/Workspace/refactoringTest/jna-sample/src/native/MagicCrypto.dll";
	
	private BugaEncrypt sharedLib = null;
	
	public BugaUtil(){
		System.load(magicCryptoPath);	// 의존성 dll 부터 호출 하여 메모리에 올림
		System.load(fcyprtEsPath);
		
		initialLibrary();
	}
	
	// get 암호화 dll path
	public String getFcryptEsPath(){
		return fcyprtEsPath;
	}
	
	// get 암호화 의존 dll path
	public String getMagicCryptoPath(){
		return magicCryptoPath;
	}
	
	private void initialLibrary(){
		sharedLib = (BugaEncrypt)Native.loadLibrary(fcyprtEsPath, BugaEncrypt.class);
		Native.synchronizedLibrary(sharedLib);
	}
	
	// 부가세 파일 암호화 
	public synchronized int bugaFileEncrypt(int handle, String filePath, String encFilePath, String password, int option){
		return sharedLib.DSFC_EncryptFile(handle, filePath, encFilePath, password, option);
	}
}
