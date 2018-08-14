package com.example.a17010233.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etNum;
    EditText etCon;
    Button btnSend;
    Button btnMess;
    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        etNum = findViewById(R.id.editTextTo);
        etCon = findViewById(R.id.editTextCon);
        btnSend = findViewById(R.id.button);
        btnMess = findViewById(R.id.buttonMessage);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                String num = etNum.getText().toString();
                String con = etCon.getText().toString();

                String sNum[] = num.split(",");
                for (String allNum : sNum) {
                    smsManager.sendTextMessage(allNum, null, con, null, null);
                }

                Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                etCon.setText("");
            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);


        btnMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = etNum.getText().toString();
                String con = etCon.getText().toString();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + num));
                    intent.putExtra("sms_body", con);
                    startActivity(intent);


                } else {
                    Uri smsUri = Uri.parse("sms:" + num);
                    Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                    intent.putExtra("phone_num", num);
                    intent.putExtra("sms_body", con);
                    intent.setType("vnd.android-dir/mms-sms"); //here setType will set the previous data null.
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

    }


    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }

    }
}
