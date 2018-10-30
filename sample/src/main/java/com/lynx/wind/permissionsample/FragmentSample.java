package com.lynx.wind.permissionsample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lynx.wind.permission.PermissionListener;
import com.lynx.wind.permission.PermissionManager;

public class FragmentSample extends Fragment implements PermissionListener {

    private PermissionManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new PermissionManager(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn = view.findViewById(R.id.btn_permission);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.check(Manifest.permission.WRITE_CALENDAR);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        log("This is request code " + requestCode);
        manager.result(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(String[] permissions, String tag) {
        Toast.makeText(getContext(), "Permission granted " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String[] permissions, String tag) {
        Toast.makeText(getContext(), "Permission denied " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDisabled(String[] permissions, String tag) {
        manager.alert("Permission required", "To Setting", "Not Now");
    }

    private void log(String msg) {
        Log.d("TAG", msg);
    }
}
