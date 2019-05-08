package com.lynx.wind.permissionsample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lynx.wind.permission.PermissionListener;
import com.lynx.wind.permission.PermissionManager;

import org.jetbrains.annotations.NotNull;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(getContext() == null) return;

        if(requestCode == PermissionManager.REQ_SETTING &&
                PermissionManager.Companion.isGranted(getContext(), Manifest.permission.WRITE_CALENDAR)){
            Toast.makeText( getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText( getContext(), "Permission disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.result(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(@NotNull String[] permissions, @NotNull String tag) {
        Toast.makeText(getContext(), "Permission granted " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(@NotNull String[] permissions, @NotNull String tag) {
        Toast.makeText(getContext(), "Permission denied " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDisabled(@NotNull String[] permissions, @NotNull String tag) {
        manager.alert("Permission required", "Not Now", "To Setting");
    }
}
