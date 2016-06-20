package com.young.ApiDemo;

import com.young.ApiDemo.linker.NamespaceActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LinkerActivity extends Activity implements OnClickListener {
    private static final String TAG = "linker test";
    private Button jniBtn;
    private Button namespaceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker);
        Log.i(TAG, "enter Linker Activity");
        // get buttons and set listeners
        namespaceBtn = (Button)findViewById(R.id.namespace_button);
        namespaceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.namespace_button:
            Intent namespaceIntent = new Intent(LinkerActivity.this, NamespaceActivity.class);
            startActivity(namespaceIntent);
            break;
        default:
            break;
        }
    }
}
