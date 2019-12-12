package com.tongchuang.phonelive.upload;

import java.io.File;

/**
 * Created by cxf on 2018/5/21.
 */

public class VideoUploadBean {
    private File mVideoFile;
    private File mImageFile;
    private String mResultVideoUrl;//视频上传结果的url
    private String mResultImageUrl;//图片上传结果的url


    public VideoUploadBean(File videoFile, File imageFile) {
        mVideoFile = videoFile;
        mImageFile = imageFile;
    }

    public File getVideoFile() {
        return mVideoFile;
    }

    public void setVideoFile(File videoFile) {
        mVideoFile = videoFile;
    }

    public File getImageFile() {
        return mImageFile;
    }

    public void setImageFile(File imageFile) {
        mImageFile = imageFile;
    }

    public String getResultVideoUrl() {
        return mResultVideoUrl;
    }

    public void setResultVideoUrl(String resultVideoUrl) {
        mResultVideoUrl = resultVideoUrl;
    }

    public String getResultImageUrl() {
        return mResultImageUrl;
    }

    public void setResultImageUrl(String resultImageUrl) {
        mResultImageUrl = resultImageUrl;
    }

    public void deleteFile() {
        if (mVideoFile != null && mVideoFile.exists()) {
            mVideoFile.delete();
        }
        if (mImageFile != null && mImageFile.exists()) {
            mImageFile.delete();
        }
    }

}
