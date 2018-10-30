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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private Activity activity;
    private Fragment fragment;
    private PermissionListener listener;
    private static int REQ_PERMISSION = 27612;
    private String TAG = "";

    public PermissionManager(Activity activity, PermissionListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public PermissionManager(Fragment fragment, PermissionListener listener) {
        this.fragment = fragment;
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
     * Request list permissions without tag
     */
    public void check(List<String> permissions) {
        check(permissions.toArray(new String[0]), "");
    }

    /**
     * Request list permissions with tag
     */
    public void check(List<String> permissions, String tag) {
        check(permissions.toArray(new String[0]), tag);
    }

    /**
     * Request single permission without tag
     */
    public void check(String permission) {
        String[] permissions = {permission};
        check(permissions, "");
    }

    /**
     * Request single permission with tag
     */
    public void check(String permission, String tag) {
        String[] permissions = {permission};
        check(permissions, tag);
    }

    /**
     * Request multiple permissions without tag
     */
    public void check(String[] permissions) {
        check(permissions, "");
    }

    /**
     * Request multiple permission with tag
     */
    public void check(String[] permissions, String tag) {
        this.TAG = tag;

        // Check device OS
        if (Build.VERSION.SDK_INT <= 22) {
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            listener.onPermissionGranted(permissions, TAG);
        } else if (permissions != null && permissions.length > 0) {
            // If list not empty, request all permissions
            if (activity != null) activity.requestPermissions(permissions, REQ_PERMISSION);
            else fragment.requestPermissions(permissions, REQ_PERMISSION);
        }
    }

    /**
     * Request all permissions in manifest without tag
     */
    public void checkAllFromManifest(){
        checkAllFromManifest("");
    }

    /**
     * Request all permissions in manifest with tag
     */
    public void checkAllFromManifest(String tag){
        String[] permissions = {};

        try{
            if(activity != null)
                permissions = activity
                        .getPackageManager()
                        .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS)
                        .requestedPermissions;
            else
                permissions = fragment
                        .getContext().getPackageManager()
                        .getPackageInfo(fragment.getActivity().getPackageName(), PackageManager.GET_PERMISSIONS)
                        .requestedPermissions;
        }catch (Exception ignore){}

        check(permissions, tag);
    }

    /**
     * Check the result after request permission
     * Must be called inside onRequestPermissionsResult()
     */
    public void result(int requestCode, String[] permissions, int[] grantResults) {
        // Check requestCode
        if (requestCode == REQ_PERMISSION) {

            // Vessel of all granted permissions
            ArrayList<String> granted = new ArrayList<>();
            // Vessel of all denied permissions
            ArrayList<String> denied = new ArrayList<>();
            // Vessel of all permissions which are
            // denied and rationale checkbox is checked
            ArrayList<String> disabled = new ArrayList<>();

            // Iterate all permissions then
            // put every single permission to each vessel
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    granted.add(permissions[i]);
                else if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]))
                    denied.add(permissions[i]);
                else if (fragment.shouldShowRequestPermissionRationale(permissions[i]))
                    denied.add(permissions[i]);
                else disabled.add(permissions[i]);
            }

            if (granted.size() > 0)
                listener.onPermissionGranted(granted.toArray(new String[0]), TAG);
            if (denied.size() > 0) listener.onPermissionDenied(denied.toArray(new String[0]), TAG);
            if (disabled.size() > 0)
                listener.onPermissionDisabled(disabled.toArray(new String[0]), TAG);
        }
    }

    /**
     * Show alert dialog to redirect user to setting page
     * WARNING Activity must use Theme.AppCompat theme (or descendant)
     */
    public void alert(String body, String positiveButton, String negativeButton) {
        Context context = activity;
        if (fragment != null) context = fragment.getContext();

        new AlertDialog.Builder(context)
                .setMessage(body)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String packageName = "";
                        if (activity != null) packageName = activity.getPackageName();
                        else packageName = fragment.getActivity().getPackageName();

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", packageName, null));

                        if (activity != null) activity.startActivity(intent);
                        else fragment.startActivity(intent);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(negativeButton, null)
                .setCancelable(true)
                .show();
    }
}
