package com.xm.logdemo.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.xm.logdemo.MyApplication;

/**
 * Created by XMclassmate on 2018/10/31
 */
public class DeviceUtil {

    /**
     * 获取安卓设备信息
     * @return
     */
    public static String getDeviceInfo(){
        PackageManager pm = MyApplication.getCtx().getApplicationContext().getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(MyApplication.getCtx().getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append("App VersionName:").append(packageInfo.versionName)
                    .append("\n")
                    .append("VersionCode:").append(packageInfo.versionCode)
                    .append("\n")
                    .append("OS Version:").append(Build.VERSION.RELEASE).append("_").append(Build.VERSION.SDK_INT)
                    .append("\n")
                    .append("Vendor:").append(Build.MANUFACTURER)
                    .append("\n")
                    .append("Model:").append(Build.MODEL)
                    .append("\n")
                    .append("CPU ABI:").append(Build.CPU_ABI);
            return sb.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
