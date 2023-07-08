package com.bmax.akp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bmax.akp.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'akp' library on application startup.
    static {
        System.loadLibrary("akp");
    }

    static final String TAG = "AKP_TEST";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        EditText etSkey = binding.etSkey;
        Button btn = binding.btnTest;

//        etSkey.setText("kernel_patch");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String skey = etSkey.getText().toString();
                if(skey.isEmpty()) return;
                Log.d(TAG, "== start test, skey: " + skey);
                readTest();
                long ret = appSu(skey);
                Log.d(TAG, "appSu ret: " + ret);

                Log.d(TAG, "appSu Test in main thread");
                readTest();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "appSu Test in new thread");
                        readTest();
                    }
                }).start();
            }
        });
    }

    public void readTest () {
        int pid = android.os.Process.myPid();
        int tid = android.os.Process.myTid();
        Log.d(TAG, "pid:" + pid + ", tid:" + tid);
        try {
            File file = new File("/system/build.prop");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n"); // 如果需要保留换行符
            }
            reader.close();
            fis.close();
            Log.d(TAG, "=== read succeed, length:" + content.length());
//            Log.d(TAG, content.toString());
        } catch (Exception e) {
            Log.e(TAG, "read failed:" + e);
        }
    }


    public native String stringFromJNI();
    public native long appSu(String skey);
}