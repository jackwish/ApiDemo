package com.young.apkdemo;

import com.young.apkdemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dalvik.system.PathClassLoader;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.lang.reflect.Method;

public class LinkerActivity extends Activity implements OnClickListener {
    private static final String TAG = "apkdemo";
    private TextView LinkerTxt;
    private Button btnPublic;
    private Button btnPrivate;
    private Button btnGreylist;
    private Button btnArmpath;
    private Button btnMultiClassLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linker);
        // get textview
        LinkerTxt = (TextView)findViewById(R.id.linker_text);
        // get buttons and set listeners
        btnPublic = (Button)findViewById(R.id.ns_load_public_lib);
        btnPublic.setOnClickListener(this);
        btnPrivate = (Button)findViewById(R.id.ns_load_private_lib);
        btnPrivate.setOnClickListener(this);
        btnGreylist = (Button)findViewById(R.id.ns_load_greylist_lib);
        btnGreylist.setOnClickListener(this);
        btnArmpath = (Button)findViewById(R.id.ns_check_armpath);
        btnArmpath.setOnClickListener(this);
        btnMultiClassLoader = (Button)findViewById(R.id.ns_multi_classloader);
        btnMultiClassLoader.setOnClickListener(this);
    }

    /* load the main functionality native library to work */
    static {
        System.loadLibrary("namespace");
    }

    /**
     * Dispatcher for subcases of dynamic link.
     */
    @Override
    public void onClick(View view) {
        String retString = "nothing";
        switch (view.getId()) {
        case R.id.ns_load_public_lib:
            retString = verifyLibraryLoading("libandroid.so", "public", true);
            break;
        case R.id.ns_load_private_lib:
            retString = verifyLibraryLoading("libhardware.so", "private", false);
            break;
        case R.id.ns_load_greylist_lib:
            retString = verifyLibraryLoading("libandroid_runtime.so", "greylist", true);
            break;
        case R.id.ns_check_armpath:
            retString = ns_scan_arm_path();
            break;
        case R.id.ns_multi_classloader:
            retString = MultiClassloaderWithSameLibraryPath(this);
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        LinkerTxt.setText(retString);
    }

    /**
     * a generic native method to load library.
     */
    private static native boolean ns_load_lib(String lib);

    /**
     * A generic method to load library, wraps the native function *ns_load_lib()*.
     */
    private String verifyLibraryLoading(String lib, String type, boolean expect) {
        Log.i(TAG, "going to load " + type + " library \"" + lib + "\" to verify - if namespace based dynamic link works");
        boolean loaded = ns_load_lib(lib);
        return (loaded == expect) ? (type + " library \"" + lib + "\" load pass - namespace works") :
            (type + " library \"" + lib + "\" load pass - namespace doesn't work");
    }

    /**
     * Check if there is directory named *arm* under /system/lib or /vendor/lib.
     * Look into libnamespace.so library for detail.
     */
    private static native String ns_scan_arm_path();


    /**
     * Test of "same arguments for different namespaces".
     * The test here is to test namespace of dynamic linker and nativebridge for cross-ABI platform.
     * The DexClassLoader test in SDK/Misc is for pure Java test. This testcase is inherit from CTS
     * (https://android-review.googlesource.com/#/c/245100/). But ignored the "multiple instance of
     * same library in different namespaces", that is for another test..
     */
    private String MultiClassloaderWithSameLibraryPath(Context c) {
        try {
            String pkgName = c.getPackageName();
            ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
                pkgName, PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
            String apkPath = ai.sourceDir;
            String libPath = ai.nativeLibraryDir;
            try {
                loadClassAndInvoke(pkgName + ".ClassNamespaceA", "hello", apkPath, libPath);
                loadClassAndInvoke(pkgName + ".ClassNamespaceB", "hello", apkPath, libPath);
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception encountered, check log!";
            }

            return "apkPath: " + apkPath + "\nlibPath: " + libPath + "\n" +
                "check log for detail - maybe you need to build a modified image for this...";
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "Exception encountered, check log!";
        }
    }

    /**
     * Load a class by PathClassLoader and invoke a specific (static) method.
     */
    private void loadClassAndInvoke(String className, String method, String apkPath, String libPath) throws Exception {
        PathClassLoader loader = new PathClassLoader(
                apkPath, libPath, ClassLoader.getSystemClassLoader());
        Class<?> clazz = loader.loadClass(className);
        clazz.getMethod(method).invoke(null);
    }

}

class ClassNamespaceA {
    private static final String TAG = "apkdemo";
    public static void hello() {
        Log.i(TAG, "hello from *ClassNamespaceA*");
        System.loadLibrary("downcall");
    }
}

class ClassNamespaceB {
    private static final String TAG = "apkdemo";
    public static void hello() {
        Log.i(TAG, "hello from *ClassNamespaceB*");
        System.loadLibrary("callee");
    }
}
