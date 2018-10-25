package com.lynx.wind.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class PermissionManager {

    private Activity activity;
    private PermissionListener listener;
    private static int REQ_PERMISSION = 27612;
    private String TAG = "";

    public PermissionManager(Activity activity, PermissionListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    /**
     * Check is permission has been granted
     */
    public static boolean isGranted(Context context, String permission) {
        // Check device OS
        // When device OS is 22 or below,
        // don't worry, it's always enabled
        return Build.VERSION.SDK_INT <= 22 ||
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request single permission
     */
    public void check(String tag, String permission){
        String[] permissions = {permission};
        check(tag, permissions);
    }

    /**
     * Request multiple permission
     */
    public void check(String tag, String[] permissions) {
        this.TAG = tag;

        // Check device OS
        if (Build.VERSION.SDK_INT <= 22) {
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            listener.onPermissionGranted(TAG, permissions);
        }else if(permissions != null && permissions.length > 0){
            // If list not empty, request all permissions
            ActivityCompat.requestPermissions(activity, permissions, REQ_PERMISSION);
        }
    }

    /**
     * Check the result after request permission
     * Must be called inside onRequestPermissionsResult()
     */
    public void result(int requestCode, String[] permissions, int[] grantResults){
        // Check requestCode
        if(requestCode == REQ_PERMISSION){

            // Vessel of all granted permissions
            ArrayList<String> granted = new ArrayList<>();
            // Vessel of all denied permissions
            ArrayList<String> denied = new ArrayList<>();
            // Vessel of all permissions which are
            // denied and rationale checkbox is checked
            ArrayList<String> disabled = new ArrayList<>();

            // Iterate all permissions then
            // put every single permission to each vessel
            for (int i = 0; i < permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) granted.add(permissions[i]);
                else if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) denied.add(permissions[i]);
                else disabled.add(permissions[i]);
            }

            if(granted.size() > 0) listener.onPermissionGranted(TAG, granted.toArray(new String[0]));
            if(denied.size() > 0) listener.onPermissionDenied(TAG, denied.toArray(new String[0]));
            if(disabled.size() > 0) listener.onPermissionDisabled(TAG, disabled.toArray(new String[0]));
        }
    }

    /**
     * Show alert dialog to redirect user to setting page
     * WARNING Activity must use Theme.AppCompat theme (or descendant)
     */
    public void alert(String body, String positiveButton, String negativeButton){
        new AlertDialog.Builder(activity)
                .setMessage(body)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null ));
                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(negativeButton, null)
                .setCancelable(true)
                .show();
    }
}
