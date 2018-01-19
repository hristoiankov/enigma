package enigma.backend;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionSystem {
	
	public static String ENC_ALGORITHM = "AES/CBC/PKCS5Padding";
	public static String HASH_ALGORITHM = "SHA-1";
	public static int ENC_IV_BYTES = 16;   // 128 bits
	public static int ENC_SALT_BYTES = 16; // 128 bits
	public static int ENC_BYTES = 16;      // 128 bits
	private static final Random RANDOM = new SecureRandom();
	
	//---------------------------------------
	// Data format:
	// [salt][iv][data]
	//---------------------------------------
	
	public static void encryptFile(File rawfile, String password) {
		
		
		
	}
	
	public static void decryptFile(File encryptedFile, String password, File outfile) {
			
		
	}
	
	/*public static String encryptText(String inData, String password) {
		byte[] passwordBytes = password.getBytes();
		byte[] inDataBytes = inData.getBytes();
		byte[] encryptedData = encryptData(inDataBytes, passwordBytes);
		String encryptedText = new Base64.Encoder().encode(encryptedData);
		return encryptedText;
	}
	
	public static String decryptText(String encryptedText, String password) {
		byte[] encryptedData = Base64.getDecoder().decodeBuffer(encryptedText);
		byte[] passwordBytes = password.getBytes();
		byte[] decryptedData = decryptData(encryptedData, passwordBytes);
		
	}*/
	
	// store this as the first 16 bytes in the file
	private static byte[] default_iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public static byte[] getKeyDigest(byte[] passwordBytes, byte[] salt) {
		MessageDigest sha;
		byte[] hash = passwordBytes;
		try {
			sha = MessageDigest.getInstance(HASH_ALGORITHM);
			byte[] saltedPassword = saltThePassword(passwordBytes, salt);
			hash = sha.digest(saltedPassword);
			hash = Arrays.copyOf(hash, ENC_BYTES); // 128 bits
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return hash;
	}
	
	/**
	 * Encrypt inData with password to outdata
	 * @return
	 */
	public static byte[] encryptData(byte[] inData, byte[] passwordBytes) {
		// salt the password
		byte[] salt = generateSalt();
		byte[] iv = generateIv();
		byte[] hash = getKeyDigest(passwordBytes, salt);
		
		Key key = new SecretKeySpec(hash, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
			encryptedData = cipher.doFinal(inData);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException |
				 InvalidKeyException | IllegalBlockSizeException | 
				 BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
		// Data format:
		// [salt][iv][data]
		//
		// prepend the data with the generated iv
		// and by the salt
		byte[] prependedData = prependData(salt, iv);
		prependedData = prependData(prependedData, encryptedData);
		
		return prependedData;
	}
	
	public static byte[] prependData(byte[] prefixData, byte[] data) {
		if(prefixData == null || data == null) return null;
		byte[] prependedData = new byte[prefixData.length + data.length];

		System.arraycopy(prefixData, 0, prependedData, 0, prefixData.length);
		System.arraycopy(data, 0, prependedData, prefixData.length, data.length);
		
		return prependedData;
	}
	
	public static byte[] generateSalt() {
		byte[] salt = new byte[ENC_SALT_BYTES];
		RANDOM.nextBytes(salt);
		return salt;
	}
	
	public static byte[] generateIv() {
		byte[] iv = new byte[ENC_IV_BYTES];
		RANDOM.nextBytes(iv);
		return iv;
	}
	
	public static byte[] saltThePassword(byte[] password, byte[] salt) {
		// append the salt to the password
		// [password][salt]
		//
		byte[] saltedPassword = new byte[password.length + salt.length];
		System.arraycopy(password, 0, saltedPassword, 0, password.length);
		System.arraycopy(salt, 0, saltedPassword, password.length, salt.length);
		return saltedPassword;
	}
	
	public static byte[] decryptData(byte[] encryptedData, byte[] passwordBytes) {
		
		//-----------------
		// Retrieve data from the provided file
		
		// get the salt
		byte[] salt = new byte[ENC_SALT_BYTES];
		System.arraycopy(encryptedData, 0, salt, 0, ENC_SALT_BYTES);
		
		// get the iv
		byte[] iv = new byte[ENC_IV_BYTES];
		System.arraycopy(encryptedData, ENC_SALT_BYTES, iv, 0, ENC_IV_BYTES);
		
		// get the data
		byte[] encData = new byte[encryptedData.length - (ENC_SALT_BYTES + ENC_IV_BYTES)];
		System.arraycopy(encryptedData, ENC_SALT_BYTES + ENC_IV_BYTES, encData, 0, encData.length);
		
		//------------------
		
		byte[] hash = getKeyDigest(passwordBytes, salt);
		Key key = new SecretKeySpec(hash, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		
		byte[] decryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
			decryptedData = cipher.doFinal(encData);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException |
				 InvalidKeyException | IllegalBlockSizeException | 
				 BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return decryptedData;
	}

}
