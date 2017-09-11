package eth.fingerfixer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

import eth.fingerfixer.bluetooth.BluetoothManager;
import eth.fingerfixer.service.BTCTemplateService;
import eth.fingerfixer.utils.AppSettings;
import eth.fingerfixer.utils.Constants;

import static java.lang.Boolean.FALSE;

// http://recipes4dev.tistory.com/43 참고

public class NoteList extends AppCompatActivity {

    /************************************************************************
     * Bluetooth 추가(시작)
     ***********************************************************************/
    // Debugging
    private static final String TAG = "Main";

    // Context, System
    private Context mContext;
    private BTCTemplateService mService;
    private ActivityHandler mActivityHandler;
    private BluetoothAdapter btAdapter;
    private TextView mTextBTState = null;
    private ImageView mImageView = null;

    /************************************************************************
     * Bluetooth 추가(끝)
     ***********************************************************************/

    String selectedNote = null; // 선택된 악보
    long backKeyPressedTime; //백버튼 클릭 시간
    boolean state = FALSE;

    ListView listview;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        /************************************************************************
         * Bluetooth 추가(시작)
         ***********************************************************************/
        // 앱이 블루투스를 사용할 수 있도록 권한 부여 (6.0버전이상에서)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        //----- System, Context
        mContext = this;	//.getApplicationContext();
        mActivityHandler = new ActivityHandler();
        AppSettings.initializeAppSettings(mContext);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "블루투스 기능을 지원하지 않음", Toast.LENGTH_LONG).show();
        }
        mTextBTState = (TextView) findViewById(R.id.bt_state);
        mTextBTState.setText("waiting...");
        mImageView = (ImageView) findViewById(R.id.status_title);
        mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));

        /************************************************************************
         * Bluetooth 추가(끝)
         ***********************************************************************/

        try {
            // Create file
            /*
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.getFilesDir().getAbsolutePath() + "/test.eth", true));
            bw.write("4/4hgdfheidceidcg");
            bw.close();
            */

            Toast.makeText(this, "Dir Path => " + this.getFilesDir().getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        String[] titleList = null;

        try {
            FilenameFilter fileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("eth");
                }
            };

            File file = new File(this.getFilesDir().getAbsolutePath() + "/");
            //Context.getFilesDir().getPath()           => internal storage directory
            //getExternalStorageDirectory().getPath()   => external sdcard directory

            if (!file.exists())
                file.mkdirs();

            File[] files = file.listFiles(fileFilter);

            titleList = new String[files.length];

            for (int i = 0; i < files.length; i++) {
                titleList[i] = files[i].getName();
            }

            //Toast.makeText(getApplicationContext(), "파일 읽기 완료", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "파일 읽기 실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // File read
        /*
        for (int i = 0; i < titleList.length; i++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(this.getFilesDir().getAbsolutePath() + "/" + titleList[i]));
                String readStr = "";
                String str = null;

                while (((str = br.readLine()) != null)) {
                    readStr += str + "\n";
                }

                br.close();

                Toast.makeText(this, titleList[i] + " => " + readStr.substring(0, readStr.length() - 1), Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not Found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        backKeyPressedTime = System.currentTimeMillis();    // 백버튼 클릭 초기화

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        // 아이템 추가
        for (int i = 0; i < titleList.length; i++) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_level1), titleList[i], "ETH");
        }

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                Drawable iconDrawable = item.getLevel();

                // TODO : use item data.
                selectedNote = titleStr;
            }
        });
        /**
         * bt
         */
        doStartService();
    }

    @Override
    public void onBackPressed() {
        //1번째 백버튼 클릭
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        //2번째 백버튼 클릭 (종료)
        else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    // 툴바에 메뉴(다운로드 버튼) 인플레이트
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // 툴바 다운로드 버튼 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu:
                Intent intent = new Intent(this, DownloadActivity.class);
                startActivity(intent);
                break;
            /************************************************************************
             * Bluetooth 추가(시작)
             ***********************************************************************/
            case R.id.action_scan:
                if (!btAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }else
                    doScan();
                return true;
        }
        return false;
    }

    // Play 플로팅 버튼 이벤트 처리
    public void fabPlayOnClick(View v) {
        if (selectedNote == null)
            Toast.makeText(this, "악보를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        else {
            if(mService.getState() == BluetoothManager.STATE_CONNECTED) {
                Toast.makeText(this, selectedNote + "가 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, NoteMain.class);
                intent.putExtra("songName", selectedNote);
                startActivity(intent);
            }else{
                Toast.makeText(this, "블루투스를 연결해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Delete 플로팅 버튼 이벤트 처리
    public void fabDeleteOnClick(View v) {
        if (selectedNote == null)
            Toast.makeText(this, "악보를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        else
            deleteDialog();
    }

    // Refresh 플로팅 버튼 이벤트 처리
    public void fabRefreshOnClick(View v) {
        String[] titleList = null;

        try {
            Toast.makeText(this, "Dir Path => " + this.getFilesDir().getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            FilenameFilter fileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("eth");
                }
            };

            File file = new File(this.getFilesDir().getAbsolutePath() + "/");

            File[] files = file.listFiles(fileFilter);

            titleList = new String[files.length];

            for (int i = 0; i < files.length; i++) {
                titleList[i] = files[i].getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        // 아이템 추가
        for (int i = 0; i < titleList.length; i++) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_level1), titleList[i], "ETH");
        }

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                Drawable iconDrawable = item.getLevel();

                // TODO : use item data.
                selectedNote = titleStr;
            }
        });
    }

    // 삭제할 때 물어보는 창
    private void deleteDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("악보를 삭제하시겠습니까?").setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File file = new File(getFilesDir().getAbsolutePath() + "/" + selectedNote);

                                if (file.delete()) {
                                    Toast.makeText(getBaseContext(), selectedNote + "을(를) 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), selectedNote + "을(를) 삭제하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    /************************************************************************
     * Bluetooth 추가(시작)
     ***********************************************************************/

    /*****************************************************
     *	Private methods
     ******************************************************/

    /**
     * Service connection
     */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here. Do not initialize while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    /**
     * Start service if it's not running
     */
    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BTCTemplateService.class));
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Stop the service
     */
    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();
        stopService(new Intent(this, BTCTemplateService.class));
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult

        if (!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

    }

    private void finalizeActivity() {
        if(!AppSettings.getBgService()) {
            doStopService();
        } else {

        }
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    /*****************************************************
     *	Public classes
     ******************************************************/

    /**
     * Receives result from external activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Bluetooth is not enabled. Set the bluetooth on at phone settings.", Toast.LENGTH_SHORT).show();
                }
                break;
        }	// End of switch(requestCode)
    }



    /*****************************************************
     *	Handler, Callback, Sub-classes
     ******************************************************/

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    mTextBTState.setText("initializing...");
                    mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    mTextBTState.setText("waiting...");
                    mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    mTextBTState.setText("connectiong...");
                    mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            mTextBTState.setText("connected");
                            mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    mTextBTState.setText("Error");
                    mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    //if(msg.obj != null) {
                    //    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    //}
                    break;

                case Constants.MESSAGE_WRITE_CHAT_DATA:
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler
}
