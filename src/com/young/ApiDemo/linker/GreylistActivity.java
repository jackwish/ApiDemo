package com.young.ApiDemo.linker;

import com.young.ApiDemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GreylistActivity extends Activity implements OnClickListener {
    private static final String TAG = "ApiDemo";
    private TextView GreylistTxt;
    private Button methodBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker_greylist);
        // get textview
        GreylistTxt = (TextView)findViewById(R.id.greylist_text);
        // get buttons and set listeners
        methodBtn1 = (Button)findViewById(R.id.greylist_method_button1);
        methodBtn1.setOnClickListener(this);
    }

    private static native boolean singleton(String lib);

    static {
        System.loadLibrary("greylist");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.greylist_method_button1:
            String tlib = "libandroid_runtime.so";
            if (singleton(tlib) == true) {
                Log.i(TAG, tlib + " load pass");
            } else {
                Log.i(TAG, tlib + " load fail");
            }
            break;
        default:
            break;
        }
    }
}
