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

package com.young.apkdemo.sdk.ipc;

import java.lang.InterruptedException;
import com.young.apkdemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;

public class DestResultActivity extends Activity {
    private static final String TAG = "apkdemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_ipc_activity_destresult);
        Log.i(TAG, "enter SDK IPC DestResultActivity Activity");
        Intent intent = new Intent();
        intent.putExtra("dest_result", "I'm from DestResultActivity");
        setResult(2, intent); // resultCode
    }
}
