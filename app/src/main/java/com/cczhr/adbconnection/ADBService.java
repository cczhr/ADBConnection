package com.cczhr.adbconnection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

/**
 * @author cczhr
 * @description
 * @since 2020/7/22 15:10
 */
public class ADBService extends Service {
    private ScheduledExecutorService scheduledThreadPool;
    private ScheduledFuture taskFuture;
    private String[] urls;
    private String tIp = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        startADBDService();
        if (Utils.getRefreshTime(this) == -1 || Utils.getUrl(this).split(",").length == 0)
            return START_STICKY;
        createPolling();
        return START_STICKY;
    }

    private void startADBDService() {
        Utils.runCommand("setprop service.adb.tcp.port " + Utils.getPort(this));
        Utils.runCommand("stop adbd");
        Utils.runCommand("start adbd");
    }

    private void stopADBDService() {
        Utils.runCommand("setprop service.adb.tcp.port -1");
        Utils.runCommand("stop adbd");
        Utils.runCommand("start adbd");
    }

    private void createPolling() {
        urls = Utils.getUrl(this).split(",");
        if (scheduledThreadPool == null || scheduledThreadPool.isShutdown()) {
            scheduledThreadPool = Executors.newScheduledThreadPool(1);
            taskFuture = scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    for (String url : urls) {
                        byte[] result = Utils.httpGet(url);
                        if (result != null) {
                            String ip = new String(result, StandardCharsets.UTF_8);
                            Intent intent = new Intent("com.cczhr.adbconnection.IP");
                            intent.putExtra("ip", ip);
                            sendBroadcast(intent);
                            if (tIp == null || (!tIp.equals(ip))) {
                                tIp = ip;
                                String text = null;
                                try {
                                    String intranetIp = getString(R.string.intranet_ip, "adb connect " + Utils.getIpAddressByWifi(ADBService.this) + ":" + Utils.getPort(ADBService.this));
                                    String internetIp = getString(R.string.internet_ip, "adb connect " + tIp + ":" + Utils.getPort(ADBService.this));
                                    text = URLEncoder.encode(Utils.getDeviceName(ADBService.this)+internetIp+intranetIp, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Utils.httpGet("https://sc.ftqq.com/" + Utils.getServerChan(ADBService.this) + ".send?text=" + text);
                                break;
                            }
                            break;


                        }
                    }
                }
            }, 0, Utils.getRefreshTime(this), TimeUnit.SECONDS);

        }

    }

    private void createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //创建通知只是利用startForeground 保活android8.0以上的后台服务
            NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("ADB Connection", "ADB Connection", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification = new Notification.Builder(this, "ADB Connection")
                    .setContentTitle("ADB Connection")
                    .setContentText("ADB Connection 服务正在运行")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(100, notification);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopADBDService();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        if (scheduledThreadPool != null) {
            taskFuture.cancel(true);
            scheduledThreadPool.shutdownNow();
        }
    }
}
