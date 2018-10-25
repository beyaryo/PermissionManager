package com.lynx.wind.permission;

public interface PermissionListener {
    void onPermissionGranted(String tag, String[] permissions);
    void onPermissionDenied(String tag, String[] permissions);
    void onPermissionDisabled(String tag, String[] permissions);
}
