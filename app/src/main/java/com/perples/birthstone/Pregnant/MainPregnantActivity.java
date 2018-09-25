/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Perples, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.perples.birthstone.Pregnant;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.perples.birthstone.CustomDialog;
import com.perples.birthstone.DataManager.DataSenderThread;
import com.perples.birthstone.General.MainGeneralActivity;
import com.perples.birthstone.GlobalData.GlobalValue;
import com.perples.birthstone.Cookie.CookieSystem;
import com.perples.birthstone.LoginActivity;
import com.perples.recosample.R;
import com.perples.birthstone.RecoActivity;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MainPregnantActivity extends RecoActivity implements RECORangingListener, View.OnClickListener {
    private BluetoothManager        mBluetoothManager;
    private BluetoothAdapter        mBluetoothAdapter;

    private View                    mLayout;
    private View                    mRequestBut;
    private TextView                mDetailText;
    private TextView                mCountText;
    private ImageView               mLogoutBut;
    private CustomDialog mCustomDialog;
    private RECOBeacon              mRecentBeacon;

    private Boolean                 mIsRegion = false;

    private long                    mScanPeriod = 1000L;
    private long                    mSleepPeriod = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pregnant);
        Toolbar toolbar= findViewById(R.id.toolbar_pregnant);
        toolbar.setBackgroundResource(R.drawable.tabbar2);
        mLayout = findViewById(R.id.main_layout);
        mDetailText = (TextView)findViewById(R.id.main_detail_info_textview);
        mCountText = (TextView)findViewById(R.id.main_count_textview);
        mLogoutBut = (ImageView) findViewById(R.id.main_logout_button);
        mRequestBut = (Button) findViewById(R.id.main_request_button);
        mLogoutBut.setOnClickListener(this);
        mRequestBut.setOnClickListener(this);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, GlobalValue.REQUEST_ENABLE_BT);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestLocationPermission();
            }
        }

        mRecoManager.setRangingListener(this);

        mRecoManager.setScanPeriod(mScanPeriod);
        mRecoManager.setSleepPeriod(mSleepPeriod*10);
        mRecoManager.bind(this);
        mRecentBeacon = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_logout_button :
                new CookieSystem(MainPregnantActivity.this.getFilesDir().getPath().toString()).setName("");
                //finish();
                Intent intent = new Intent(MainPregnantActivity.this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.main_left_button :
                break;

            case R.id.main_right_button :
                break;

            case R.id.main_request_button :
                if( mIsRegion ){
                    mCustomDialog = new CustomDialog(this,
                            "[탄생석 요청]", // 제목
                            "탄생석을 요청하시겠어요?", // 내용
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(mRecentBeacon != null){
                                        HashMap<String, String> data = new HashMap<String, String>();
                                        data.put("user_id", GlobalValue.USERID);
                                        data.put("user_type",GlobalValue.USERTYPE);
                                        data.put("beacon_id",mRecentBeacon.getProximityUuid());
                                        data.put("beacon_major", String.valueOf(mRecentBeacon.getMajor()));
                                        data.put("beacon_minor", String.valueOf(mRecentBeacon.getMinor()));
                                        new DataSenderThread("match/region_user_request.php",null, data).start();
                                    }
                                    mCustomDialog.dismiss();

                                    Intent intent = new Intent(MainPregnantActivity.this, Waiting.class);
                                    startActivity(intent);
                                }
                            }, // 왼쪽 버튼 이벤트
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mCustomDialog.dismiss();
                                }
                            }); // 오른쪽 버튼 이벤트
                    mCustomDialog.show();
                }
                break;
            default :
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalValue.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
            //finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void requestLocationPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, GlobalValue.REQUEST_LOCATION);
            return;
        }

        Snackbar.make(mLayout, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainPregnantActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, GlobalValue.REQUEST_LOCATION);
                    }
                })
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stop(mRegions);
        this.unbind();
    }
    @Override
    public void onServiceConnect() {
        this.start(mRegions);
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
        mRecoManager.setDiscontinuousScan(GlobalValue.DISCONTINUOUS_SCAN);
        this.start(mRegions);
    }
    @Override
    protected void start(ArrayList<RECOBeaconRegion> regions) {
        Log.i("MainPregnantActivity", "start");

        for(RECOBeaconRegion region : regions) {
            try {
                region.setRegionExpirationTimeMillis(10*1000L);
                mRecoManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECOMonitoringActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("MainPregnantActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.i("MainPregnantActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("MainPregnantActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }
    private void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("MainPregnantActivity", "Remote Exception");
            e.printStackTrace();
        }
    }
    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        Log.i("RECORangingActivity", ""+errorCode);
        return;
    }
    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
        Log.i("RECORangingActivity", "didRangeBeaconsInRegion() region: " + recoRegion.getUniqueIdentifier() + ", number of beacons ranged: " + recoBeacons.size());
        if(recoBeacons.size() > 0){
            RECOBeacon topBeacon = null;
            for(RECOBeacon  beacon: recoBeacons) {
                if ( topBeacon != null ){
                    if ( beacon.getRssi() > topBeacon.getRssi() ){
                        topBeacon = beacon;
                    }
                }else{
                    topBeacon = beacon;
                }
            }

            mRecentBeacon = topBeacon;
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("user_id",GlobalValue.USERID);
            data.put("user_type",GlobalValue.USERTYPE);
            data.put("beacon_id",topBeacon.getProximityUuid());
            data.put("beacon_major", String.valueOf(topBeacon.getMajor()));
            data.put("beacon_minor", String.valueOf(topBeacon.getMinor()));
            new DataSenderThread("match/region_user_register.php",null, data).start();
            mIsRegion = true;
            mDetailText.setText("요청 범위 내에 있습니다.");
        }else{
            if(mIsRegion){
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("user_id",GlobalValue.USERID);
                new DataSenderThread("match/region_user_delete.php",null, data).start();
            }
            mIsRegion = false;
            mDetailText.setText("요청 위치를 벗어났습니다.");
        }
    }
    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {
        Log.i("RECORangingActivity", ""+recoErrorCode);
    }

}
