/**
 * -------------------------------------------
 * @author JYOTI SALITRA
 * UTA-ID: ***********
 * Course: CSE6331 Cloud Computing
 * Programming Assignment - 1 | Introduction to Cloud Storage
 * UTA Fall 2014
 * Submission Date: 09/14/2014
 * -------------------------------------------
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * A helper class to encrypt and decrypt files using DES encryption/decryption
 * 
 * References:
 * 	http://en.wikipedia.org/wiki/Data_Encryption_Standard
 *  http://www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
 *  http://examples.javacodegeeks.com/core-java/security/messagedigest/generate-a-file-checksum-value-in-java/
 *
 */
public class EncryDecryptor 
{
	private String sharedKey = "";
	
	/**
	 * EncryDecryptor constructor to create an instance of it
	 * Initializes DES symmetric key
	 * @param key
	 */
	public EncryDecryptor (String key)
	{
		sharedKey = key;
	}

	/**
	 * Encrypt file at sourceFilePath with DES encryption and stores it at destFilePath
	 * @param sourceFilePath
	 * @param destFilePath
	 */
	public void encryptFile(String sourceFilePath, String destFilePath)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			//initialize sourceFile and destFile
			fis = new FileInputStream(sourceFilePath);
			fos = new FileOutputStream(destFilePath);
			
			//initilize DES key spec with the DES symmetric key
			DESKeySpec dks = new DESKeySpec(sharedKey.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			
			//gets an instance of of DES Cipher 
			Cipher cipher = Cipher.getInstance("DES");
			
			//set cipher to ENCRYPT_MODE
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			
			//initialize cipherstream with cipher for encryption
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			
			//copy data from cipherStream to destFilePath
			copyBytes(cis, fos);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			//close system resources
			try {
				if(fis != null)
				{
					fis.close();
				}
				
				if(fos != null)
				{
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Decrypts file at sourceFilePath with DES decryption and stores the decrypted file at destFilePath
	 * @param sourceFilePath
	 * @param destFilePath
	 */
	public void decryptFile (String sourceFilePath, String destFilePath)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			//initialize sourceFile and destFile
			fis = new FileInputStream(sourceFilePath);
			fos = new FileOutputStream(destFilePath);
			
			//initilize DES key spec with the DES symmetric key
			DESKeySpec dks = new DESKeySpec(sharedKey.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			
			//gets an instance of of DES Cipher 
			Cipher cipher = Cipher.getInstance("DES");
			
			//set cipher to DECRYPT_MODE
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			
			//initialize cipherStream with cipher for decryption
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			
			//copy data from sourceFilePath to cipherStream
			copyBytes(fis, cos);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			//close system resources
			try {
				if(fis != null)
				{
					fis.close();
				}
				
				if(fos != null)
				{
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Compares two files located at filePathOne and filePathTwo
	 * @param filePathTwo
	 * @param filePathTwo
	 * @throws IOException
	 */
	public String compareFiles(String filePathOne, String filePathTwo) throws IOException {
		String checkSum1 = checkSum(filePathOne);
		String checkSum2 = checkSum(filePathTwo);
		if(checkSum1.equals(checkSum2))
		{
			return "SUCCESS";
		}
		else
		{
			return "FAILED";
		}
	}
	
	/**
	 * Copies bytes from one inputStream to an outputStream.
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	private void copyBytes(InputStream input, OutputStream output) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = input.read(bytes)) != -1) {
			output.write(bytes, 0, numBytes);
		}
		output.flush();
	}

	/**
	 * Calculates the checkSum of the file located at filepath
	 * @param path
	 * @return checkSum
	 */
	private String checkSum (String filepath) {
		
		String checksum = null;
		
		FileInputStream fileInput = null;
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            //open file
			fileInput = new FileInputStream(filepath);
			byte[] dataBytes = new byte[1024];
	
			int bytesRead = 0;
	
			//read file byte by byte and updated messageDigest 
			while ((bytesRead = fileInput.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, bytesRead);
			}
			
			//get digestBytes from the messageDigest
			byte[] digestBytes = md.digest();
            
            BigInteger number = new BigInteger(1, digestBytes);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			//close the resources
			try {
				if(fileInput != null)
				{
					fileInput.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return checksum;
	}

}
