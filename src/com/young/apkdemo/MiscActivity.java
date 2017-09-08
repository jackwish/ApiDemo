package com.young.apkdemo;

import java.io.*;
import com.young.apkdemo.R;
import com.young.apkdemo.apkdemo;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.content.Context;
import android.content.ContextWrapper;

public class MiscActivity extends Activity implements OnClickListener {
    private static final String TAG = "apkdemo";
    private static String fileHelperPath;
    private TextView MiscTxt;
    private Button btnCreateFileAndroid;
    private Button btnOpenFileAndroid;
    private Button btnCreateFileStandalone;
    private Button btnOpenFileStandalone;
    private Button btnGetAbi;
    private Button btnGetOsArch;
    private Button btnGetCpuinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc);
        // get textview
        MiscTxt = (TextView)findViewById(R.id.misc_text);
        // get buttons and set listeners
        btnCreateFileAndroid = (Button)findViewById(R.id.try_create_file_android);
        btnCreateFileAndroid.setOnClickListener(this);
        btnOpenFileAndroid = (Button)findViewById(R.id.try_open_file_android);
        btnOpenFileAndroid.setOnClickListener(this);
        btnCreateFileStandalone = (Button)findViewById(R.id.try_create_file_standalone);
        btnCreateFileStandalone.setOnClickListener(this);
        btnOpenFileStandalone = (Button)findViewById(R.id.try_open_file_standalone);
        btnOpenFileStandalone.setOnClickListener(this);
        btnGetAbi = (Button)findViewById(R.id.get_abi_native);
        btnGetAbi.setOnClickListener(this);
        btnGetOsArch = (Button)findViewById(R.id.get_os_arch);
        btnGetOsArch.setOnClickListener(this);
        btnGetCpuinfo = (Button)findViewById(R.id.get_cpuinfo);
        btnGetCpuinfo.setOnClickListener(this);

        // prepare create/open native file helper executable
        fileHelperPath = apkdemo.prepareExecutable(this, "file_helper");
    }

    // load the native library to work
    static {
        System.loadLibrary("misc");
    }

    @Override
    public void onClick(View view) {
        String retString = "nothing happened";
        switch (view.getId()) {
        case R.id.try_create_file_android:
            retString = tryCreateFileAndroid();
            break;
        case R.id.try_open_file_android:
            retString = tryOpenFileAndroid();
            break;
        case R.id.try_create_file_standalone:
            retString = tryCreateFileStandalone(fileHelperPath);
            break;
        case R.id.try_open_file_standalone:
            retString = tryOpenFileStandalone(fileHelperPath);
            break;
        case R.id.get_abi_native:
            retString = getAbi();
            break;
        case R.id.get_os_arch:
            retString = getOsArch();
            break;
        case R.id.get_cpuinfo:
            retString = getCpuinfo();
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        MiscTxt.setText(retString);
    }

    // try create/open file via native method
    private static native String tryCreateFileAndroid();
    private static native String tryOpenFileAndroid();
    private static native String tryCreateFileStandalone(String helperPath);
    private static native String tryOpenFileStandalone(String helperPath);

    private static native String getAbi();

    private static String getOsArch() {
        String msg = "getProperty(\"os.arch\") got: " + System.getProperty("os.arch");
        Log.i(TAG, msg);
        return msg;
    }

    private static String getCpuinfo() {
        String msg = "";
        String file = "/proc/cpuinfo";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader local = new BufferedReader(fr, 8192);
            while ((msg = local.readLine()) != null) {
                Log.i(TAG, msg + "\n");
            }
            local.close();
            String ret = "get /proc/cpuinfo in adb log";
            return ret;
        } catch (IOException e) {}
        return "can't get /proc/cpuinfo";
    }
}
