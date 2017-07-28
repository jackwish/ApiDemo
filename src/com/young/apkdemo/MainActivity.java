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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    private static final String TAG = "apkdemo";
    private Button sdkBtn;
    private Button ndkBtn;
    private Button linkerBtn;
    private Button miscBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG, "enter MainActivity");
        // get buttons and set listeners
        sdkBtn = (Button)findViewById(R.id.sdk_button);
        sdkBtn.setOnClickListener(this);
        ndkBtn = (Button)findViewById(R.id.ndk_button);
        ndkBtn.setOnClickListener(this);
        linkerBtn = (Button)findViewById(R.id.linker_button);
        linkerBtn.setOnClickListener(this);
        miscBtn = (Button)findViewById(R.id.misc_button);
        miscBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent tIntent;
        switch (view.getId()) {
        case R.id.sdk_button:
            tIntent = new Intent(MainActivity.this, SdkActivity.class);
            startActivity(tIntent);
            break;
        case R.id.ndk_button:
            tIntent = new Intent(MainActivity.this, NdkActivity.class);
            startActivity(tIntent);
            break;
        case R.id.linker_button:
            tIntent = new Intent(MainActivity.this, LinkerActivity.class);
            startActivity(tIntent);
            break;
        case R.id.misc_button:
            tIntent = new Intent(MainActivity.this, MiscActivity.class);
            startActivity(tIntent);
            break;
        default:
            break;
        }
    }
}
