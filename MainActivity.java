package com.example.jbbuu.goodintentions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.AlarmClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_SET_ALARM = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendEmail(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Ensures only email apps will appear.
        //Check if support for email is available on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendSms(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto: 678900"));  // Ensures only sms apps will appear. Start msg to 678900
        intent.putExtra("sms_body", "My message");
        //Check if support for email is available on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void setAlarm(View view) {
        //Check if permission is granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, "My Alarm")
                    .putExtra(AlarmClock.EXTRA_HOUR, 2)
                    .putExtra(AlarmClock.EXTRA_MINUTES, 45);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this, "Permission for alarm not granted", Toast.LENGTH_SHORT).show();
        }
    }
}
