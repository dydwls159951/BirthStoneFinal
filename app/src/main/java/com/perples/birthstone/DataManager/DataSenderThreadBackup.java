package com.perples.birthstone.DataManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.perples.birthstone.GlobalData.GlobalValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataSenderThreadBackup extends Thread {
    private final String    mBaseURL = "http://price1010.cafe24.com/Birth/";
    private HashMap         mSendData;
    private Handler         mHandler;
    private String          mURL;
    private Boolean         mRunning;
    private Boolean         mResult;
    public DataSenderThreadBackup(int type, Handler handler, HashMap<String,String> data){
        mRunning = false;
        mResult = false;
        mSendData = data;
        if( type == 0 ){
            mURL = mBaseURL + "userInfo/user_login.php";
        }
        else if( type == 1 ){
            mURL = mBaseURL + "Transfer/send_msg.php";
        }
        else if( type == 2 ){
            mURL = mBaseURL + "Transfer/receive_msg.php";
        }
        else{
            mURL = mBaseURL + "UserInfo/signout.php";
        }
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        mRunning = true;
        try{
            Map<String,Object> params = new LinkedHashMap<>();
            Iterator dataIt = mSendData.entrySet().iterator();
            while(dataIt.hasNext()){
                Map.Entry pair = (Map.Entry)dataIt.next();
                params.put((String) pair.getKey(), (String)pair.getValue());
                dataIt.remove(); // avoids a ConcurrentModificationException
            }
            StringBuilder postData = new StringBuilder();
            for(Map.Entry<String,Object> param : params.entrySet()) {
                if(postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL Url = new URL(mURL); // URL화 한다.
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); // URL을 연결한 객체 생성.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

/*
            String strCookie = conn.getHeaderField("Set-Cookie"); //쿠키데이터 보관
            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(URLEncoder.encode(writebuffer.toString(),"UTF-8"));
            outputStream.flush();
            outputStream.close();
*/
            ArrayList<String> result = new ArrayList<>();
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = null;
            String resultLine = "";
            int count = 0;
            while((line = rd.readLine()) != null) {
                Log.d("StringLine","Line-"+line);
                resultLine += line;
                if(!line.startsWith("E-")){
                    mResult = true;
                    String[] data = line.split(",");
                    GlobalValue.USERID = data[0];
                    GlobalValue.USERTYPE = data[1];
                    break;
                }
            }
            rd.close();
            if(mHandler != null){
                Bundle bundle = new Bundle();
                bundle.putString("result", resultLine);
                Message msg = new Message();
                msg.what = 0;
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }
/*
            if(mResult){
                Bundle bundle = new Bundle();
                bundle.putString("name", (String) params.get("name"));
                bundle.putString("password",(String) params.get("password"));
                Message msg = new Message();
                msg.what = 0;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }*/

        }catch(MalformedURLException | ProtocolException exception) {
            exception.printStackTrace();

        }catch(IOException io){
            io.printStackTrace();
        }
        mRunning = false;
    }
    public Boolean getRunning(){
        return mRunning;
    }

    public Boolean getResult() {
        return mResult;
    }
}
