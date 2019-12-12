package com.tongchuang.phonelive;

/**
 * Created by cxf on 2018/6/7.
 */

public class Constants {
    public static final String URL = "url";
    public static final String PAYLOAD = "payload";
    public static final String SEX = "sex";
    public static final String NICK_NAME = "nickname";
    public static final String CITY = "city";
    public static final String AVATAR = "avatar";
    public static final String SIGN = "sign";
    public static final String TO_UID = "toUid";
    public static final String TO_NAME = "toName";
    public static final String STREAM = "stream";
    public static final String LIMIT = "limit";
    public static final String UID = "uid";
    public static final String TIP = "tip";
    public static final String SHOW_INVITE = "showInvite";
    public static final String USER_BEAN = "userBean";
    public static final String CLASS_ID = "classID";
    public static final String CLASS_NAME = "className";
    public static final String CHECKED_ID = "checkedId";
    public static final String CHECKED_COIN = "checkedCoin";
    public static final String LIVE_DANMU_PRICE = "danmuPrice";
    public static final String COIN_NAME = "coinName";
    public static final String LIVE_BEAN = "liveBean";
    public static final String LIVE_TYPE = "liveType";
    public static final String LIVE_KEY = "liveKey";
    public static final String LIVE_POSITION = "livePosition";
    public static final String LIVE_TYPE_VAL = "liveTypeVal";
    public static final String LIVE_UID = "liveUid";
    public static final String LIVE_NAME = "liveName";
    public static final String LIVE_STREAM = "liveStream";
    public static final String LIVE_HOME = "liveHome";
    public static final String LIVE_FOLLOW = "liveFollow";
    public static final String LIVE_NEAR = "liveNear";
    public static final String LIVE_PERSONAL_CENTER = "livePersonalCenter";
    public static final String LIVE_CLASS_PREFIX = "liveClass_";
    public static final String HAS_GAME = "hasGame";
    public static final String ANCHOR = "anchor";
    public static final String FOLLOW = "follow";
    public static final String DIAMONDS = "金币";
    public static final String VOTES = "米币";//原“映票”
    public static final String PAY_ALI_NOT_ENABLE = "支付宝未接入";
    public static final String PAY_WX_NOT_ENABLE = "微信支付未接入";
    public static final String PAY_ALL_NOT_ENABLE = "未开启支付";
    public static final int PAY_TYPE_ALI = 8001;
    public static final int PAY_TYPE_WX = 8002;

    public static final String PACKAGE_NAME_ALI = "com.eg.android.AlipayGphone";//支付宝的包名
    public static final String PACKAGE_NAME_WX = "com.tencent.mm";//微信的包名
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";//QQ的包名
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ADDRESS = "address";
    public static final String SCALE = "scale";
    public static final String SELECT_IMAGE_PATH = "selectedImagePath";
    public static final String COPY_PREFIX = "copy://";
    public static final int GUARD_TYPE_NONE = 0;
    public static final int GUARD_TYPE_MONTH = 1;
    public static final int GUARD_TYPE_YEAR = 2;

    public static final String GIF_GIFT_PREFIX = "gif_gift_";
    public static final String GIF_CAR_PREFIX = "gif_car_";
    public static final String DOWNLOAD_MUSIC = "downloadMusic";
    public static final String LINK = "link";
    public static final String REPORT = "report";
    public static final String SAVE = "save";
    public static final String DELETE = "delete";
    public static final String SHARE_FROM = "shareFrom";
    public static final int SHARE_FROM_LIVE = 101;
    public static final int SHARE_FROM_HOME = 102;
    public static final int SETTING_MODIFY_PWD = 15;
    public static final int SETTING_UPDATE_ID = 16;
    public static final int SETTING_CLEAR_CACHE = 18;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;
    public static final int FOLLOW_FROM_LIST = 1001;
    public static final int FOLLOW_FROM_FOLLOW = 1002;
    public static final int FOLLOW_FROM_FANS = 1003;
    public static final int FOLLOW_FROM_SEARCH = 1004;
    public static final int FOLLOW_FROM_LIVE = 1005;
    public static final int FOLLOW_FROM_HOME = 1006;
    public static final int FOLLOW_FROM_VIDEO_PLAY = 1007;
    public static final int FOLLOW_FROM_TICKET_BUY = 1008;
    //直播房间类型
    public static final int LIVE_TYPE_NORMAL = 0;//普通房间
    public static final int LIVE_TYPE_PWD = 1;//密码房间
    public static final int LIVE_TYPE_PAY = 2;//收费房间
    public static final int LIVE_TYPE_TIME = 3;//计时房间
    public static final int LIVE_TYPE_ACT = 4;//门票房间
    //主播直播间功能
    public static final int LIVE_FUNC_BEAUTY = 2001;//美颜
    public static final int LIVE_FUNC_CAMERA = 2002;//切换摄像头
    public static final int LIVE_FUNC_FLASH = 2003;//闪光灯
    public static final int LIVE_FUNC_MUSIC = 2004;//音乐
    public static final int LIVE_FUNC_SHARE = 2005;//分享
    public static final int LIVE_FUNC_GAME = 2006;//游戏
    public static final int LIVE_FUNC_RED_PACK = 2007;//红包
    public static final int LIVE_FUNC_LINK_MIC = 2008;//连麦
    public static final int LIVE_FUNC_LINK_MESSAGE = 2009;//消息
    public static final int LIVE_FUNC_LINK_WISH = 2010;//心愿单
    //socket
    public static final String SOCKET_CONN = "conn";
    public static final String SOCKET_BROADCAST = "broadcastingListen";
    public static final String SOCKET_SEND = "broadcast";
    public static final String SOCKET_STOP_PLAY = "stopplay";//超管关闭直播间
    public static final String SOCKET_STOP_LIVE = "stopLive";//超管关闭直播间
    public static final String SOCKET_SEND_MSG = "SendMsg";//发送文字消息，点亮，用户进房间  PS:这种混乱的设计是因为服务器端逻辑就是这样设计的,客户端无法自行修改
    public static final String SOCKET_LIGHT = "light";//飘心
    public static final String SOCKET_SEND_GIFT = "SendGift";//送礼物
    public static final String SOCKET_SEND_BARRAGE = "SendBarrage";//发弹幕
    public static final String SOCKET_LEAVE_ROOM = "disconnect";//用户离开房间
    public static final String SOCKET_LIVE_END = "StartEndLive";//主播关闭直播
    public static final String SOCKET_SYSTEM = "SystemNot";//系统消息
    public static final String SOCKET_KICK = "KickUser";//踢人
    public static final String SOCKET_SHUT_UP = "ShutUpUser";//禁言
    public static final String SOCKET_SET_ADMIN = "setAdmin";//设置或取消管理员
    public static final String SOCKET_CHANGE_LIVE = "changeLive";//切换计时收费类型
    public static final String SOCKET_UPDATE_VOTES = "updateVotes";//门票或计时收费时候更新主播的映票数
    public static final String SOCKET_FAKE_FANS = "requestFans";//僵尸粉
    public static final String SOCKET_LINK_MIC = "ConnectVideo";//连麦
    public static final String SOCKET_LINK_MIC_ANCHOR = "LiveConnect";//主播连麦
    public static final String SOCKET_LINK_MIC_PK = "LivePK";//主播PK
    public static final String SOCKET_BUY_GUARD = "BuyGuard";//购买守护
    public static final String SOCKET_RED_PACK = "SendRed";//红包
    //游戏socket
    public static final String SOCKET_GAME_ZJH = "startGame";//炸金花
    public static final String SOCKET_GAME_HD = "startLodumaniGame";//海盗船长
    public static final String SOCKET_GAME_NZ = "startCattleGame";//开心牛仔
    public static final String SOCKET_GAME_ZP = "startRotationGame";//幸运转盘
    public static final String SOCKET_GAME_EBB = "startShellGame";//二八贝

    public static final int SOCKET_WHAT_CONN = 0;
    public static final int SOCKET_WHAT_DISCONN = 2;
    public static final int SOCKET_WHAT_BROADCAST = 1;
    //socket 用户类型
    public static final int SOCKET_USER_TYPE_NORMAL = 30;//普通用户
    public static final int SOCKET_USER_TYPE_ADMIN = 40;//房间管理员
    public static final int SOCKET_USER_TYPE_ANCHOR = 50;//主播
    public static final int SOCKET_USER_TYPE_SUPER = 60;//超管

    //提现账号类型，1表示支付宝，2表示微信，3表示银行卡
    public static final int CASH_ACCOUNT_ALI = 1;
    public static final int CASH_ACCOUNT_WX = 2;
    public static final int CASH_ACCOUNT_BANK = 3;
    public static final String CASH_ACCOUNT_ID = "cashAccountID";
    public static final String CASH_ACCOUNT = "cashAccount";
    public static final String CASH_ACCOUNT_TYPE = "cashAccountType";


    public static final int RED_PACK_TYPE_AVERAGE = 0;//平均红包
    public static final int RED_PACK_TYPE_SHOU_QI = 1;//拼手气红包
    public static final int RED_PACK_SEND_TIME_NORMAL = 0;//立即发放
    public static final int RED_PACK_SEND_TIME_DELAY = 1;//延时发放

    public static final int JPUSH_TYPE_LIVE = 1;//直播
    public static final int JPUSH_TYPE_MESSAGE = 2;//消息

    public static final String VIDEO_TREND = "videoTrend";
    public static final String VIDEO_HOME = "videoHome";
    public static final String VIDEO_USER = "videoUser_";
    public static final String VIDEO_KEY = "videoKey";
    public static final String VIDEO_POSITION = "videoPosition";
    public static final String VIDEO_PAGE = "videoPage";
    public static final String VIDEO_BEAN = "videoBean";
    public static final String VIDEO_ID = "videoId";
    public static final String VIDEO_COMMENT_BEAN = "videoCommnetBean";
    public static final String VIDEO_FACE_OPEN = "videoOpenFace";
    public static final String VIDEO_FACE_HEIGHT = "videoFaceHeight";
    public static final String VIDEO_DURATION = "videoDuration";
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_FROM_RECORD = "videoFromRecord";
    public static final String VIDEO_MUSIC_BEAN = "videoMusicBean";
    public static final String VIDEO_MUSIC_NAME_PREFIX = "videoMusicName_";
    public static final String VIDEO_SAVE_TYPE = "videoSaveType";
    public static final String VIDEO_TIME_LONG = "videoTimeLong";
    public static final int VIDEO_SAVE_SAVE_AND_PUB = 1;//保存并发布
    public static final int VIDEO_SAVE_SAVE = 2;//仅保存
    public static final int VIDEO_SAVE_PUB = 3;//仅发布

    public static final String PICTURE_LIST = "pictureList";

    //活动
    public static final int TICKET_SELL = 0;//预售
    public static final int TICKET_HISTORY = 1;//历史
    public static final int TICKET_CENTER_ING = 2;//活动中心，进行中
    public static final int TICKET_CENTER_ED = 3;//活动中心，历史
}
