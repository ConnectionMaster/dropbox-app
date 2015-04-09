# dropbox-app
A Java program to upload and download files on Dropbox using Dropbox API.

###System Requirements:
1. Java 7
1. dropbox-core-sdk-1.7.7.jar
2. jackson-core-2.2.4.jar


###Steps to Compile:
1. Put above .jar files in the classpath.
2. Go to CSE6331App/src and execute following command on the terminal   
    `javac *.java`


###Steps to Run:
1. Run following command        
    `java CloudApp`
2. Perform an one time Dropbox authorization for `CSE6331App`.
3. Drop the files in the upload-folder
4. For downloading, type the name of the file available on Dropbox and hit enter.
5. The app can upload and download any file format. The encryption and decryption is performned using DES algorithm. 
6. The files are verified using MD5 checksum algorithm.
7. For syncing between Dropbox and Google Drive, I am using cloudHQ service. The user can also use the same service for syncing two directories on Dropbox and Google Drive.
