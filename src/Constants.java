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

/**
 * A final Constant class storing all app constants.
 */
public final class Constants {

	/** Dropbox API_KEY for CSE6331App */
	public static final String DROPBOX_API_KEY = "************";
	
	/** Dropbox API_SECRET */
	public static final String DROPBOX_API_SECRET = "************";
	
	/** Dropbox Client Identifier string for CSE6331App requests */
	public static final String DROPBOX_CLIENT_IDENTIFIER = "CSE6331App/1.0";
	
	/** DES key which is used in encrypting and decrypting all the files */
	public static final String DES_SHARED_KEY = "cloud-cse6331-jyoti-salitra-app-uta";
	
	/** A directory on this system where user can drop the files and they will be uploaded to Dropbox account. */
	public static final String UPLOAD_DIR = "./upload-folder/";
	
	/** A directory on this system where files are downloaded from the Dropbox account */
	public static final String DOWNLOAD_DIR = "./download-folder/";
	
	/** A file that stores Dropbox access token of the user, if user has authorized CSE6331App at least once on this machine. */
	public static final String ACCESS_TOKEN_FILE = "./dropbox_access_token.txt";
	
	/** Temporary directory to store some intermediate files */
	public static final String TEMP_FOLDER = "./temp/";
}
