package com.vishalinventory.physicalinventory;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class PermissionsUtil {
    public static final int PERMISSION_ALL = 1;

    public static boolean doesAppNeedPermissions(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
    }

    public static String[] getPermissions(Context context)
            throws PackageManager.NameNotFoundException {
        PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(), PackageManager.GET_PERMISSIONS);

        return info.requestedPermissions;
    }

    public static void askPermissions(Activity activity){
        if(doesAppNeedPermissions()) {
            try {
                String[] permissions = getPermissions(activity);

                if(!checkPermissions(activity, permissions)){
                    ActivityCompat.requestPermissions(activity, permissions,
                            PERMISSION_ALL);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkPermissions(Context context, String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null &&
                permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}