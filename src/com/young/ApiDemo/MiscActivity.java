package com.young.ApiDemo;

import com.young.ApiDemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.res.Resources;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import android.content.Context;
import android.content.ContextWrapper;
import java.io.IOException;

public class MiscActivity extends Activity implements OnClickListener {
    private static final String TAG = "ApiDemo";
    private static String fileHelperPath;
    private TextView MiscTxt;
    private Button btnCreateFileAndroid;
    private Button btnOpenFileAndroid;
    private Button btnCreateFileStandalone;
    private Button btnOpenFileStandalone;

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

        // prepare create/open native file helper executable
        fileHelperPath = prepareExecutable();
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

    // copy file helper from assets and set it executable
    private String prepareExecutable() {
        final String file_helper = "file_helper";
        Log.i(TAG, "trying to copy " + file_helper + " from assets");
        try {
            InputStream ins = getAssets().open(file_helper);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            FileOutputStream fos = openFileOutput(file_helper, Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.close();
            ins.close();

            File file = getFileStreamPath(file_helper);
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true, false);
            Log.i(TAG, "copy " + file_helper + " to " + file.getAbsolutePath() + " and set EXE done!");
            return file.getAbsolutePath();
        } catch (IOException ex) {
            String fret = "fail to copy " + file_helper + " from assets";
            Log.e(TAG, fret, ex);
            return fret;
        }
    }

}
