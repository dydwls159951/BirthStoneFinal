package com.perples.birthstone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.perples.birthstone.Cookie.CookieSystem;
import com.perples.birthstone.DataManager.DataSenderThreadBackup;
import com.perples.birthstone.General.MainGeneralActivity;
import com.perples.birthstone.GlobalData.GlobalValue;
import com.perples.birthstone.Pregnant.MainPregnantActivity;
import com.perples.recosample.R;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private String basicPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNameView = (EditText) findViewById(R.id.user_name);
        mPasswordView = (EditText) findViewById(R.id.user_password);
        basicPath = this.getFilesDir().getPath().toString();

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mNameView.getText().toString();
                String password = mPasswordView.getText().toString();
                new CookieSystem(basicPath).setName(name+"/"+password);
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
/* 자동 로그인
        String cookieData = new CookieSystem(basicPath).getName();
        if (cookieData != null){
            String[] dataSplit = cookieData.split("/");
            if(dataSplit.length > 1){
                String t_ID = dataSplit[0];
                String t_PW = dataSplit[1];
                if(cookieData != null){
                    showProgress(true);
                    mAuthTask = new UserLoginTask(t_ID, t_PW);
                    mAuthTask.execute((Void) null);
                }
            }
        }
*/
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mNameView.setError(null);
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(name,password);
            mAuthTask.execute((Void) null);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mName;
        private String mPassword;

        UserLoginTask(String name, String password) {
            mName = name;
            mPassword = password;
            GlobalValue.USERID = mName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HashMap<String, String> data = new HashMap<>();
            data.put("name", mName);
            data.put("password", mPassword);
            DataSenderThreadBackup loginDataThread = new DataSenderThreadBackup(0, null, data);
            loginDataThread.start();
            while (true) {
                if(!loginDataThread.getRunning()){
                    break;
                }
            }
            try {
                loginDataThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (loginDataThread.getResult()) {
                finish();
                if(GlobalValue.USERTYPE.equals("0")){
                    Intent intent = new Intent(LoginActivity.this, MainGeneralActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(LoginActivity.this, MainPregnantActivity.class);
                    startActivity(intent);
                }
            }
            // TODO: register the new account here.
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

