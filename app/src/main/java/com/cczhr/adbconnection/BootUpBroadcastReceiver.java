package com.cczhr.adbconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class BootUpBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)&&Utils.getBootUp(context)) {
            context.startService(new Intent(context,ADBService.class));
        }
    }
}
