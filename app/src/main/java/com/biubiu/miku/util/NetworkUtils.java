package com.biubiu.miku.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.biubiu.miku.MikuApplication;

/**
 * Network utility functions.
 */
public final class NetworkUtils {

  private NetworkUtils() {}

  public static boolean isNetworkConnected() {
    Context context = MikuApplication.context;
    ConnectivityManager connManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = null;
    try {
      activeNetworkInfo = connManager.getActiveNetworkInfo();
    } catch (Exception e) {
      // in some roms, here maybe throw a exception(like nullpoint).
      e.printStackTrace();
    }
    return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
  }

  public static NetworkInfo getActiveNetworkInfo(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = null;
    try {
      activeNetworkInfo = connManager.getActiveNetworkInfo();
    } catch (Exception e) {
      // in some roms, here maybe throw a exception(like nullpoint).
      e.printStackTrace();
    }
    return activeNetworkInfo;
  }

  public static boolean isMobileNetworkConnected(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = null;
    try {
      // maybe throw exception in android framework
      networkInfo =
          connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return networkInfo != null && networkInfo.isConnected();
  }

  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = null;
    try {
      // maybe throw exception in android framework
      networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (networkInfo != null && networkInfo.isConnected());
  }

  public static boolean isWifiClosed(Context context) {
    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    return wm.getWifiState() == WifiManager.WIFI_STATE_DISABLED;
  }

}
