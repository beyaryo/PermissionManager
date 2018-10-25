package com.lynx.wind.permissionsample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lynx.wind.permission.PermissionListener;
import com.lynx.wind.permission.PermissionManager;

public class MainActivity extends AppCompatActivity implements PermissionListener {

    // PermissionManager instance
    private PermissionManager manager = new PermissionManager(this, this);

    // Textview to display the permission request result
    private TextView txtGranted, txtDenied, txtDisabled;

    // Instance of single permission
    private String singlePermission = Manifest.permission.CAMERA;
    // Instance of multiple permission
    private String[] multiplePermission = {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if camera permission is granted
        if (PermissionManager.isGranted(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera enabled", Toast.LENGTH_SHORT).show();
        }

        // Bind view
        txtGranted = findViewById(R.id.txt_granted);
        txtDenied = findViewById(R.id.txt_denied);
        txtDisabled = findViewById(R.id.txt_disabled);
        Button btnSingle = findViewById(R.id.btn_single);
        Button btnMultiple = findViewById(R.id.btn_multiple);

        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.check("", singlePermission);
            }
        });

        btnMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.check("", multiplePermission);
            }
        });
    }

    /**
     * Will be called after user do action on permission dialog
     * REQUIRED inside this function must call
     * <PermissionManager instance>.result()
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.result(requestCode, permissions, grantResults);
    }

    /**
     * Override from PermissionListener interface
     * No explanation needed for this method
     * Every one hope this method will be called
     * so the permission is granted XD
     */
    @Override
    public void onPermissionGranted(String tag, String[] permissions) {
        // Do something here when permission is granted
        StringBuilder msg = new StringBuilder("Granted (" + permissions.length + ")");

        for (String perm : permissions) {
            msg.append("\n").append(perm);
        }

        txtGranted.setText(msg);
    }

    /**
     * Override from PermissionListener interface
     * This method will be called
     * when user click deny on some permission requested
     */
    @Override
    public void onPermissionDenied(String tag, String[] permissions) {
        StringBuilder msg = new StringBuilder("Denied (" + permissions.length + ")");

        for (String perm : permissions) {
            msg.append("\n").append(perm);
        }

        txtDenied.setText(msg);
    }

    /**
     * Override from PermissionListener interface
     * This method will be called
     * when user click deny and check "Don't ask again" checkbox on some permission requested
     * or user disabled the permission from setting
     */
    @Override
    public void onPermissionDisabled(String tag, String[] permissions) {
        StringBuilder msg = new StringBuilder("Disabled (" + permissions.length + ")");

        for (String perm : permissions) {
            msg.append("\n").append(perm);
        }

        txtDisabled.setText(msg);

        // Show alert dialog when some permissions are disabled
        manager.alert("Some permission is required", "To setting", "Not now");
    }
}
