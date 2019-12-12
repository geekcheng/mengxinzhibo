package com.tongchuang.phonelive;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongchuang.phonelive.bean.ConfigBean;
import com.tongchuang.phonelive.bean.LevelBean;
import com.tongchuang.phonelive.bean.LiveGiftBean;
import com.tongchuang.phonelive.bean.UserBean;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.im.ImMessageUtil;
import com.tongchuang.phonelive.im.ImPushUtil;
import com.tongchuang.phonelive.interfaces.CommonCallback;
import com.tongchuang.phonelive.utils.L;
import com.tongchuang.phonelive.utils.SpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2017/8/4.
 */

public class AppConfig {
    //视频宽高比例
    public static final float mVideoRadio = 1.78f;

    /**
     * 视频高度
     */
    public static int getVidowHeight() {
        return (int) (AppContext.sInstance.getResources().getDisplayMetrics().widthPixels / 2 * mVideoRadio);
    }

    //域名
    public static final String HOST = BuildConfig.SERVER_URL;

    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = AppContext.sInstance.getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "tongchuang";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_PATH = DCMI_PATH + "/" + DIR_NAME + "/video/";
    //下载贴纸的时候保存的路径
    public static final String VIDEO_TIE_ZHI_PATH = DCMI_PATH + "/" + DIR_NAME + "/tieZhi/";
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = DCMI_PATH + "/" + DIR_NAME + "/camera/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";

    //QQ登录是否与PC端互通
    public static final boolean QQ_LOGIN_WITH_PC = false;
    //是否使用游戏
    public static final boolean GAME_ENABLE = true;
    //系统消息图标是否使用app图标
    public static final boolean SYSTEM_MSG_APP_ICON = false;

    private static AppConfig sInstance;

    private AppConfig() {

    }

    public static AppConfig getInstance() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) {
                    sInstance = new AppConfig();
                }
            }
        }
        return sInstance;
    }

    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private String mVersion;
    private boolean mLoginIM;//IM是否登录了
    private boolean mLaunched;//App是否启动了
    private String mJPushAppKey;//极光推送的AppKey
    private String mTxLocationKey;//腾讯定位，地图的AppKey
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private List<LiveGiftBean> mGiftList;
    private boolean mFrontGround;

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance()
                    .getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                }
            } else {
                return "-1";
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }

    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            HttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }

    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    /**
     * 区
     */
    public String getDistrict() {
        if (TextUtils.isEmpty(mDistrict)) {
            mDistrict = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_DISTRICT);
        }
        return mDistrict == null ? "" : mDistrict;
    }

    public void setUserBean(UserBean bean) {
        mUserBean = bean;
    }

    public UserBean getUserBean() {
        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            }
        }
        return mUserBean;
    }


    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        L.e("登录成功", "uid------>" + uid);
        L.e("登录成功", "token------>" + token);
        mUid = uid;
        mToken = token;
        if (save) {
            Map<String, String> map = new HashMap<>();
            map.put(SpUtil.UID, uid);
            map.put(SpUtil.TOKEN, token);
            SpUtil.getInstance().setMultiStringValue(map);
        }
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        mLoginIM = false;
        ImMessageUtil.getInstance().logoutEMClient();
        ImPushUtil.getInstance().logout();
        SpUtil.getInstance().clear();
    }


    /**
     * 设置位置信息
     *
     * @param lng      经度
     * @param lat      纬度
     * @param province 省
     * @param city     市
     */
    public void setLocationInfo(double lng, double lat, String province, String city, String district) {
        mLng = lng;
        mLat = lat;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.LOCATION_LNG, String.valueOf(lng));
        map.put(SpUtil.LOCATION_LAT, String.valueOf(lat));
        map.put(SpUtil.LOCATION_PROVINCE, province);
        map.put(SpUtil.LOCATION_CITY, city);
        map.put(SpUtil.LOCATION_DISTRICT, district);
        SpUtil.getInstance().setMultiStringValue(map);
    }


    public boolean isLoginIM() {
        return mLoginIM;
    }

    public void setLoginIM(boolean loginIM) {
        mLoginIM = loginIM;
    }

    /**
     * 获取版本号
     */
    public String getVersion() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = AppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(AppContext.sInstance.getPackageName(), 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    /**
     * 获取MetaData中的极光AppKey
     *
     * @return
     */
    public String getJPushAppKey() {
        if (mJPushAppKey == null) {
            mJPushAppKey = getMetaDataString("JPUSH_APPKEY");
        }
        return mJPushAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppKey
     *
     * @return
     */
    public String getTxLocationKey() {
        if (mTxLocationKey == null) {
            mTxLocationKey = getMetaDataString("TencentMapSDK");
        }
        return mTxLocationKey;
    }

    private String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = AppContext.sInstance.getPackageManager().getApplicationInfo(AppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }
        int size = mLevelMap.size();
        if (mLevelMap == null || size == 0) {
            return null;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }
        int size = mAnchorLevelMap.size();
        if (mAnchorLevelMap == null || size == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }

    public List<LiveGiftBean> getGiftList() {
        return mGiftList;
    }

    public void setGiftList(List<LiveGiftBean> giftList) {
        mGiftList = giftList;
    }

    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = AppContext.sInstance.getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }

    /**
     * 是否显示"心愿单"
     */
    public static boolean showWishBill() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            case "tianjiaoa":
                return true;
            case "mili":
                return true;
            case "fengche":
                return true;
            case "huatian":
                return true;
            case "qinglong":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否显示"心愿单"，直播间中
     */
    public static boolean showWishBillBigLogo() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 主界面第一栏显示内容
     *
     * @return 0 短视频；1 直播
     */
    public static int showMainFirstType() {
        switch (BuildConfig.FLAVOR) {
            case "dylivea":
                return 0;
            default:
                return 1;
        }
    }

    /**
     * 主界面第二栏显示内容
     *
     * @return 0 直播；1 短视频；2 动态
     */
    public static int showMainSecondType() {
        switch (BuildConfig.FLAVOR) {
            case "dylivea":
                return 0;
            case "mili":
                return 1;
            case "fengche":
                return 1;
            case "huatian":
                return 1;
            default:
                return 2;
        }
    }

    /**
     * 在“我的”界面
     * 0，活动中心；1，排行；2，消息；
     */
    public static int showRankingOnMainMe() {
        switch (BuildConfig.FLAVOR) {
            case "mili":
                return 1;
            case "fengche":
                return 1;
            case "huatian":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 主界面是否显示“活动”
     */
    public static boolean showMainAction() {
        switch (BuildConfig.FLAVOR) {
            case "mili":
                return true;
            case "fengche":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏拍摄图片
     */
    public static boolean hideTakePictureOnVideo() {
        switch (BuildConfig.FLAVOR) {
            case "mili":
                return true;
            case "fengche":
                return true;
            case "huatian":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏私信功能
     */
    public static boolean hideChatRoom() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            case "tianjiaoa":
                return true;
            case "mili":
                return true;
            case "fengche":
                return true;
            case "huatian":
                return true;
            case "qinglong":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏观众禁言信息
     */
    public static boolean hideShutUpForAudience() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return false;
            case "tianjiaoa":
                return false;
            case "mili":
                return true;
            case "fengche":
                return true;
            case "huatian":
                return true;
            case "qinglong":
                return false;
            default:
                return false;
        }
    }

    /**
     * 是否上下滑动切换直播间
     */
    public static boolean liveRoomScroll() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return false;
            case "tianjiaoa":
                return true;
            case "mili":
                return false;
            case "fengche":
                return false;
            case "huatian":
                return false;
            case "qinglong":
                return false;
            default:
                return true;
        }
    }

    /**
     * 支付宝充值时，是否显示APP名称
     */
    public static boolean aliPayShowAppName() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 禁止定位功能（全部禁止）
     */
    public static boolean forbidLocation() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            case "huatian":
                return true;
            default:
                return true;
        }
    }

    /**
     * 守护文字重新定义
     */
    public static boolean guardWordModify() {
        switch (BuildConfig.FLAVOR) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 将七天守护图标变灰
     */
    public static boolean grayGuardIcon() {
        switch (BuildConfig.FLAVOR) {
            case "mili":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏"动态"分享按钮
     */
    public static boolean hideTrendShare() {
        switch (BuildConfig.FLAVOR) {
            case "qinglong":
                return true;
            default:
                return false;
        }
    }
}
