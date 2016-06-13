package com.young.ApiDemo;

import com.young.ApiDemo.linker.GreylistActivity;
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
    private Button greylistBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker);
        Log.i(TAG, "enter Linker Activity");
        // get buttons and set listeners
        greylistBtn = (Button)findViewById(R.id.greylist_button);
        greylistBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.greylist_button:
            Intent greylistIntent = new Intent(LinkerActivity.this, GreylistActivity.class);
            startActivity(greylistIntent);
            break;
        default:
            break;
        }
    }
}
