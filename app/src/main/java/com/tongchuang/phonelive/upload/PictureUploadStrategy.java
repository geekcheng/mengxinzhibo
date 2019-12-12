package com.tongchuang.phonelive.upload;

import java.io.File;
import java.util.List;

public interface PictureUploadStrategy {
    void upload(List<File> files, PictureUploadCallback callback);

    void cancel();
}
