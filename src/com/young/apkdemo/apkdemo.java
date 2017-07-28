/*
 * Copyright (C) 2016 王振华 (WANG Zhenhua) <i@jackwish.net>
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

import com.young.apkdemo.R;
import com.young.apkdemo.apkdemo;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.res.Resources;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import android.content.Context;
import android.content.ContextWrapper;
import java.io.IOException;


public class apkdemo {
    private static final String TAG = "apkdemo";

    public static String prepareExecutable(Context context, String name) {
        final String arch = System.getProperty("os.arch");
        final String fname = name + (arch.startsWith("arm") ? ".arm" : ".x86" );
        Log.i(TAG, "trying to copy " + fname + " from assets");
        try {
            InputStream ins = context.getAssets().open(fname);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            FileOutputStream fos = context.openFileOutput(fname, Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.close();
            ins.close();

            File f = context.getFileStreamPath(fname);
            f.setExecutable(true, false);
            f.setReadable(true, false);
            f.setWritable(true, false);
            Log.i(TAG, "copied " + f.getAbsolutePath() + " and set EXE done!");
            return f.getAbsolutePath();
        } catch (IOException ex) {
            String fret = "fail to copy " + fname + " from assets";
            Log.e(TAG, fret, ex);
            return "";
        }
    }
}
