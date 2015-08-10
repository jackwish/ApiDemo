package com.young.jniinterface;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.graphics.Canvas;

public class DowncallActivity extends Activity implements OnClickListener {
    private TextView downcallTxt;
    private Button methodBtn1;
    private Button methodBtn2;
    private Button methodBtn3;
    private Button methodBtn4;
    private Button methodBtn5;
    private Button methodBtn6;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downcall);
        mContext = DowncallActivity.this;
        // get textview
        downcallTxt = (TextView)findViewById(R.id.downcall_text);
        // get buttons and set listeners
        methodBtn1 = (Button)findViewById(R.id.dc_method_button1);
        methodBtn2 = (Button)findViewById(R.id.dc_method_button2);
        methodBtn3 = (Button)findViewById(R.id.dc_method_button3);
        methodBtn4 = (Button)findViewById(R.id.dc_method_button4);
        methodBtn5 = (Button)findViewById(R.id.dc_method_button5);
        methodBtn6 = (Button)findViewById(R.id.dc_method_button6);
        methodBtn1.setOnClickListener(this);
        methodBtn2.setOnClickListener(this);
        methodBtn3.setOnClickListener(this);
        methodBtn4.setOnClickListener(this);
        methodBtn5.setOnClickListener(this);
        methodBtn6.setOnClickListener(this);
    }

    public native String downcallMtd1();
    public native boolean downcallMtd2(int i1, long i2, float i3);
    public native String downcallMtd3();
    public native String downcallMtd4();
    public native boolean downcallMtd5(int i1, int i2);
    public native void downcallMtd6(Canvas canvas);

    static {
        System.loadLibrary("downcall");
        System.loadLibrary("downcall_skia");
    }

    class TestView extends View {
        public TestView(Context context) {
            super(context);
        }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            downcallMtd6(canvas);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.dc_method_button1:
            downcallTxt.setText(downcallMtd1());
            break;
        case R.id.dc_method_button2:
            String ret = String.valueOf(downcallMtd2(-1, 0x1234567890abcdefL, 3.14F));
            downcallTxt.setText(ret);
            break;
        case R.id.dc_method_button3:
            downcallTxt.setText(downcallMtd3());
            break;
        case R.id.dc_method_button4:
            downcallTxt.setText(downcallMtd4());
            break;
        case R.id.dc_method_button5:
            long startTime, endTime;
            float time;
            int cnt = 0;
            final int COUNT = 1000000;
            startTime = System.currentTimeMillis();
            for (int i = 0; i < COUNT; i++) {
                if ((downcallMtd5(100, 200)) == true) {
                    cnt++;
                } else {
                    break;
                }
            }
            endTime = System.currentTimeMillis();
            if (cnt == COUNT) {
                time = (float)(endTime - startTime) / 1000;
                downcallTxt.setText("exectime = " + time + "s");
            } else {
                downcallTxt.setText("error");
            }
            break;
        case R.id.dc_method_button6:
            TestView testview = new TestView(mContext);
            setContentView(testview);
            break;
        default:
            break;
        }
    }
}
