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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * A Thread to watch ./upload-folder for any new files and uploaded them to dropbox after encrypting them
 * References:
 * 	http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * 	http://www.codejava.net/java-se/file-io/file-change-notification-example-with-watch-service-api
 *  
 */
public class FileWatcher implements Runnable {

	//WatchService instance (Java 7)
	private WatchService myWatcher;
	
	//An instance of DropboxHelper
	private DropboxHelper dropbox;
	
	/**
	 * FileWatcher constructor that accepts DropboxHelper instance 
	 * @param dropbox
	 */
	public FileWatcher(DropboxHelper dropbox){
		this.dropbox = dropbox;
	}

	@Override
	public void run() {
		
		try {
			//get the path of the directory ./upload-folder to watch
			Path toWatch = Paths.get(Constants.UPLOAD_DIR);
			
			//get new watchService instance from the file system
			myWatcher = toWatch.getFileSystem().newWatchService();
			
			//register path with watchService for the CREATE event
			//this allows to watch the files which are newly created/dropped in this directory
			toWatch.register(myWatcher, StandardWatchEventKinds.ENTRY_CREATE);
			
			//wait for any CREATE event (or file drop)
			WatchKey key = myWatcher.take();
			
			//one more more files are created in the toWatch path
			//loop one by one
			while (key != null) {
				
				//loop over polled events and check for each new file in the directory
				for (WatchEvent<?> event : key.pollEvents()) 
				{
					System.out.printf("\nFound a new file: %s\n", event.context());
					System.out.printf("Uploading to Dropbox: %s\n", event.context());
					//upload this file to the Dropbox
					dropbox.uploadFile(Constants.UPLOAD_DIR + event.context());
				}
				
				//reset the key
				key.reset();
				dropbox.listDropboxFiles();
				System.out.println("Enter fileName to download from Dropbox or Enter 'exit':\n");
				
				//wait for any CREATE event (or file drop)
				key = myWatcher.take();
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping thread");
	}

}
