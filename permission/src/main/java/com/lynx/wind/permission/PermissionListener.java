package com.lynx.wind.permission;

/**
 * The interface Permission listener.
 */
public interface PermissionListener {

    /**
     * On permission granted.
     * @param tag         the tag
     * @param permissions the permissions
     */
    void onPermissionGranted(String[] permissions, String tag);

    /**
     * On permission denied.
     * @param tag         the tag
     * @param permissions the permissions
     */
    void onPermissionDenied(String[] permissions, String tag);

    /**
     * On permission disabled.
     * @param tag         the tag
     * @param permissions the permissions
     */
    void onPermissionDisabled(String[] permissions, String tag);
}
