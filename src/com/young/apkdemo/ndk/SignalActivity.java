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
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SignalActivity extends Activity implements OnClickListener {
    private TextView SignalTxt;
    private Button methodBtn1;
    private Button methodBtn2;
    private Button methodBtn3;
    private Button methodBtn4;
    private Button methodBtn5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndk_signal);
        // get textview
        SignalTxt = (TextView)findViewById(R.id.signal_text);
        // get buttons and set listeners
        methodBtn1 = (Button)findViewById(R.id.sig_method_button1);
        methodBtn2 = (Button)findViewById(R.id.sig_method_button2);
        methodBtn3 = (Button)findViewById(R.id.sig_method_button3);
        methodBtn4 = (Button)findViewById(R.id.sig_method_button4);
        methodBtn5 = (Button)findViewById(R.id.sig_method_button5);
        methodBtn1.setOnClickListener(this);
        methodBtn2.setOnClickListener(this);
        methodBtn3.setOnClickListener(this);
        methodBtn4.setOnClickListener(this);
        methodBtn5.setOnClickListener(this);
    }

    public native String SignalTest1();
    public native String SignalTest2();
    public native String SignalTest3();
    public native String SignalTest4();
    public native String SignalTest5();

    static {
        System.loadLibrary("signal");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.sig_method_button1:
            SignalTxt.setText(SignalTest1());
            break;
        case R.id.sig_method_button2:
            SignalTxt.setText(SignalTest2());
            break;
        case R.id.sig_method_button3:
            SignalTxt.setText(SignalTest3());
            break;
        case R.id.sig_method_button4:
            SignalTxt.setText(SignalTest4());
            break;
        case R.id.sig_method_button5:
            SignalTxt.setText(SignalTest5());
            break;
        default:
            break;
        }
    }
}
