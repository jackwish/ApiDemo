package com.young.ApiDemo.linker;

import com.young.ApiDemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NamespaceActivity extends Activity implements OnClickListener {
    private static final String TAG = "ApiDemo";
    private TextView NamespaceTxt;
    private Button btnPrivate;
    private Button btnGreylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker_namespace);
        // get textview
        NamespaceTxt = (TextView)findViewById(R.id.namespace_text);
        // get buttons and set listeners
        btnPrivate = (Button)findViewById(R.id.ns_load_private_lib);
        btnPrivate.setOnClickListener(this);
        btnGreylist = (Button)findViewById(R.id.ns_load_greylist_lib);
        btnGreylist.setOnClickListener(this);
    }

    /* a generic native method to load library */
    private static native boolean nsLoadLib(String lib);

    /* load the native library to work */
    static {
        System.loadLibrary("namespace");
    }

    @Override
    public void onClick(View view) {
        String retString = "nothing";
        switch (view.getId()) {
        case R.id.ns_load_private_lib:
            String libhw = "libhardware.so";
            Log.i(TAG, "going to load private lib \"" + libhw + "\" to verify - if namespace based dynamic link works");
            if (nsLoadLib(libhw) == true) {
                retString = "private lib \"" + libhw + "\" load pass - namespace works";
            } else {
                retString = "private lib \"" + libhw + "\" load fail - namespace doesn't work";
            }
            break;
        case R.id.ns_load_greylist_lib:
            String libtest = "libandroid_runtime.so";
            Log.i(TAG, "going to load greylist lib \"" + libtest + "\" to verify - if namespace based dynamic link works");
            if (nsLoadLib(libtest) == true) {
                retString = "greylist lib \"" + libtest + "\" load pass - namespace works";
            } else {
                retString = "greylist lib \"" + libtest + "\" load fail - namespace doesn't work";
            }
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        NamespaceTxt.setText(retString);
    }
}
