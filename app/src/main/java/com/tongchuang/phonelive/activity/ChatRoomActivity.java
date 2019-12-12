package com.tongchuang.phonelive.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.UserBean;
import com.tongchuang.phonelive.dialog.ChatVoiceInputDialog;
import com.tongchuang.phonelive.interfaces.ActivityResultCallback;
import com.tongchuang.phonelive.interfaces.ChatRoomActionListener;
import com.tongchuang.phonelive.interfaces.ImageResultCallback;
import com.tongchuang.phonelive.interfaces.KeyBoardHeightChangeListener;
import com.tongchuang.phonelive.utils.KeyBoardHeightUtil;
import com.tongchuang.phonelive.utils.ProcessImageUtil;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.ChatRoomViewHolder;

import java.io.File;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomActivity extends AbsActivity implements KeyBoardHeightChangeListener {

    private ViewGroup mRoot;
    private ViewGroup mContianer;
    private ChatRoomViewHolder mChatRoomViewHolder;
    private KeyBoardHeightUtil mKeyBoardHeightUtil;
    private ProcessImageUtil mImageUtil;
    private boolean mPaused;

    public static void forward(Context context, UserBean userBean, boolean following) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.FOLLOW, following);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        UserBean userBean = intent.getParcelableExtra(Constants.USER_BEAN);
        if (userBean == null) {
            return;
        }
        boolean following = intent.getBooleanExtra(Constants.FOLLOW, false);
        mRoot = (ViewGroup) findViewById(R.id.root);
        mContianer = (ViewGroup) findViewById(R.id.container);
        mChatRoomViewHolder = new ChatRoomViewHolder(mContext, mContianer, userBean, ChatRoomViewHolder.TYPE_ACTIVITY, following);
        mChatRoomViewHolder.setActionListener(new ChatRoomActionListener() {
            @Override
            public void onCloseClick() {
                superBackPressed();
            }

            @Override
            public void onPopupWindowChanged(final int height) {
                onKeyBoardChanged(height);
            }

            @Override
            public void onChooseImageClick() {
                checkReadWritePermissions();
            }

            @Override
            public void onCameraClick() {
                takePhoto();
            }

            @Override
            public void onVoiceInputClick() {
                checkVoiceRecordPermission(new Runnable() {
                    @Override
                    public void run() {
                        openVoiceInputDialog();
                    }
                });
            }

            @Override
            public void onLocationClick() {
                checkLocationPermission();
            }

            @Override
            public void onVoiceClick() {
                checkVoiceRecordPermission(new Runnable() {
                    @Override
                    public void run() {
                        if (mChatRoomViewHolder != null) {
                            mChatRoomViewHolder.clickVoiceRecord();
                        }
                    }
                });
            }

            @Override
            public ViewGroup getImageParentView() {
                return mRoot;
            }

//            @Override
//            public void onImageClick() {
//
//            }

        });
        mChatRoomViewHolder.addToParent();
        mChatRoomViewHolder.loadData();
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (mChatRoomViewHolder != null) {
                    mChatRoomViewHolder.sendImage(file.getAbsolutePath());
                }
            }

            @Override
            public void onFailure() {

            }
        });
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        mRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mKeyBoardHeightUtil!=null){
                    mKeyBoardHeightUtil.start();
                }
            }
        }, 500);
    }

    private void onKeyBoardChanged(int keyboardHeight) {

        if (mRoot != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRoot.getLayoutParams();
            params.setMargins(0, 0, 0, keyboardHeight);
            mRoot.setLayoutParams(params);
            if (mChatRoomViewHolder != null) {
                mChatRoomViewHolder.scrollToBottom();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.back();
        } else {
            superBackPressed();
        }
    }

    private void release() {
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.refreshLastMessage();
            mChatRoomViewHolder.release();
        }
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mKeyBoardHeightUtil = null;
        mChatRoomViewHolder = null;
        mImageUtil = null;
    }

    private void superBackPressed() {
        release();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }


    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if(mPaused){
            return;
        }
        if (keyboardHeight == 0 && mChatRoomViewHolder != null && mChatRoomViewHolder.isPopWindowShowed()) {
            return;
        }
        onKeyBoardChanged(keyboardHeight);
    }

    @Override
    public boolean isSoftInputShowed() {
        if (mKeyBoardHeightUtil != null) {
            return mKeyBoardHeightUtil.isSoftInputShowed();
        }
        return false;
    }

    /**
     * 聊天时候选择图片，检查读写权限
     */
    private void checkReadWritePermissions() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new Runnable() {
                    @Override
                    public void run() {
                        forwardChooseImage();
                    }
                });
    }

    /**
     * 前往选择图片页面
     */
    private void forwardChooseImage() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.startActivityForResult(new Intent(mContext, ChatChooseImageActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String imagePath = intent.getStringExtra(Constants.SELECT_IMAGE_PATH);
                    if (mChatRoomViewHolder != null) {
                        mChatRoomViewHolder.sendImage(imagePath);
                    }
                }
            }
        });
    }


    /**
     * 拍照
     */
    private void takePhoto() {
        if (mImageUtil != null) {
            mImageUtil.getImageByCamera(false);
        }
    }

    /**
     * 发送位置的时候检查定位权限
     */
    private void checkLocationPermission() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                new Runnable() {
                    @Override
                    public void run() {
                        forwardLocation();
                    }
                });
    }

    /**
     * 前往发送位置页面
     */
    private void forwardLocation() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.startActivityForResult(new Intent(mContext, LocationActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    double lat = intent.getDoubleExtra(Constants.LAT, 0);
                    double lng = intent.getDoubleExtra(Constants.LNG, 0);
                    int scale = intent.getIntExtra(Constants.SCALE, 0);
                    String address = intent.getStringExtra(Constants.ADDRESS);
                    if (lat > 0 && lng > 0 && scale > 0 && !TextUtils.isEmpty(address)) {
                        if (mChatRoomViewHolder != null) {
                            mChatRoomViewHolder.sendLocation(lat, lng, scale, address);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.im_get_location_failed));
                    }
                }
            }
        });
    }

    /**
     * 检查录音权限
     */
    private void checkVoiceRecordPermission(Runnable runnable) {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                runnable);
    }

    /**
     * 打开语音输入窗口
     */
    private void openVoiceInputDialog() {
        ChatVoiceInputDialog fragment = new ChatVoiceInputDialog();
        fragment.setChatRoomViewHolder(mChatRoomViewHolder);
        fragment.show(getSupportFragmentManager(), "ChatVoiceInputDialog");
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onResume();
        }
    }
}
