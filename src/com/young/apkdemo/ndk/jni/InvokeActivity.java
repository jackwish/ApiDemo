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

package com.young.apkdemo.ndk.jni;

import com.young.apkdemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InvokeActivity extends Activity {
    private static final String TAG = "apkdemo";
    private Button methodBtn;
    private TextView invokeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndk_jni_invoke);
        Log.i(TAG, "enter NDK JNI Invoke Activity");
        invokeTxt = (TextView)findViewById(R.id.invoke_text);
        // get button and set listener
        methodBtn = (Button)findViewById(R.id.iv_method_button);
        methodBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mainThread();
                invokeTxt.setText("OK");
            }
        });
        globalizeVar();
    }

    public static void invoke(int i) {
        Log.i(TAG, "trigger upcall! (invoke, i = " + i + ")");
    }

    public native void mainThread();
    public native void globalizeVar();

    static {
        System.loadLibrary("invoke");
    }
}
