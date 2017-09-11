package eth.fingerfixer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by hyunx on 2017-06-29.
 */

public class FTP_Thread {

    private ConnectFTP connectFTP;
    private static final String TAG = "DownloadActivity";
    private static String host = "ftp.hyunx5.co19.kr";
    private static String user = "ETH";
    private static String password = "ETH";
    private static int port = 21;

    private String dir_path;
    private String current_path;
    private String newFilePath;
    private String file_name;

    private String[] titleList = null;

    private File file;

    public FTP_Thread() {
        connectFTP = new ConnectFTP();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean status = false;
                    status = connectFTP.ftpConnect(host, user, password, port);
                    if (status == true) {
                        Log.d(TAG, "Connection Success");
                    } else {
                        Log.d(TAG, "Connection failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String FTP_get_path() {

        Thread t = new Thread() {
            public void run() {
                try {
                    current_path = connectFTP.ftpGetDirectory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            synchronized(this) {
                t.start();
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    current_path = connectFTP.ftpGetDirectory();

                    final Handler mHandler = new Handler(Looper.getMainLooper());

                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext.getApplicationContext(), current_path, Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */

        current_path = "/home/pi/ETH";

        return current_path;
    }

    public String[] FTP_get_file_list(String directory) {
        dir_path = directory;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    titleList = connectFTP.ftpGetFileList(dir_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            synchronized(this) {
                t.start();
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return titleList;
    }

    public Boolean FTP_download(final Context mContext, String title) {
        Boolean state = FALSE;
        file_name = title;

        Thread t = new Thread(new Runnable() {
            public void run() {
                file = new File(mContext.getFilesDir().getAbsolutePath() + "/");
                newFilePath = mContext.getFilesDir().getAbsolutePath() + "/";
                newFilePath += file_name;
                try {
                    file = new File(newFilePath);
                    file.createNewFile();

                    //Toast.makeText(mContext.getApplicationContext(), newFilePath, Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }

                connectFTP.ftpDownloadFile(current_path + "/" +  file_name, newFilePath);
            }
        });

        try {
            synchronized(this) {
                t.start();
                this.wait(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }
}
