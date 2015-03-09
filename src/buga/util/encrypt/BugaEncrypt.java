package buga.util.encrypt;

import com.sun.jna.Library;

public interface BugaEncrypt extends Library{
	public int DSFC_EncryptFile(int handle, 
			String plainFilePathName, 
			String encryptFilePathName, 
			String password, 
			int option);
}
