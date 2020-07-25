package com.cczhr.adbconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText key;
    private EditText time;
    private EditText port;
    private CheckBox bootUp;
    private EditText server;
    private TextView tips;
    private TextView internetIp;
    private TextView intranetIp;
    private EditText deviceName;
    private TextView log;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        tips();
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                internetIp.setText(getString(R.string.internet_ip, "\nadb connect " + intent.getStringExtra("ip") + ":" + Utils.getPort(context)));
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.cczhr.adbconnection.IP"));
    }

    private void tips() {
        tips.setText("说明:\n");
        tips.append("若设备没有获取Root权限则先把设备用usb连接上电脑执行adb tcpip ");
        tips.append(Utils.getPort(this));
        tips.append("再执行adb connect命令");
    }


    private void initView() {
        log = findViewById(R.id.log);
        key = findViewById(R.id.key);
        time = findViewById(R.id.time);
        port = findViewById(R.id.port);
        bootUp = findViewById(R.id.boot_up);
        server = findViewById(R.id.server);
        tips = findViewById(R.id.tips);
        deviceName = findViewById(R.id.device_name);
        internetIp = findViewById(R.id.internet_ip);
        intranetIp = findViewById(R.id.intranet_ip);
        key.setText(Utils.getServerChan(this));
        time.setText(String.valueOf(Utils.getRefreshTime(this)));
        port.setText(Utils.getPort(this));
        bootUp.setChecked(Utils.getBootUp(this));
        server.setText(Utils.getUrl(this));
        deviceName.setText(Utils.getDeviceName(this));
    }


    public void start(View view) {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
            if (TextUtils.isEmpty(port.getText()))
                Utils.savePort(this, "5555");
            else
                Utils.savePort(this, port.getText().toString());

            if (TextUtils.isEmpty(time.getText()))
                Utils.saveRefreshTime(this, -1);
            else
                Utils.saveRefreshTime(this, Integer.parseInt(time.getText().toString()));

            Utils.saveBootUp(this, bootUp.isChecked());
            Utils.saveUrl(this, server.getText().toString());
            Utils.setDeviceName(this, deviceName.getText().toString());
            Utils.saveServerChan(this, key.getText().toString());

            Intent intent = new Intent(this, ADBService.class);
            startService(intent);
            intranetIp.setText(getString(R.string.intranet_ip, "\nadb connect " + Utils.getIpAddressByWifi(this) + ":" + Utils.getPort(this)));
        } else {
            Intent intent = new Intent(this, ADBService.class);
            stopService(intent);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void test(View view) {
        final String url = "https://sc.ftqq.com/" + key.getText().toString() + ".send?text=test";
        log.setText(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
              Utils.httpGet(url);
            }
        }).start();

    }
}