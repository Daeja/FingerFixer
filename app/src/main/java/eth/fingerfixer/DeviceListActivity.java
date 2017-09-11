/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eth.fingerfixer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;    // 등록된 디바이스 목록
    private ArrayAdapter<String> mNewDevicesArrayAdapter;       // 새로운 디바이스 목록

    Button mScanButton = null;  // 기기 검색 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("연결할 디바이스 선택");
        // 기기 리스트 액티비티 띄움
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // 등록된 디바이스 목록 출력
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        // 연결가능한 디바이스 목록 출력
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            //String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add("등록된 디바이스 없음");
        }

        // 디바이스 검색 버튼 연결 및 클릭
        mScanButton = (Button) findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                    mNewDevicesArrayAdapter.clear();    // 새로운 디바이스 목록 초기화
                    doDiscovery();  // 디바이스 검색하기
                    v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        try {
            unregisterReceiver(mReceiver);
        }  catch (IllegalArgumentException e){
        } catch (Exception e) {

        } finally {

        }
        super.onDestroy();
    }

    // 디바이스 검색
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // 맨 위 타이틀 변경 [ -> 디바이스 검색 중...]
        setProgressBarIndeterminateVisibility(true);
        setTitle("디바이스 검색 중...");


        // 밑에 새로운 타이틀 보이게함 [연결 가능한 디바이스]
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        //if (mBtAdapter.isDiscovering()) {
        //    mBtAdapter.cancelDiscovery();
        //}

        // 디바이스 검색할 때 브로드캐스트 전송 레지스터
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        // 디바이스 검색 시작
        mBtAdapter.startDiscovery();
        /*
        if(mBtAdapter.startDiscovery()) {
            Toast.makeText(getApplicationContext(), "검색시작", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(getApplicationContext(), "검색실패",Toast.LENGTH_LONG).show();
        */
    }
    // 목록에 뜬 디바이스 중 누를 때 발생
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 검색 중지
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            // 선택한 디바이스 MAC 주소를 얻어오고,
            String info = ((TextView) v).getText().toString();
            if(info != null && info.length() > 16) {
                String address = info.substring(info.length() - 17);
                Log.d(TAG, "User selected device : " + address);

                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished

    // 검색된 디바이스의
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 검색이 시작될 때
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            // 검색이 완료될 때
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 검색이 완료되면, Activity title을 변경한다. [디바이스 검색 중... ->디바이스를 선택하시오]
                setProgressBarIndeterminateVisibility(false);
                setTitle("연결할 디바이스 선택");
                // 검색된 기기가 없을 때
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    //String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add("검색된 디바이스 없음");
                }
                mScanButton.setVisibility(View.VISIBLE);
            // 기기를 검색했을 때
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 블루투스 장치를 intent를 통해 가져온다.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }

    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
