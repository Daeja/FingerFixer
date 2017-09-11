package eth.fingerfixer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import eth.fingerfixer.service.BTCTemplateService;
import eth.fingerfixer.utils.Constants;

// http://recipes4dev.tistory.com/43 참고 - 유환준

public class NoteMain extends AppCompatActivity {
    /**
     * bt
     */
    private BTCTemplateService mService;
    private Handler mActivityHandler = null;
    private int firstCount = 0;
    private int secondCount = 0;
    private TextView mTextBTState = null;
    private ImageView mImageView = null;
    private long tempo = 120L;
    private long time = 60000/4/tempo;

    Activity activity = this;

    FrameLayout[] frameLayout = new FrameLayout[4];
    LinearLayout[] linearLayout = new LinearLayout[4];
    Note note = null;

    // Division Image
    // 구분선 구현에 필요한 변수들 (9.11 추가-환준)
    ImageView division;
    final int startPoint = 190;
    final int move_gap = 105; // 초기값 24
    int left_margin = startPoint;

    boolean isPlay = false; // 현재 실행,정지 중인거 구분하는 변수
    ImageButton buttonPlay; // play 버튼 일시정지로 바꾸는데 이용
    RadioGroup radioGroup;  // 기본연주,자동연주 선택하는 라디오 그룹, onCreate에서 사용
    boolean isNormalMode = true; // 기본연주, 자동연주인거 구분하는 변수

    // Reference MusicNote class
    MusicNote musicNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        /**
         * bt
         */
        mActivityHandler = new ActivityHandler();
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
        mTextBTState = (TextView) findViewById(R.id.bt_state2);
        mTextBTState.setText("connected");
        mImageView = (ImageView) findViewById(R.id.status_title2);
        mImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));



        // 곡 제목 적어주기
        TextView songName = (TextView) findViewById(R.id.song_name);
        songName.setText(getIntent().getStringExtra("songName"));

        // 툴바 생성
        Toolbar noteToolbar1 = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(noteToolbar1);


        // 몇분의 몇박자인지 변수
        int topTempo, bottomTempo;
        topTempo = 4;   bottomTempo = 4;    // 임시로 여기서 초기화
        int noteCount = 16 / bottomTempo * topTempo + 1;    // 한 마디에 들어가는 음표의 갯수 (+1하는 이유는 몇분의 몇박자도 그려줘야 해서)

        // 악보 띄우는 레이아웃
        LinearLayout linearLayoutSet = (LinearLayout) findViewById(R.id.linearlayout_note_set);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayoutSet.getLayoutParams();
        params.topMargin = 33;
        params.leftMargin = 110;
        linearLayoutSet.setLayoutParams(params);

        LinearLayout linearLayoutSet2 = (LinearLayout) findViewById(R.id.linearlayout_note_set2);
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) linearLayoutSet2.getLayoutParams();
        params2.topMargin = 192;
        params2.leftMargin = 110;
        linearLayoutSet2.setLayoutParams(params2);
        //linearLayoutSet2.setBackground(ContextCompat.getDrawable(this, R.color.mainColor));

        LinearLayout linearLayoutSet3 = (LinearLayout) findViewById(R.id.linearlayout_note_set3);
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) linearLayoutSet3.getLayoutParams();
        params3.topMargin = 33;
        params3.leftMargin = 110;
        linearLayoutSet3.setLayoutParams(params3);

        LinearLayout linearLayoutSet4 = (LinearLayout) findViewById(R.id.linearlayout_note_set4);
        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) linearLayoutSet4.getLayoutParams();
        params4.topMargin = 192;
        params4.leftMargin = 110;
        linearLayoutSet4.setLayoutParams(params4);


        // 음표 띄우기

        frameLayout[0] = (FrameLayout) findViewById(R.id.framelayout_tempo_main); // 박자표
        frameLayout[1] = (FrameLayout) findViewById(R.id.framelayout_tempo_main2); // 박자표
        frameLayout[2] = (FrameLayout) findViewById(R.id.framelayout_tempo_main3); // 박자표
        frameLayout[3] = (FrameLayout) findViewById(R.id.framelayout_tempo_main4); // 박자표

        linearLayout[0] = (LinearLayout) findViewById(R.id.linearlayout_note_main1);
        //linearLayout[0].setBackground(ContextCompat.getDrawable(this, R.color.mainColor));
        linearLayout[1] = (LinearLayout) findViewById(R.id.linearlayout_note_main2);
        //linearLayout[1].setBackground(ContextCompat.getDrawable(this, R.color.mainColor));
        linearLayout[2] = (LinearLayout) findViewById(R.id.linearlayout_note_main3);
        //linearLayout[2].setBackground(ContextCompat.getDrawable(this, R.color.mainColor));
        linearLayout[3] = (LinearLayout) findViewById(R.id.linearlayout_note_main4);
        //linearLayout[3].setBackground(ContextCompat.getDrawable(this, R.color.mainColor));



        // 라디오그룹 리스너, 이벤트 처리
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_normal) {
                    isNormalMode = true;
                } else if (checkedId == R.id.radio_button_auto) {
                    isNormalMode = false;
                }
                stopButtonOnClick(findViewById(R.id.button_stop));
            }
        });

        // play 버튼 객체 연결
        buttonPlay = (ImageButton) findViewById(R.id.button_play);

        // parameter - 곡 명, Context
        musicNote = new MusicNote(getIntent().getStringExtra("songName"), getApplicationContext());


        // Get id from division
        // 구분선 객체 인플래이트 및 초기화 등등 (9.11 추가-유환준)
        division = (ImageView) findViewById(R.id.division);
        division.setX(left_margin);
        musicNote.current_location = 0;

        try{
            notedraw(note, firstCount, this, frameLayout, linearLayout);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void notedraw(Note note, int firstCount, Activity activity, FrameLayout[] frameLayout, LinearLayout[] linearLayout) {
        frameLayout[0].removeAllViews();
        frameLayout[1].removeAllViews();
        frameLayout[2].removeAllViews();
        frameLayout[3].removeAllViews();
        linearLayout[0].removeAllViews();
        linearLayout[1].removeAllViews();
        linearLayout[2].removeAllViews();
        linearLayout[3].removeAllViews();
        note = new Note(activity, frameLayout[0], 4, 4);
        for (int i = 0; i < musicNote.ARRNUM; i++) {
            note = new Note(activity, linearLayout[0],
                    musicNote.upperNote[firstCount][i].charAt(0),
                    musicNote.upperNote[firstCount][i].charAt(1),
                    musicNote.upperNote[firstCount][i].charAt(2),
                    musicNote.upperNote[firstCount][i].charAt(3),
                    musicNote.upperNote[firstCount][i].charAt(4));
        }

        note = new Note(activity, frameLayout[1], 4, 4);
        for (int i = 0; i < musicNote.ARRNUM; i++) {
            note = new Note(this, linearLayout[1],
                    musicNote.lowerNote[firstCount][i].charAt(0),
                    musicNote.lowerNote[firstCount][i].charAt(1),
                    musicNote.lowerNote[firstCount][i].charAt(2),
                    musicNote.lowerNote[firstCount][i].charAt(3),
                    musicNote.lowerNote[firstCount][i].charAt(4));
        }

        note = new Note(activity, frameLayout[2], 4, 4);
        for (int i = 0; i < musicNote.ARRNUM; i++) {
            note = new Note(activity, linearLayout[2],
                    musicNote.upperNote[firstCount+1][i].charAt(0),
                    musicNote.upperNote[firstCount+1][i].charAt(1),
                    musicNote.upperNote[firstCount+1][i].charAt(2),
                    musicNote.upperNote[firstCount+1][i].charAt(3),
                    musicNote.upperNote[firstCount+1][i].charAt(4));
        }

        note = new Note(activity, frameLayout[3], 4, 4);
        for (int i = 0; i < musicNote.ARRNUM; i++) {
            note = new Note(this, linearLayout[3],
                    musicNote.lowerNote[firstCount+1][i].charAt(0),
                    musicNote.lowerNote[firstCount+1][i].charAt(1),
                    musicNote.lowerNote[firstCount+1][i].charAt(2),
                    musicNote.lowerNote[firstCount+1][i].charAt(3),
                    musicNote.lowerNote[firstCount+1][i].charAt(4));
        }
    }
    /**
     * Service connection
     */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            // Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();
            //mServiceHandler = mService.mServiceHandler;

            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        //Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult

        if (!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

    }

    // 툴바 백 버튼 이벤트 처리
    public void backButtonOnClick(View v) {
        finish();
    }

    // toolbar2 play 버튼 이벤트 처리
    public void playButtonOnClick(View v) {
        if (isPlay == false) {
            isPlay = true;
            if (isNormalMode == true) {
                Toast.makeText(this, "기본연주 모드 시작", Toast.LENGTH_SHORT).show();
                basicPlay();
            } else {
                Toast.makeText(this, "자동연주 모드 시작", Toast.LENGTH_SHORT).show();
                //Play2(120);
                autoPlay(time);
            }
            buttonPlay.setBackground(getResources().getDrawable(R.drawable.button_pause));
        } else {
            Toast.makeText(this, "일시 정지 버튼클릭", Toast.LENGTH_SHORT).show();
            buttonPlay.setBackground(getResources().getDrawable(R.drawable.fab_play));
            isPlay = false;
        }
    }

    // toolbar2 stop 버튼 이벤트 처리
    public void stopButtonOnClick(View v) {
        Toast.makeText(this, "정지 버튼 클릭", Toast.LENGTH_SHORT).show();
        firstCount = 0;
        secondCount = 0;
        buttonPlay.setBackground(getResources().getDrawable(R.drawable.fab_play));
        isPlay = false;
        notedraw(note, firstCount, this, frameLayout, linearLayout);

        // 구분선 제자리로...
        // 9.11 추가-유환준
        left_margin = startPoint;
        division.setX(left_margin);
        musicNote.current_location = 0;
    }

    private void basicPlay() {
        if (isPlay == true) {
            mService.sendMessageToRemote(musicNote.upperNote[firstCount][secondCount] + "**" + musicNote.lowerNote[firstCount][secondCount]);
            secondCount++;

            // 다음줄로 넘어갈때
            if (secondCount == musicNote.ARRNUM) {
                firstCount++;
                notedraw(note, firstCount, this, frameLayout, linearLayout);
                secondCount = 0;
            }
            // 줄이 끝나면?
            if (firstCount == musicNote.upperNote.length) {
                firstCount = 0;
                secondCount = 0;
                try {
                    isPlay = false;
                    buttonPlay.setBackground(getResources().getDrawable(R.drawable.fab_play));
                    Toast.makeText(getApplicationContext(), "악보 끝", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void autoPlay(long time){
        Handler mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mService.sendMessageToRemote("1" + musicNote.upperNote[firstCount][secondCount] + "**" + musicNote.lowerNote[firstCount][secondCount]);
                secondCount++;

                // 다음줄로 넘어갈때
                if (secondCount == musicNote.ARRNUM) {
                    firstCount++;
                    secondCount = 0;
                    notedraw(note, firstCount, activity, frameLayout, linearLayout);
                }

                // 줄이 끝나면?
                if (firstCount == musicNote.upperNote.length) {
                    firstCount = 0;
                    secondCount = 0;

                    try {
                        isPlay = false;
                        buttonPlay.setBackground(getResources().getDrawable(R.drawable.fab_play));
                        Toast.makeText(getApplicationContext(), "악보 끝", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };
        if(isPlay==true)
            mhandler.sendEmptyMessageDelayed(0, time);
    }

    // Move right division 구분선 0.125초 마다 구분선 이동
    // 구분선 관련 메소드 (9.11 추가-환준)
    public void move_division() {
        if(musicNote.current_location == musicNote.ARRNUM)
        { musicNote.current_location = 0; left_margin = startPoint; }

        division.setX(left_margin);
        left_margin += move_gap;
        musicNote.current_location++;
    }

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                    if (mService != null) {
                        String deviceName = mService.getDeviceName();
                        if (deviceName != null) {
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
                    try {
                        if (msg.obj != null) {
                            if ("YES".equals(msg.obj.toString())) {
                                // 패킷을 받을때마다 이동
                                move_division();
                                basicPlay();
                            }else {
                                // 패킷을 받을때마다 이동
                                autoPlay(time);
                                move_division();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MESSAGE_WRITE_CHAT_DATA:
                    autoPlay(time);
                    break;
                case 9999:

                    break;
                // 악보가 끝났을 때
                case 999:
                    Toast.makeText(getApplicationContext(), "악보 끝", Toast.LENGTH_LONG).show();
                    buttonPlay.setBackground(getResources().getDrawable(R.drawable.fab_play));
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }    // End of class ActivityHandler

/*
    private class Play extends Thread{
        boolean stopflag;

        public Play(){
            stopflag = false;
        }

        public void stopPlay(){
            stopflag = true;
        }

        @Override
        public void run() {
            while(!stopflag) {
                mHandler.sendEmptyMessageDelayed(0, 60000/4*tempo);
            }
        }

        public void autoPlay(){
            mService.sendMessageToRemote("1" + musicNote.upperNote[firstCount][secondCount] + "**" + musicNote.lowerNote[firstCount][secondCount]);
            secondCount++;

            // 다음줄로 넘어갈때
            if (secondCount == musicNote.ARRNUM) {
                firstCount++;
                secondCount = 0;
            }

            // 줄이 끝나면?
            if (firstCount == musicNote.upperNote.length) {
                firstCount = 0;
                secondCount = 0;

                isPlay = false;
                stopflag = true;

                mActivityHandler.obtainMessage(999);
            }
        }

        Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    // 자동연주
                    case 0:
                        autoPlay();
                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }*/
}

