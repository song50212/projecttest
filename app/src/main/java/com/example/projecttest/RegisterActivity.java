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

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_pass, et_phone;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        class CustomTask extends AsyncTask<String, Void, String> {
            String sendMsg, receiveMsg;
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String str;
                    URL url = new URL("http://34.64.201.195:8080/GraduationPjt/join.jsp");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                    sendMsg = "id="+strings[0]+"&pwd="+strings[1]+"&number="+strings[2]+"&type="+strings[3];
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

        //아이디 값 찾아주기
        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_phone = findViewById(R.id.et_phone);

        //회원가입 버튼 클릭 시 수행
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_register :
                        //EditText에 현재 입력되어있는 값을 가져온다.
                        String id = et_id.getText().toString();
                        String pwd = et_pass.getText().toString();
                        String number = et_phone.getText().toString();
                        try {
                            String result;
                            result  = new CustomTask().execute(id,pwd,number,"join.jsp").get();
                            if(result.equals(">success")) {
                                Toast.makeText(RegisterActivity.this,"회원가입을 축하합니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else if(result.equals(">fail")) {
                                Toast.makeText(RegisterActivity.this,"이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {}
                        break;
                }
            }
        });
    }
}
