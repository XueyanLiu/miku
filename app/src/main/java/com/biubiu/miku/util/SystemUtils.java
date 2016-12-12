package com.biubiu.miku.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.biubiu.miku.MikuApplication;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

/**
 * System and device related utils.
 */
public class SystemUtils {
  public static final String SHARE_PACKAGENAME_WECHAT = "com.tencent.mm";
  private static String versionName = null;
  private static int versionCode = 0;
  private static String deviceId = null;
  private static String deviceMAC = null;

  private SystemUtils() {
  }

  public static boolean aboveApiLevel(int sdkInt) {
    return getApiLevel() >= sdkInt;
  }

  public static int getApiLevel() {
    return Build.VERSION.SDK_INT;
  }

  public static boolean isSDCardMounted() {
    return Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);
  }

  public static String getWifiIPAddress(Context context) {
    try {
      WifiManager mgr = (WifiManager) context
          .getSystemService(Context.WIFI_SERVICE);
      if (mgr == null) {
        return null;
      }

      WifiInfo info = mgr.getConnectionInfo();
      if (info == null) {
        return null;
      }

      int ipAddress = info.getIpAddress();
      if (ipAddress == 0) {
        return null;
      }

      String ip = String.format(Locale.US, "%d.%d.%d.%d", (ipAddress & 0xff),
          (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
          (ipAddress >> 24 & 0xff));

      return ip;
    } catch (Exception e) {
      return null;
    }
  }

  public static int getVersionCode(Context context) {
    if (versionCode != 0) {
      return versionCode;
    }
    PackageInfo packageInfo;
    try {
      packageInfo = context.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      versionCode = packageInfo.versionCode;
      return versionCode;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String getImei(Context context) {
    try {
      TelephonyManager telephonyManager = (TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE);
      return telephonyManager.getDeviceId();
    } catch (Exception e) {
      // In some devices, we are not able to get device id, and may cause some exception,
      // so catch it.
      return "";
    }
  }

  public static String getVersionName(Context context) {
    if (versionName == null) {
      PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), 0);
      if (packageInfo != null) {
        versionName = packageInfo.versionName;
      } else {
        versionName = "";
      }

    }
    return versionName;
  }

  public static String getFullVersion(Context context) {
    return getVersionName(context) + "." + getVersionCode(context);
  }

  public static PackageInfo getPackageInfo(Context context, String packageName, int flag) {
    PackageManager packageManager = context.getPackageManager();
    PackageInfo packageInfo = null;
    try {
      packageInfo = packageManager.getPackageInfo(packageName, flag);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      // In some ROM, there will be a PackageManager has died exception. So we catch it here.
      e.printStackTrace();
    }
    return packageInfo;
  }

  public static int getNavigationBarHeightPx() {
    Resources resources = MikuApplication.context.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
  }

  public static int getStatusBarHeightPx() {
    try {
      Class c = Class.forName("com.android.internal.R$dimen");
      Object obj = c.newInstance();
      Field field = c.getField("status_bar_height");
      int x = Integer.parseInt(field.get(obj).toString());
      return MikuApplication.context.getResources().getDimensionPixelSize(x);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static int getScreenHeightPx() {
    WindowManager windowManager = (WindowManager) MikuApplication.context
        .getSystemService(Context.WINDOW_SERVICE);
    Display defaultDisplay = windowManager.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    defaultDisplay.getMetrics(metrics);
    return metrics.heightPixels;
  }

  public static int getScreenWidthPx() {
    WindowManager windowManager = (WindowManager) MikuApplication.context
        .getSystemService(Context.WINDOW_SERVICE);
    Display defaultDisplay = windowManager.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    defaultDisplay.getMetrics(metrics);
    return metrics.widthPixels;
  }

  public static String getBrand() {
    if (TextUtils.isEmpty(Build.BRAND)) {
      return "";
    } else {
      return Build.BRAND;
    }
  }

  public static String getDeviceVersion() {
    return Build.VERSION.RELEASE;
  }

  public static void showInputMethod(View view) {
    try {
      InputMethodManager imm = (InputMethodManager) view.getContext()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void hideInputMethod(View view) {
    try {
      ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
          .hideSoftInputFromWindow(view.getWindowToken(), 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean isPackageInstalled(Context context, String packageName) {
    PackageManager packageManager = context.getPackageManager();
    List<PackageInfo> packages = packageManager.getInstalledPackages(0);
    for (PackageInfo packageInfo : packages) {
      if (packageInfo.packageName.equals(packageName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断当前屏幕是否锁屏.
   *
   * @param context
   * @return boolean
   */
  public static boolean inKeyguardRestrictedInputMode(Context context) {
    KeyguardManager mKeyguardManager = (KeyguardManager)
        context.getSystemService(Context.KEYGUARD_SERVICE);
    return mKeyguardManager.inKeyguardRestrictedInputMode();
  }

  /**
   * 屏幕是否是亮着的.
   *
   * @param context
   * @return boolean
   */
  public static boolean isScreenOn(Context context) {
    PowerManager pm = (PowerManager)
        context.getSystemService(Context.POWER_SERVICE);
    return pm.isScreenOn();
  }

  public static int dpToPx(float dp) {
    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        MikuApplication.context.getResources().getDisplayMetrics());
    return (int) px;
  }

  public static int getRealScreenHeight() {
    if (Build.BRAND.toLowerCase().contains("meizu")) {
      return SystemUtils.getScreenHeightPx() - SystemUtils.getNavigationBarHeightPx();
    }
    return SystemUtils.getScreenHeightPx();
  }

  public static synchronized String getDeviceId() {
    if (TextUtils.isEmpty(deviceId)) {
      Context context = MikuApplication.context;
      TelephonyManager telephonyManager
          = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      deviceId = telephonyManager.getDeviceId();
    }
    return deviceId;
  }

  public static synchronized String getMACAddress() {
    if (TextUtils.isEmpty(deviceMAC)) {
      Context context = MikuApplication.context;
      WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (wifiManager != null) {
        WifiInfo info = null;
        try {
          // here maybe throw exception in android framework
          info = wifiManager.getConnectionInfo();
        } catch (Exception e) {
          e.printStackTrace();
        }
        if (info != null) {
          deviceMAC = info.getMacAddress();
        }
      }
    }
    return deviceMAC;
  }

  /**
   * @param value
   * @return 将dip或者dp转为float
   */
  public static float dipOrDpToFloat(String value) {
    if (value.indexOf("dp") != -1) {
      value = value.replace("dp", "");
    }
    else {
      value = value.replace("dip", "");
    }
    return Float.parseFloat(value);
  }
}
