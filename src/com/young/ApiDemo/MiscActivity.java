package com.young.ApiDemo;

import com.young.ApiDemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MiscActivity extends Activity implements OnClickListener {
    private static final String TAG = "ApiDemo";
    private TextView MiscTxt;
    private Button btnCreateFileAndroid;
    private Button btnOpenFileAndroid;

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
    }

    /* try create/open file via native method */
    private static native String tryCreateFileAndroid();
    private static native String tryOpenFileAndroid();

    /* load the native library to work */
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
        default:
            break;
        }
        Log.i(TAG, retString);
        MiscTxt.setText(retString);
    }
}
