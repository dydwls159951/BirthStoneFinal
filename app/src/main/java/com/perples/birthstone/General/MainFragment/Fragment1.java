package com.perples.birthstone.General.MainFragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.perples.birthstone.CustomDialog;
import com.perples.birthstone.DataManager.DataSenderThread;
import com.perples.birthstone.General.CustomGeneralDialog;
import com.perples.birthstone.General.DialogFrag;
import com.perples.birthstone.General.MainGeneralActivity;
import com.perples.birthstone.General.SelectSeat;
import com.perples.birthstone.GlobalData.GlobalValue;
import com.perples.birthstone.Pregnant.MainPregnantActivity;
import com.perples.birthstone.Pregnant.Waiting;
import com.perples.recosample.R;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;

import java.util.HashMap;

@SuppressLint("ValidFragment")
public class Fragment1 extends Fragment implements View.OnClickListener {
    private RECOBeaconManager       mBeaconManager;
    private View                    mLayout;
    private TextView                mPeopleCountText;
    private TextView                mDetailText;
    private Handler                 mHandler;
    private CustomGeneralDialog     mCustomDialog;
    private RequestThread           mRequestThread;

    private Boolean                 mIsDialog;
    private Button                  test;

    public Fragment1(View layout, RECOBeaconManager beaconManager){
        mLayout = layout;
        //mBeaconList = recoBeacons;
        mBeaconManager = beaconManager;
        mIsDialog = false;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // xml 로 만들어준 프레그먼트를 자바 단에서 만들어줌
        ViewGroup rootGroup = (ViewGroup) inflater.inflate(R.layout.fragment_fragment1, container, false);
        Button test = rootGroup.findViewById(R.id.popupbutton);
        test.setOnClickListener(this);
        mPeopleCountText = (TextView)rootGroup.findViewById(R.id.fragment_count_textview);
        mDetailText = (TextView)rootGroup.findViewById(R.id.fragment_detail_textview);

        test = rootGroup.findViewById(R.id.popupbutton);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), SelectSeat.class);
                startActivity(intent);
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("RECORangingActivity", "Handler - run");
                if(msg.what == 0){
                    if(msg.getData().getString("result").startsWith("popup-") && !mIsDialog){
                        mIsDialog = true;
                        mCustomDialog = new CustomGeneralDialog(Fragment1.this.getContext(),
                            "[탄생석 요청]", // 제목
                            "탄생석을 요청하시겠어요?", // 내용
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        mCustomDialog.dismiss();
                                        mIsDialog = false;
                                        HashMap<String, String> data = new HashMap<String, String>();
                                        data.put("user_id", GlobalValue.USERID);
                                        data.put("select", "0");
                                        new DataSenderThread("match/region_match_delete.php",null, data).start();
                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mCustomDialog.dismiss();
                                        mIsDialog = false;
                                        HashMap<String, String> data = new HashMap<String, String>();
                                        data.put("user_id", GlobalValue.USERID);
                                        data.put("select", "1");
                                        new DataSenderThread("match/region_match_delete.php",null, data).start();
                                    }
                                }) ; // 오른쪽 버튼 이벤트
                        mCustomDialog.show();
                    }else if (msg.getData().getString("result").startsWith("count-")){
                        int peopleCount = Integer.parseInt((msg.getData().getString("result").replace("count-","")));
                        mPeopleCountText.setText(peopleCount + "명");
                        mDetailText.setText("주변에 "+peopleCount+"명의 임산부가 탑승 중이에요.");
                    }
                }
            }
        };

        mRequestThread = new RequestThread();
        mRequestThread.start();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
        return rootGroup;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.popupbutton :

                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestThread.StopThread();
        mRequestThread.interrupt();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class RequestThread extends Thread{
        private Boolean mIsRunning;

        public RequestThread(){
            mIsRunning = true;
        }

        @Override
        public void run() {
            super.run();
            try {
                while (mIsRunning) {
                    Thread.sleep(5000);

                    if(Thread.interrupted()){ //인터럽트가 들어오면 루프를 탈출합니다.
                        mIsRunning = false;
                        break;
                    }
                    Log.i("RECORangingActivity", "FragmentThread - run : "+mBeaconManager.getRangedRegions().size());
                    if(mBeaconManager.getRangedRegions().size() > 0){
                        for(RECOBeaconRegion beacon: mBeaconManager.getRangedRegions()) {
                            HashMap<String, String> data = new HashMap<String, String>();
                            data.put("user_id", GlobalValue.USERID);
                            data.put("user_type",GlobalValue.USERTYPE);
                            data.put("beacon_id",beacon.getProximityUuid());
                            data.put("beacon_major", String.valueOf(beacon.getMajor()));
                            data.put("beacon_minor", String.valueOf(beacon.getMinor()));
                            new DataSenderThread("match/get_people_request.php",mHandler, data).start();
                        }
                    }else{
                        mDetailText.setText("위치 정보를 확인중입니다.");
                    }
                }
            } catch (InterruptedException e) {//sleep 상태에서 인터럽트가 들어오면 exception 발생
                // TODO Auto-generated catch block
                mIsRunning = false;
            }
        }
        public void StopThread(){
            mIsRunning = false;
        }
    }
}