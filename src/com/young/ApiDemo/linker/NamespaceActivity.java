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
    private Button btnPublic;
    private Button btnPrivate;
    private Button btnGreylist;
    private Button btnArmpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker_namespace);
        // get textview
        NamespaceTxt = (TextView)findViewById(R.id.namespace_text);
        // get buttons and set listeners
        btnPublic = (Button)findViewById(R.id.ns_load_public_lib);
        btnPublic.setOnClickListener(this);
        btnPrivate = (Button)findViewById(R.id.ns_load_private_lib);
        btnPrivate.setOnClickListener(this);
        btnGreylist = (Button)findViewById(R.id.ns_load_greylist_lib);
        btnGreylist.setOnClickListener(this);
        btnArmpath = (Button)findViewById(R.id.ns_check_armpath);
        btnArmpath.setOnClickListener(this);
    }

    /* a generic native method to load library */
    private static native boolean nsLoadLib(String lib);

    /* check if *arm* is hiddened under /system/lib and /vendor/lib */
    private static native String nsScanArmPath();

    /* load the native library to work */
    static {
        System.loadLibrary("namespace");
    }

    private String verify_library_loading(String lib, String type, boolean expect)
    {
        Log.i(TAG, "going to load " + type + " library \"" + lib + "\" to verify - if namespace based dynamic link works");
        boolean loaded = nsLoadLib(lib);
        return (loaded == expect) ? (type + " library \"" + lib + "\" load pass - namespace works") :
            (type + " library \"" + lib + "\" load pass - namespace doesn't work");
    }

    @Override
    public void onClick(View view) {
        String retString = "nothing";
        switch (view.getId()) {
        case R.id.ns_load_public_lib:
            retString = verify_library_loading("libandroid.so", "public", true);
            break;
        case R.id.ns_load_private_lib:
            retString = verify_library_loading("libhardware.so", "private", false);
            break;
        case R.id.ns_load_greylist_lib:
            retString = verify_library_loading("libandroid_runtime.so", "greylist", true);
            break;
        case R.id.ns_check_armpath:
            retString = nsScanArmPath();
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        NamespaceTxt.setText(retString);
    }
}
