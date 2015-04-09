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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

/**
 * A helper class for handling Dropbox specific tasks
 * 
 * References: 
 * 	https://www.dropbox.com/developers/core/start/java
 *  http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/
 *  
 */
public class DropboxHelper {

	//Dropbox API instances representing an instance of CSE6331App on this machine
	private DbxAppInfo appInfo = null;
	private DbxClient client = null;
	private DbxRequestConfig config = null;

	/**
	 * EncryDecryptor instance representing used to handle encrypting and decrypting of files
	 */
	private EncryDecryptor encryDecryptor = null;

	
	/**
	 * Public constructor for DropboxHelper class.
	 * Initializes appInfor, config, and encryDecryptor instances for CSE6331App
	 */
	public DropboxHelper() {
		//creates appInfo by passing API_KEY and API_SECRET
		appInfo = new DbxAppInfo(Constants.DROPBOX_API_KEY, Constants.DROPBOX_API_SECRET);
		
		//creates config by passing clientIdentifier for CSE6331App
		config = new DbxRequestConfig(Constants.DROPBOX_CLIENT_IDENTIFIER, Locale.getDefault().toString());
		
		//creates encryDecryptor by passing a DES Shared Key
		encryDecryptor = new EncryDecryptor(Constants.DES_SHARED_KEY);
	}

	/**
	 * Looks for an existing Dropbox access token on this machine.
	 * If not found, returns a blank access token.
	 * @return accessToken
	 */
	private String getAccessToken() 
	{
		String accessToken = "";
		BufferedReader bufferedReader = null;
		try {
			//open the token file stored on this machine
			File tokenFile = new File(Constants.ACCESS_TOKEN_FILE);
			
			//create if not found
			if(!tokenFile.exists()) {
				tokenFile.createNewFile();
			} 
			
			//read the token file into a string
			bufferedReader = new BufferedReader(new FileReader(tokenFile));
			
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}

			accessToken = stringBuffer.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			//close system resources
			try {
				if(bufferedReader != null)
				{
					bufferedReader.close();
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//return the access token
		return accessToken;
	}
	
	/**
	 * Saves a new Dropbox access token to the accessToken file
	 * @param accessToken
	 */
	private void saveAccessToken (String accessToken)
	{
		PrintWriter out = null;
		try {
			//open the token file
			out = new PrintWriter(Constants.ACCESS_TOKEN_FILE);
			//print token to the file
			out.print(accessToken);
			//close the stream
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempt to authorize user.
	 * If user has already given access to CSE6331App before on this machine, it will use the same accessToken.
	 * otherwise, it will request user to authorize the CSE6331App by signing to their Dropbox account.
	 * It is a one time process, unless user deletes this CSE6331App from his/her system or modifies the accessToken file.
	 */
	public void authorizeUser() {

		//tries to get an existing Dropbox access token
		String accessToken = getAccessToken();
		
		//tries to connect to the Dropbox account of the user with the existing token
		client = new DbxClient(config, accessToken);
		
		try {
			//tries to get displayName associated with the linked account
			String displayName  = client.getAccountInfo().displayName;
			System.out.println("Welcome back " + displayName);
			System.out.println("------------------------------------------------\n");
		} catch (DbxException e1) 
		{
			//you are here because the existing access token failed to authenticate the user with CSE6331App
			//this may be because either the token file is not on this machine or has been modified
			DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

			//starts authorization process with Dropbox
			String authorizeUrl = webAuth.start();
			System.out.println("Please authorize CSE6311App by following these steps (One Time Process):");
			System.out.println("1. Go to: " + authorizeUrl);
			System.out.println("2. Click \"Allow\" (you might have to log in first)");
			System.out.println("3. Copy the authorization code.");

			try {
				//reads the authorization code entered by user
				String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

				//finish authorization process using the user-entered authorization code
				DbxAuthFinish authFinish = webAuth.finish(code);
				
				//get access token from the authorization
				accessToken = authFinish.accessToken;

				//connect again to the Dropbox account of the user with the new token
				client = new DbxClient(config, accessToken);
				String displayName  = client.getAccountInfo().displayName;
				
				//saves access token to the token file for future use
				saveAccessToken(accessToken);

				System.out.println("Thanks for authorizing CSE6331App. We have stored your authorization details on this machine.");
				System.out.println("------------------------------------------------");
				System.out.println("Linked account: "	+ displayName);
				System.out.println("------------------------------------------------");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lists files available on Dropbox account of the user in the direcotry Dropbox/Apps/CSE6331App
	 */
	public void listDropboxFiles() {
		
		DbxEntry.WithChildren listing;
		try {
			listing = client.getMetadataWithChildren("/");
			System.out.println("\n\n======= Files in your Dropbox/Apps/CSE6331App =======\n");
			for (DbxEntry child : listing.children) {
				System.out.println("  " + child.name + "\t\tlast modified: " + child.asFile().lastModified);
			}
			System.out.println("-------------------------------------------------");			
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Uploads the file to Dropbox from localFilePath after encrypting the files
	 * @param localFilePath
	 */
	public void uploadFile (String localFilePath) {
		
		FileInputStream inputStream = null;
		try {

			Thread.sleep(1000);
			//open the file to be uploaded 
			File inputFile = new File(localFilePath);

			//get the fileName from the path 
			String fileName = inputFile.getName();
			
			//create destFilePath where encrypted file will be stored before uploaded to the Dropbox
			String destFilePath = Constants.TEMP_FOLDER + "encrypted-" + fileName;
			
			//encrypt file stored at localFilePath and put encrypted file path at destFilePath
			encryDecryptor.encryptFile(localFilePath, destFilePath);
			
			//open encrypted file now so it can be uploaded to Dropbox
			File fileToUpload = new File(destFilePath);
			inputStream = new FileInputStream(destFilePath);
			
			//upload the encrypted file to the Dropbox account associated with the user
			DbxEntry.File uploadedFile = client.uploadFile("/" + fileName, DbxWriteMode.add(), fileToUpload.length(), inputStream);
			System.out.println("File Uploaded: " + uploadedFile.name + "(" + uploadedFile.asFile().numBytes + ") Bytes");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//close system resources
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Downloads the file with name fileName from Dropbox and store it on the destFilePath after decrypting it
	 * @param destFilePath
	 * @param fileName
	 */
	public void downloadFile(String destFilePath, String fileName) {
		FileOutputStream outputStream = null;
		try {
			//create temporary path where file will be stored after download from Dropbox and before decrption
			String sourceFilePath = Constants.TEMP_FOLDER + "downloaded-" + fileName;

			outputStream = new FileOutputStream(sourceFilePath);

			//download the file with name=fileName and put it in the temporary path
			System.out.println("Starting to download :" + fileName);
			DbxEntry.File downloadedFile = client.getFile("/" + fileName, null, outputStream);
			System.out.println("Downloaded: " + downloadedFile.name);

			//now decrypt the downloaded file and place it to the sourceFilePath
			encryDecryptor.decryptFile(sourceFilePath, destFilePath);
			Thread.sleep(500);
			String compared = encryDecryptor.compareFiles(Constants.DOWNLOAD_DIR+fileName, Constants.DOWNLOAD_DIR+fileName);
			System.out.println("File verification: " + compared);
			System.out.println("Decrypted file is available at: " + System.getProperty("user.dir") + "\\download-folder");
		} catch (NullPointerException e) {
			System.out.println("No file found with name: " + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//close system resources
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
