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

package com.young.apkdemo.ndk;

import com.young.apkdemo.R;
import com.young.apkdemo.ndk.jni.DowncallActivity;
import com.young.apkdemo.ndk.jni.DowncallOnloadActivity;
import com.young.apkdemo.ndk.jni.UpcallActivity;
import com.young.apkdemo.ndk.jni.InvokeActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class JniActivity extends Activity implements OnClickListener {
    private static final String TAG = "apkdemo";
    private Button downcallBtn;
    private Button downcallOnloadBtn;
    private Button upcallBtn;
    private Button invokeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndk_jni);
        Log.i(TAG, "enter NDK JNI Activity");
        // get buttons and set listeners
        downcallBtn = (Button)findViewById(R.id.downcall_button);
        downcallOnloadBtn = (Button)findViewById(R.id.downcall_onload_button);
        upcallBtn = (Button)findViewById(R.id.upcall_button);
        invokeBtn = (Button)findViewById(R.id.invoke_button);
        downcallBtn.setOnClickListener(this);
        downcallOnloadBtn.setOnClickListener(this);
        upcallBtn.setOnClickListener(this);
        invokeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.downcall_button:
            Intent downcallIntent = new Intent(JniActivity.this, DowncallActivity.class);
            startActivity(downcallIntent);
            break;
        case R.id.downcall_onload_button:
            Intent downcallOnloadIntent = new Intent(JniActivity.this, DowncallOnloadActivity.class);
            startActivity(downcallOnloadIntent);
            break;
        case R.id.upcall_button:
            Intent upcallIntent = new Intent(JniActivity.this, UpcallActivity.class);
            startActivity(upcallIntent);
            break;
        case R.id.invoke_button:
            Intent invokeIntent = new Intent(JniActivity.this, InvokeActivity.class);
            startActivity(invokeIntent);
            break;
        default:
            break;
        }
    }
}
