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
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This is the main program for the application.
 */
public class CloudApp 
{
	public static void main(String[] args)
	{
		//Creates an instance of the DropboxHelper
		DropboxHelper dropbox = new DropboxHelper();
		
		//Create some directories if not present

		//temp folder for storing intermediate files like encrypted-files before Dropbox upload and
		//encrypted-files after Dropbox download
		File temp = new File(Constants.TEMP_FOLDER);
		if(!temp.exists()) {
			temp.mkdir();
		}
		
		//upload directory. All the files dropped here are automatically encrypted and sent to Dropbox folder.
		temp = new File(Constants.UPLOAD_DIR);
		if(!temp.exists()) {
			temp.mkdir();
		}
		
		//download directory. It contains files downloaded from Dropbox after decryption
		temp = new File(Constants.DOWNLOAD_DIR);
		if(!temp.exists()) {
			temp.mkdir();
		}
		
		System.out.println("======= Welcome To CSE6331App ========\n\n");
		
		//Attempt to authorize user.
		//If user has already given access to CSE6331App before, it will use the same accessToken.
		//otherwise, it will request user to authorize the CSE6331App by signing to their Dropbox account.
		//It is a one time process, unless user deletes this CSE6331App from his/her system or modifies the accessToken file.
		dropbox.authorizeUser();
		
		String uploadDir = System.getProperty("user.dir") + "\\upload-folder";
		
		System.out.println("\n======= Directory to drop the files =======\n");
		System.out.println(uploadDir);
		System.out.println("------------------------------------------------");
		
		//starts a WatchService (requires Java 7) on the ./upload-folder so that new file CREATE event can he handled
		Thread th = new Thread(new FileWatcher(dropbox), "FileWatcher");
        th.start();
        
        while(true)
        {
			try {
				//lists the files available in Dropbox/Apps/CSE6331App directory on users' Dropbox account.
				dropbox.listDropboxFiles();
				System.out.println("Enter fileName to download from Dropbox or Enter 'exit':\n");
				
				String cmd = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
				if(cmd.equals("exit"))
				{
					break;
				}
				else
				{
					String destFilePath = Constants.DOWNLOAD_DIR + cmd;
					dropbox.downloadFile(destFilePath, cmd);
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
		System.out.println("=======Goodbye from CSE6331App ========\n\n");
		System.exit(0);
	}

}
