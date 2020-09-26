package com.itube.android.permission;

public interface MyPermissionListener {
    void onGranted(int resultCode);
    void onDenied(int resultCode);
}
