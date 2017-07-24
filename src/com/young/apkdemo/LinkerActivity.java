package com.young.apkdemo;

import com.young.apkdemo.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.EditText;
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
    private Button btnFaked;
    private Button btnArmpath;
    private Button btnMultiClassLoader;
    private Button btnMultiLibIns;
    private Button btnLoadZip;
    private Button btnUnwindFindExidx;
    private Button btnIndirectDependent;
    private EditText EditableLibName;
	private Button btnLoadCertainLib;

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
        btnFaked = (Button)findViewById(R.id.ns_load_faked_lib);
        btnFaked.setOnClickListener(this);
        btnArmpath = (Button)findViewById(R.id.ns_check_armpath);
        btnArmpath.setOnClickListener(this);
        btnMultiClassLoader = (Button)findViewById(R.id.ns_multi_classloader);
        btnMultiClassLoader.setOnClickListener(this);
        btnMultiLibIns = (Button)findViewById(R.id.ns_multi_lib_instance);
        btnMultiLibIns.setOnClickListener(this);
        btnLoadZip = (Button)findViewById(R.id.load_from_zip);
        btnLoadZip.setOnClickListener(this);
        btnUnwindFindExidx = (Button)findViewById(R.id.dl_unwind_find_exidx);
        btnUnwindFindExidx.setOnClickListener(this);
        btnIndirectDependent = (Button)findViewById(R.id.indirect_dependent);
        btnIndirectDependent.setOnClickListener(this);
        EditableLibName = (EditText)findViewById(R.id.editable_lib_name);
        EditableLibName.clearFocus();
        EditableLibName.setSelectAllOnFocus(true);
        btnLoadCertainLib = (Button)findViewById(R.id.ns_load_certain_lib);
        btnLoadCertainLib.setOnClickListener(this);
    }

    /* load the main functionality native library to work */
    static {
        System.loadLibrary("namespace");
        System.loadLibrary("dltest");
        System.loadLibrary("dlext_test");
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
        case R.id.ns_load_faked_lib:
            retString = verifyLibraryLoading("libart.so", "faked", true);
            break;
        case R.id.ns_check_armpath:
            retString = nsScanArmPath();
            break;
        case R.id.ns_multi_classloader:
            retString = MultiClassloaderWithSameLibraryPath(this);
            break;
        case R.id.ns_multi_lib_instance:
            retString = MultiInstanceOfSameLibrary(this);
            break;
        case R.id.load_from_zip:
            retString = loadLibraryFromZip();
            break;
        case R.id.dl_unwind_find_exidx:
            retString = dlUnwindFindExidx();
            break;
        case R.id.indirect_dependent:
            retString = indirectDependent();
            break;
        case R.id.ns_load_certain_lib:
            String lib_name = EditableLibName.getText().toString();
			retString = certainLibraryLoading(lib_name);
            break;
        default:
            break;
        }
        Log.i(TAG, retString);
        LinkerTxt.setText(retString);
    }

    private String certainLibraryLoading(String lib) {
        Log.i(TAG, "going to load " + "certain library \"" + lib + "\" to verify - if namespace based dynamic link works");
        boolean loaded = nsLoadLib(lib);
        return (loaded == true) ? ("certain library \"" + lib + "\" loads success, users themselves verify the result") :
            ("certain library \"" + lib + "\" loads fail, users themselves verify the result");
    }

    /**
     * A generic method to load library, wraps the native function *nsLoadLib()*.
     */
    private String verifyLibraryLoading(String lib, String type, boolean expect) {
        Log.i(TAG, "going to load " + type + " library \"" + lib + "\" to verify - if namespace based dynamic link works");
        boolean loaded = nsLoadLib(lib);
        return (loaded == expect) ? (type + " library \"" + lib + "\" loading result matches - namespace works") :
            (type + " library \"" + lib + "\" loading result NOT match - namespace doesn't work");
    }

    private static native boolean nsLoadLib(String lib);


    /**
     * Check if there is directory named *arm* under /system/lib or /vendor/lib.
     * Look into libnamespace.so library for detail.
     */
    private static native String nsScanArmPath();


    /**
     * Test of "same arguments for different namespaces".
     *
     * The test here is to test namespace of dynamic linker and nativebridge for cross-ABI platform.
     * The DexClassLoader test in SDK/Misc is for pure Java test. This testcase is inherit from CTS
     * (https://android-review.googlesource.com/#/c/245100/). But ignored the "multiple instance of
     * same library in different namespaces", that is for another test..
     */
    private static Class<?> classA = null;
    private static Class<?> classB = null;
    private String MultiClassloaderWithSameLibraryPath(Context c) {
        try {
            if (classA == null) {
                classA = loadClass(c, "ClassA");
            }
            if (classB == null) {
                classB = loadClass(c, "ClassB");
            }
            invokeHello(classA);
            invokeHello(classB);

            return "seems ok, check log for detail - maybe you need to build a modified image for this...";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception encountered, maybe try to relaunch the app?";
        }
    }

    private Class<?> loadClass(Context c, String className) throws Exception {
        String pkgName = c.getPackageName();
        ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
            pkgName, PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
        String apkPath = ai.sourceDir;
        String libPath = ai.nativeLibraryDir;
        PathClassLoader loader = new PathClassLoader(
            apkPath, libPath, ClassLoader.getSystemClassLoader());
        return loader.loadClass(pkgName + "." + className);
    }

    private void invokeHello(Class<?> clazz) throws Exception {
        clazz.getMethod("hello").invoke(null);
    }

    private int invokeGetValue(Class<?> clazz) throws Exception {
        return (Integer)clazz.getMethod("getValue").invoke(null);
    }

    private void invokeIncValue(Class<?> clazz) throws Exception {
        clazz.getMethod("incValue").invoke(null);
    }

    /**
     * Test whether a native user library used by different ClassLoader has multiple instance.
     *
     * Android Nougat introduced namespace based dynamic link which could be taken as extension
     * of Java ClassLoader at native world. As a result, a user library has instance for every
     * ClassLoader/namespace if it's used. The *instance* here means a copy in memory of one
     * same process. This case is to verify such scenario.
     *
     * We have two Java classes - ClassA and ClassB which has same logic and same method name.
     * But for native method, they call into native library libclassloader_a.so and
     * libclassloader_b.so respectively. Both these two native libraries reference
     * libclassloader_base.so where the true get_value() and inc_value() is implemented.
     * This case simply creates two ClassLoader and calls getValue() and incValue() of them
     * respectively, so:
     *  - Without namespace, the value is increased by 2 since inc_value() is in same library instance.
     *  - With namespace, the value is increased by 1 in two library instances.
     */
    private String MultiInstanceOfSameLibrary(Context c) {
        try {
            if (classA == null) {
                classA = loadClass(c, "ClassA");
            }
            if (classB == null) {
                classB = loadClass(c, "ClassB");
            }

            int vA = invokeGetValue(classA);
            int vB = invokeGetValue(classB);
            if (vA != vB) {
                // String ret = "ERROR: the value of class A and B are different: " + vA + ", " + vB;
                // Log.i(TAG, ret);
                return "ERROR: the value of class A and B are different: " + vA + ", " + vB;
            }

            int origin = vA;
            invokeIncValue(classA);
            invokeIncValue(classB);
            vA = invokeGetValue(classA);
            vB = invokeGetValue(classB);
            if (vA != vB || origin + 1 != vA) {
                String ret = "Unexpected result: origin(" + origin + "), A(" + vA + "), B("+ vB + ")";
                Log.i(TAG, ret);
                return ret;
            }

            return "everything seems ok - the library has multiple instance";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception encountered, maybe try to relaunch the app?";
        }
    }

    /**
     * Test whether can load from zip archive correctly.
     */
    private static native String loadLibraryFromZip();

    /**
     * Test whether dl_unwind_find_exidx() works correctly.
     */
    private static native String dlUnwindFindExidx();

    /**
     * Try to dlsym a non-NDK symbol indirectly.
     */
    private static native String indirectDependent();
}

class ClassA {
    private static final String TAG = "apkdemo";

    static {
        System.loadLibrary("classloader_a");
    }

    public static void hello() {
        Log.i(TAG, "hello from *ClassA*");
        System.loadLibrary("downcall");
    }

    public static native void incValue();
    public static native int getValue();
}

class ClassB {
    private static final String TAG = "apkdemo";

    static {
        System.loadLibrary("classloader_b");
    }

    public static void hello() {
        Log.i(TAG, "hello from *ClassB*");
        System.loadLibrary("callee");
    }

    public static native void incValue();
    public static native int getValue();
}
