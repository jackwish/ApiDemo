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
    private Button btnOpenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc);
        // get textview
        MiscTxt = (TextView)findViewById(R.id.misc_text);
        // get buttons and set listeners
        btnOpenFile = (Button)findViewById(R.id.open_file);
        btnOpenFile.setOnClickListener(this);
    }

    /* try open file via native method */
    private static native boolean tryOpenFile();

    /* load the native library to work */
    static {
        System.loadLibrary("misc");
    }

    @Override
    public void onClick(View view) {
        String retString = "nothing happened";
        switch (view.getId()) {
        case R.id.open_file:
            if (tryOpenFile() == true) {
                retString = "amazing! all file open pass!";
            } else {
                retString = "some file open failed, check log for detail";
            }
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        MiscTxt.setText(retString);
    }
}
