package com.example.projecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_pass;
    private Button btn_login, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        class CustomTask extends AsyncTask<String, Void, String> {
            String sendMsg, receiveMsg;
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String str;
                    URL url = new URL("http://34.64.201.195:8080/GraduationPjt/login.jsp");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                    sendMsg = "id="+strings[0]+"&pwd="+strings[1]+"&type="+strings[2];
                    osw.write(sendMsg);
                    osw.flush();
                    if(conn.getResponseCode() == conn.HTTP_OK) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {
                            buffer.append(str);
                        }
                        receiveMsg = buffer.toString();

                    } else {
                        Log.i("통신 결과", conn.getResponseCode()+"에러");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return receiveMsg;
            }
        }

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        //회원가입 버튼을 클릭 시 수행
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼 클릭 시 수행
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_login :
                        String id = et_id.getText().toString();
                        String pwd = et_pass.getText().toString();
                        try {
                            String result;
                            result  = new CustomTask().execute(id,pwd,"login.jsp").get();
                            Log.i("리턴 값",result);
                            if(result.equals(">login success!!")) {
                                Toast.makeText(LoginActivity.this,"로그인되었습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
//                            } else if(result.equals(">login fail!!")) {
//                                Toast.makeText(LoginActivity.this,"아이디 또는 비밀번호가 틀렸음",Toast.LENGTH_SHORT).show();
//                                et_id.setText("");
//                                et_pass.setText("");
                            } else if(result.equals(">login fail!!")){
                                Toast.makeText(LoginActivity.this,"아이디 또는 비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {}
                        break;
                }

            }
        });

    }
}
