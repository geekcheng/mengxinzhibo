package com.tongchuang.phonelive.upload;

public interface PictureUploadCallback {
    void onSuccess(String url);

    void onFailure();
}
