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

public class SyscallActivity extends Activity implements OnClickListener {
    private TextView SyscallTxt;
    private Button methodBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndk_syscall);
        // get textview
        SyscallTxt = (TextView)findViewById(R.id.syscall_text);
        // get buttons and set listeners
        methodBtn1 = (Button)findViewById(R.id.sys_method_button1);
        methodBtn1.setOnClickListener(this);
    }

    public native String SyscallTest1();

    static {
        System.loadLibrary("syscall");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.sys_method_button1:
            SyscallTxt.setText(SyscallTest1());
            break;
        default:
            break;
        }
    }
}
