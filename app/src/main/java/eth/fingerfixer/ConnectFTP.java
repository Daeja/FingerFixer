package eth.fingerfixer;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

/**
 * Created by hyunx on 2017-06-28.
 */

public class ConnectFTP {
    private final String TAG = "Connect FTP";
    public FTPClient mFTPClient = null;

    public ConnectFTP() {
        mFTPClient = new FTPClient();
    }

    // Connect FTP
    public boolean ftpConnect(String host, String username, String password, int port) {
        boolean result = false;
        try {
            mFTPClient.connect(host, port);

            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                result = mFTPClient.login(username, password);
                mFTPClient.enterLocalPassiveMode();
            }
        } catch (Exception e) {
            Log.d(TAG, "Couldn't connect to host");
        }
        return result;
    }

    // Disconnect FTP
    public boolean ftpDisconnect() {
        boolean result = false;
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to disconnect with server");
        }
        return result;
    }

    // Get directory path
    public String ftpGetDirectory() {
        String directory = null;

        try {
            directory = mFTPClient.printWorkingDirectory();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Couldn't get current directory");
        }

        return directory;
    }

    // Modify directory path
    public boolean ftpChangeDirectory(String directory) {
        try {
            mFTPClient.changeWorkingDirectory(directory);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Couldn't change the directory");
        }
        return false;
    }

    // Get file list from FTP server
    public String[] ftpGetFileList(String directory) {
        String[] fileList = null;
        int i = 0;

        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(directory);

            fileList = new String[ftpFiles.length];

            for (FTPFile file : ftpFiles) {
                String fileName = file.getName();

                if (file.isFile()) {
                    //fileList[i] = "(File) " + fileName;
                    fileList[i] = fileName;
                } else {
                    fileList[i] = "(Directory) " + fileName;
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    // Create directory in FTP server
    public boolean ftpCreateDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.makeDirectory(directory);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't make the directory");
        }
        return result;
    }

    // Delete directory in FTP server
    public boolean ftpDeleteDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.removeDirectory(directory);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove directory");
        }
        return result;
    }

    // Delete file in FTP server
    public boolean ftpDeleteFile(String file) {
        boolean result = false;
        try {
            result = mFTPClient.deleteFile(file);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove the file");
        }
        return result;
    }

    // Rename file in FTP server
    public boolean ftpRenameFile(String from, String to) {
        boolean result = false;
        try {
            result = mFTPClient.rename(from, to);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't rename file");
        }
        return result;
    }

    // Download file from FTP server
    public boolean ftpDownloadFile(String srcFilePath, String desFilePath) {
        boolean result = false;
        try {
            mFTPClient.setFileType(BINARY_FILE_TYPE);
            mFTPClient.setFileTransferMode(BINARY_FILE_TYPE);

            FileOutputStream fos = new FileOutputStream(desFilePath);
            result = mFTPClient.retrieveFile(srcFilePath, fos);

            fos.close();
        } catch (Exception e) {
            Log.d(TAG, "Download failed");
        }
        return result;
    }

    // Upload file to FTP server
    public boolean ftpUploadFile(String srcFilePath, String desFileName, String desDirectory) {
        boolean result = false;
        try {
            FileInputStream fis = new FileInputStream(srcFilePath);
            if (ftpChangeDirectory(desDirectory)) {
                result = mFTPClient.storeFile(desFileName, fis);
            }
            fis.close();
        } catch (Exception e) {
            Log.d(TAG, "Couldn't upload the file");
        }
        return result;
    }
}
