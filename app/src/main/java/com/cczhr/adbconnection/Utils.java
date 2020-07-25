package com.cczhr.adbconnection;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Description：
 * create by cczhr on 2020/7/22
 * Email: cczhr1@163.com
 */
public class Utils {

    private static final String PORT = "port";
    private static final String REFRESH_TIME = "refreshTime";
    private static final String DATA = "data";
    private static final String URL = "url";
    private static final String BOOT_UP = "bootUp";
    private static final String SERVER_CHAN = "serverChan";
    private static final String DEVICE_NAME = "deviceName";

    public static void setDeviceName(Context context, String name) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putString(DEVICE_NAME, name).apply();
    }

    public static String getDeviceName(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getString(DEVICE_NAME, android.os.Build.MODEL);
    }


    public static void saveUrl(Context context, String url) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putString(URL, url).apply();
    }

    public static String getUrl(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getString(URL, "https://api.ipify.org,https://api.ip.sb/ip,https://ifconfig.me/ip");
    }


    public static void savePort(Context context, String port) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putString(PORT, port).apply();
    }

    public static String getPort(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getString(PORT, "5555");
    }

    public static void saveServerChan(Context context, String key) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putString(SERVER_CHAN, key).apply();
    }

    public static String getServerChan(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getString(SERVER_CHAN, "");
    }

    public static void saveRefreshTime(Context context, int time) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putInt(REFRESH_TIME, time).apply();
    }

    public static int getRefreshTime(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getInt(REFRESH_TIME, 30);
    }

    public static void saveBootUp(Context context, boolean isBootUp) {
        context.getSharedPreferences(DATA, MODE_PRIVATE).edit().putBoolean(BOOT_UP, isBootUp).apply();
    }

    public static boolean getBootUp(Context context) {
        return context.getSharedPreferences(DATA, MODE_PRIVATE).getBoolean(BOOT_UP, false);
    }


    public static String getIpAddressByWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        int ipAddressInt = wm.getDhcpInfo().ipAddress;
        return String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddressInt & 0xff), (ipAddressInt >> 8 & 0xff), (ipAddressInt >> 16 & 0xff), (ipAddressInt >> 24 & 0xff));
    }

    public static void runCommand(String cmd) {
        try {
            Process exec = Runtime.getRuntime().exec("su");
            OutputStream outputStream = exec.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static String runCommandForResult(String cmd) {
        Process process;
        BufferedReader successResult;
        BufferedReader errorResult;
        DataOutputStream os;
        StringBuilder result = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());
        try {
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            String line;
            try {
                while ((line = successResult.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    successResult.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                while ((line = errorResult.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    errorResult.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        process.destroy();
        return result.toString();
    }

    public static byte[] httpGet(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                byte[] result = baos.toByteArray();
                baos.close();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
