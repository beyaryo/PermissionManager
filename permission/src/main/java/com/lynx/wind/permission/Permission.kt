package com.lynx.wind.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

/**
 * The interface Permission listener.
 */
interface PermissionListener {

    /**
     * On permission granted.
     * @param tag         the tag
     * @param permissions the permissions
     */
    fun onPermissionGranted(permissions: Array<String>, tag: String)

    /**
     * On permission denied.
     * @param tag         the tag
     * @param permissions the permissions
     */
    fun onPermissionDenied(permissions: Array<String>, tag: String)

    /**
     * On permission disabled.
     * @param tag         the tag
     * @param permissions the permissions
     */
    fun onPermissionDisabled(permissions: Array<String>, tag: String)
}

class PermissionManager {

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private val listener: PermissionListener
    private var TAG = ""

    constructor(activity: Activity, listener: PermissionListener) {
        this.activity = activity
        this.listener = listener
    }

    constructor(fragment: Fragment, listener: PermissionListener) {
        this.fragment = fragment
        this.listener = listener
    }

    /**
     * Request list permissions
     */
    fun check(permissions: List<String>) = check(permissions.toTypedArray(), "")
    fun check(permissions: List<String>, tag: String) = check(permissions.toTypedArray(), tag)

    /**
     * Request single permission
     */
    fun check(permission: String) = check(permission, "")
    fun check(permission: String, tag: String) = check(arrayOf(permission), tag)

    /**
     * Request multiple permission with tag
     */
    fun check(permissions: Array<String>) = check(permissions, "")
    fun check(permissions: Array<String>, tag: String) {
        this.TAG = tag

        // Check device OS
        if (Build.VERSION.SDK_INT <= 22) {
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            listener.onPermissionGranted(permissions, TAG)
        } else if (permissions.isNotEmpty()) {
            // If list not empty, request all permissions
            activity?.requestPermissions(permissions, REQ_PERMISSION)
            fragment?.requestPermissions(permissions, REQ_PERMISSION)
        }
    }

    /**
     * Request all permissions in manifest with tag
     */
    fun checkAllFromManifest() = checkAllFromManifest("")
    fun checkAllFromManifest(tag: String) {
        var permissions: Array<String>? = arrayOf()

        try {
            activity?.let {
                permissions = it
                        .packageManager
                        .getPackageInfo(it.packageName, PackageManager.GET_PERMISSIONS)
                        .requestedPermissions
            }

            fragment?.let {
                permissions = it
                        .context?.packageManager
                        ?.getPackageInfo(it.activity?.packageName, PackageManager.GET_PERMISSIONS)
                        ?.requestedPermissions
            }
        } catch (ignore: Exception) {
        }

        permissions?.let { check(it, tag) }
    }

    /**
     * Check the result after request permission
     * Must be called inside onRequestPermissionsResult()
     */
    fun result(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check requestCode
        if (requestCode == REQ_PERMISSION) {

            // Vessel of all granted permissions
            val granted = ArrayList<String>()
            // Container of all denied permissions
            val denied = ArrayList<String>()
            // Contains all permissions which are
            // denied and rationale checkbox is checked
            val disabled = ArrayList<String>()

            // Iterate all permissions then
            // put every single permission to each vessel
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    granted.add(permissions[i])
                else if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions[i]))
                    denied.add(permissions[i])
                else if (fragment != null && fragment!!.shouldShowRequestPermissionRationale(permissions[i]))
                    denied.add(permissions[i])
                else
                    disabled.add(permissions[i])
            }

            if (granted.size > 0)
                listener.onPermissionGranted(granted.toTypedArray(), TAG)
            if (denied.size > 0)
                listener.onPermissionDenied(denied.toTypedArray(), TAG)
            if (disabled.size > 0)
                listener.onPermissionDisabled(disabled.toTypedArray(), TAG)
        }
    }

    /**
     * Show alert dialog to redirect user to setting page
     * WARNING Activity must use Theme.AppCompat theme (or descendant)
     */
    fun alert(body: String, negativeMsg: String, positiveMsg: String) =
            alert(body, negativeMsg, positiveMsg, ::doNothing, ::toSetting)

    fun alert(body: String, negativeMsg: String, positiveMsg: String, negativeAct: () -> Unit) =
            alert(body, negativeMsg, positiveMsg, negativeAct, ::toSetting)

    fun alert(body: String, negativeMsg: String, positiveMsg: String,
              negativeAct: () -> Unit, positiveAct: () -> Unit){
        val context = if (activity != null) activity else fragment?.context

        AlertDialog.Builder(context!!)
                .setMessage(body)
                .setPositiveButton(positiveMsg) { dialog, _ ->
                    positiveAct.invoke()
                    dialog.dismiss()
                }
                .setNegativeButton(negativeMsg){ dialog, _->
                    negativeAct.invoke()
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
    }

    private fun toSetting(){
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", if (activity != null)
                activity?.packageName
            else
                fragment?.activity?.packageName, null)
        }

        activity?.startActivityForResult(intent, REQ_SETTING)
        fragment?.startActivityForResult(intent, REQ_SETTING)
    }

    private fun doNothing(){}

    companion object {
        const val REQ_SETTING = 7901
        private const val REQ_PERMISSION = 27612

        /**
         * Check is permission has been granted
         */
        public fun isGranted(context: Context, permission: String): Boolean {
            // Check device OS
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            return Build.VERSION.SDK_INT <= 22 || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}