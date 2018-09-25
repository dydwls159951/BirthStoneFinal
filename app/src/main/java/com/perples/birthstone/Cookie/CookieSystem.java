package com.perples.birthstone.Cookie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Jinoo on 2018-06-17.
 */

public class CookieSystem {
    String mPath;
    public CookieSystem(String path){
        mPath = path+"/profile.txt";
    }
    public String getName(){
        CreateFile();
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            FileInputStream fis = new FileInputStream(mPath);//파일명
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            String result = "";
            while (str != null) {
                result += str;
                str = buffer.readLine();
            }
            buffer.close();
            fis.close();
            if ( result.length() > 0 ) {
                return result;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setName(String name){
        CreateFile();
        try {
            FileOutputStream fos = new FileOutputStream(mPath, false);// 저장모드
            PrintWriter out = new PrintWriter(fos);
            out.print(name);
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void CreateFile(){
        try {
            File f = new File(mPath);
            if(!f.exists())
                f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
