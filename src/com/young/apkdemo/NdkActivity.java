/*
 * Copyright (C) 2015 Mu Weiyang <young.mu@aliyun.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranties of
 * MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.young.apkdemo;

import com.young.apkdemo.ndk.JniActivity;
import com.young.apkdemo.ndk.SyscallActivity;
import com.young.apkdemo.ndk.SignalActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NdkActivity extends Activity implements OnClickListener {
    private static final String TAG = "apkdemo";
    private Button jniBtn;
    private Button syscallBtn;
    private Button signalBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndk);
        Log.i(TAG, "enter NDK Activity");
        // get buttons and set listeners
        jniBtn = (Button)findViewById(R.id.jni_button);
        syscallBtn = (Button)findViewById(R.id.syscall_button);
        signalBtn = (Button)findViewById(R.id.signal_button);
        jniBtn.setOnClickListener(this);
        syscallBtn.setOnClickListener(this);
        signalBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.jni_button:
            Intent jniIntent = new Intent(NdkActivity.this, JniActivity.class);
            startActivity(jniIntent);
            break;
        case R.id.syscall_button:
            Intent syscallIntent = new Intent(NdkActivity.this, SyscallActivity.class);
            startActivity(syscallIntent);
            break;
        case R.id.signal_button:
            Intent signalIntent = new Intent(NdkActivity.this, SignalActivity.class);
            startActivity(signalIntent);
            break;
        default:
            break;
        }
    }
}
