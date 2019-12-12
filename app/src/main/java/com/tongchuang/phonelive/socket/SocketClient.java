package com.tongchuang.phonelive.socket;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongchuang.phonelive.AppConfig;
import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.LiveBuyGuardMsgBean;
import com.tongchuang.phonelive.bean.LiveChatBean;
import com.tongchuang.phonelive.bean.LiveDanMuBean;
import com.tongchuang.phonelive.bean.LiveEnterRoomBean;
import com.tongchuang.phonelive.bean.LiveReceiveGiftBean;
import com.tongchuang.phonelive.bean.LiveUserGiftBean;
import com.tongchuang.phonelive.bean.UserBean;
import com.tongchuang.phonelive.utils.L;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cxf on 2018/10/9.
 */

public class SocketClient {

    private static final String TAG = "socket";
    private Socket mSocket;
    private String mLiveUid;
    private String mStream;
    private SocketHandler mSocketHandler;

    public SocketClient(String url, SocketMessageListener listener) {
        if (!TextUtils.isEmpty(url)) {
            try {
                IO.Options option = new IO.Options();
                option.forceNew = true;
                option.reconnection = true;
                option.reconnectionDelay = 2000;
                mSocket = IO.socket(url, option);
                mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
                mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
                mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
                mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
                mSocket.on(Constants.SOCKET_CONN, onConn);//连接socket消息
                mSocket.on(Constants.SOCKET_BROADCAST, onBroadcast);//接收服务器广播的具体业务逻辑相关的消息
                mSocketHandler = new SocketHandler(listener);
            } catch (Exception e) {
                L.e(TAG, "socket url 异常--->" + e.getMessage());
            }
        }
    }


    public void connect(String liveuid, String stream) {
        mLiveUid = liveuid;
        mStream = stream;
        if (mSocket != null) {
            mSocket.connect();
        }
        if (mSocketHandler != null) {
            mSocketHandler.setLiveUid(liveuid);
        }
    }

    public void disConnect() {
        if (mSocket != null) {
            mSocket.close();
            mSocket.off();
        }
        if (mSocketHandler != null) {
            mSocketHandler.release();
        }
        mSocketHandler = null;
        mLiveUid = null;
        mStream = null;
    }

    /**
     * 向服务发送连接消息
     */
    private void conn() {
        org.json.JSONObject data = new org.json.JSONObject();
        try {
            data.put("uid", AppConfig.getInstance().getUid());
            data.put("token", AppConfig.getInstance().getToken());
            data.put("liveuid", mLiveUid);
            data.put("roomnum", mLiveUid);
            data.put("stream", mStream);
            mSocket.emit("conn", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnect-->" + args);
            conn();
        }
    };

    private Emitter.Listener mReConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--reConnect-->" + args);
            //conn();
        }
    };

    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onDisconnect-->" + args);
            if (mSocketHandler != null) {
                mSocketHandler.sendEmptyMessage(Constants.SOCKET_WHAT_DISCONN);
            }
        }
    };
    private Emitter.Listener mErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectError-->" + args);
        }
    };

    private Emitter.Listener mTimeOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectTimeOut-->" + args);
        }
    };

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    String s = ((JSONArray) args[0]).getString(0);
                    L.e(TAG, "--onConn-->" + s);
                    Message msg = Message.obtain();
                    msg.what = Constants.SOCKET_WHAT_CONN;
                    msg.obj = s.equals("ok");
                    mSocketHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        Message msg = Message.obtain();
                        msg.what = Constants.SOCKET_WHAT_BROADCAST;
                        msg.obj = array.getString(i);
                        if (mSocketHandler != null) {
                            mSocketHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    public void send(SocketSendBean bean) {
        if (mSocket != null) {
            mSocket.emit(Constants.SOCKET_SEND, bean.create());
        }
    }

    private static class SocketHandler extends Handler {

        private SocketMessageListener mListener;
        private String mLiveUid;

        private Charset iso = StandardCharsets.ISO_8859_1;
        private Charset utf8 = StandardCharsets.UTF_8;
        private CharsetEncoder isoEncoder = iso.newEncoder();

        public SocketHandler(SocketMessageListener listener) {
            mListener = new WeakReference<>(listener).get();
        }

        public void setLiveUid(String liveUid) {
            mLiveUid = liveUid;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) {
                return;
            }
            switch (msg.what) {
                case Constants.SOCKET_WHAT_CONN:
                    mListener.onConnect((Boolean) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_BROADCAST:
                    processBroadcast((String) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_DISCONN:
                    mListener.onDisConnect();
                    break;
            }
        }

        private String convertToUTF8(String data) {
            try {
                if (isoEncoder.canEncode(data)) {
                    return utf8.decode(iso.encode(data)).toString();
                } else {
                    return data;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return data;
            }
        }

        private void processBroadcast(String socketMsg) {
            L.e("收到socket--->" + socketMsg);
            if (Constants.SOCKET_STOP_PLAY.equals(socketMsg)) {
                mListener.onSuperCloseLive();//超管关闭房间
                return;
            }
            SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);
            switch (map.getString("_method_")) {
                case Constants.SOCKET_SYSTEM://系统消息
                    systemChatMessage(map.getString("ct"));
                    break;
                case Constants.SOCKET_KICK://踢人
                    systemChatMessage(map.getString("ct"));
                    mListener.onKick(map.getString("touid"));
                    break;
                case Constants.SOCKET_SHUT_UP://禁言
                    if (map.getString("msgtype").equals("4") && map.getString("touid").equals(AppConfig.getInstance().getUid()) && AppConfig.hideShutUpForAudience()) {

                    } else {
                        String ct = map.getString("ct");
                        systemChatMessage(ct);
                        mListener.onShutUp(map.getString("touid"), ct);
                    }
                    break;
                case Constants.SOCKET_SEND_MSG://文字消息，点亮，用户进房间，这种混乱的设计是因为服务器端逻辑就是这样设计的,客户端无法自行修改
                    String msgtype = map.getString("msgtype");
                    if ("2".equals(msgtype)) {//发言，点亮
                        if ("409002".equals(received.getRetcode())) {
                            ToastUtil.show(R.string.live_you_are_shut);
                            return;
                        }
                        LiveChatBean chatBean = new LiveChatBean();
                        chatBean.setId(map.getString("uid"));
                        chatBean.setUserNiceName(convertToUTF8(map.getString("uname")));
                        chatBean.setLevel(map.getIntValue("level"));
                        chatBean.setAnchor(map.getIntValue("isAnchor") == 1);
                        chatBean.setManager(map.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                        chatBean.setContent(convertToUTF8(map.getString("ct")));
                        int heart = map.getIntValue("heart");
                        chatBean.setHeart(heart);
                        if (heart > 0) {
                            chatBean.setType(LiveChatBean.LIGHT);
                        }
                        chatBean.setLiangName(map.getString("liangname"));
                        chatBean.setVipType(map.getIntValue("vip_type"));
                        chatBean.setGuardType(map.getIntValue("guard_type"));
                        mListener.onChat(chatBean);
                    } else if ("0".equals(msgtype)) {//用户进入房间
                        JSONObject obj = JSON.parseObject(map.getString("ct"));
                        LiveUserGiftBean u = JSON.toJavaObject(obj, LiveUserGiftBean.class);
                        UserBean.Vip vip = new UserBean.Vip();
                        int vipType = obj.getIntValue("vip_type");
                        vip.setType(vipType);
                        u.setVip(vip);
                        UserBean.Car car = new UserBean.Car();
                        car.setId(obj.getIntValue("car_id"));
                        car.setSwf(obj.getString("car_swf"));
                        car.setSwftime(obj.getFloatValue("car_swftime"));
                        car.setWords(obj.getString("car_words"));
                        u.setCar(car);
                        UserBean.Liang liang = new UserBean.Liang();
                        String liangName = obj.getString("liangname");
                        liang.setName(liangName);
                        u.setLiang(liang);
                        LiveChatBean chatBean = new LiveChatBean();
                        chatBean.setType(LiveChatBean.ENTER_ROOM);
                        chatBean.setId(u.getId());
                        chatBean.setUserNiceName(u.getUserNiceName());
                        chatBean.setLevel(u.getLevel());
                        chatBean.setVipType(vipType);
                        chatBean.setLiangName(liangName);
                        chatBean.setManager(obj.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                        chatBean.setContent(WordUtil.getString(R.string.live_enter_room));
                        chatBean.setGuardType(obj.getIntValue("guard_type"));
                        mListener.onEnterRoom(new LiveEnterRoomBean(u, chatBean));
                    }
                    break;
                case Constants.SOCKET_LIGHT://飘心
                    mListener.onLight();
                    break;
                case Constants.SOCKET_SEND_GIFT://送礼物
                    LiveReceiveGiftBean receiveGiftBean = JSON.parseObject(map.getString("ct"), LiveReceiveGiftBean.class);
                    receiveGiftBean.setAvatar(map.getString("uhead"));
                    receiveGiftBean.setUserNiceName(map.getString("uname"));
                    LiveChatBean chatBean = new LiveChatBean();
                    chatBean.setUserNiceName(receiveGiftBean.getUserNiceName());
                    chatBean.setLevel(receiveGiftBean.getLevel());
                    chatBean.setId(map.getString("uid"));
                    chatBean.setLiangName(map.getString("liangname"));
                    chatBean.setVipType(map.getIntValue("vip_type"));
                    chatBean.setType(LiveChatBean.GIFT);
                    chatBean.setContent(receiveGiftBean.getGiftCount() + WordUtil.getString(R.string.live_send_gift_2) + receiveGiftBean.getGiftName());
                    receiveGiftBean.setLiveChatBean(chatBean);
                    if (map.getIntValue("ifpk") == 1) {
                        if (!TextUtils.isEmpty(mLiveUid)) {
                            if (mLiveUid.equals(map.getString("roomnum"))) {
                                mListener.onSendGift(receiveGiftBean);
                                mListener.onSendGiftPk(map.getLongValue("pktotal1"), map.getLongValue("pktotal2"));
                            } else {
                                mListener.onSendGiftPk(map.getLongValue("pktotal2"), map.getLongValue("pktotal1"));
                            }
                        }
                    } else {
                        mListener.onSendGift(receiveGiftBean);
                    }

                    break;
                case Constants.SOCKET_SEND_BARRAGE://发弹幕
                    LiveDanMuBean liveDanMuBean = JSON.parseObject(map.getString("ct"), LiveDanMuBean.class);
                    liveDanMuBean.setAvatar(map.getString("uhead"));
                    liveDanMuBean.setUserNiceName(map.getString("uname"));
                    mListener.onSendDanMu(liveDanMuBean);
                    break;
                case Constants.SOCKET_LEAVE_ROOM://离开房间
                    UserBean u = JSON.parseObject(map.getString("ct"), UserBean.class);
                    mListener.onLeaveRoom(u);
                    break;
                case Constants.SOCKET_LIVE_END://主播关闭直播
                    mListener.onLiveEnd();
                    break;
                case Constants.SOCKET_CHANGE_LIVE://主播切换计时收费类型
                    mListener.onChangeTimeCharge(map.getIntValue("type_val"));
                    break;
                case Constants.SOCKET_UPDATE_VOTES:
                    mListener.onUpdateVotes(map.getString("uid"), map.getString("votes"), map.getIntValue("isfirst"));
                    break;
                case Constants.SOCKET_FAKE_FANS:
                    JSONObject obj = map.getJSONObject("ct");
                    String s = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getString("list");
                    int nums = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getIntValue("nums");
                    L.e("僵尸粉--->" + s);
                    List<LiveUserGiftBean> list = JSON.parseArray(s, LiveUserGiftBean.class);
                    mListener.addFakeFans(list, nums);
                    break;
                case Constants.SOCKET_SET_ADMIN://设置或取消管理员
                    systemChatMessage(map.getString("ct"));
                    mListener.onSetAdmin(map.getString("touid"), map.getIntValue("action"));
                    break;
                case Constants.SOCKET_BUY_GUARD://购买守护
                    LiveBuyGuardMsgBean buyGuardMsgBean = new LiveBuyGuardMsgBean();
                    buyGuardMsgBean.setUid(map.getString("uid"));
                    buyGuardMsgBean.setUserName(map.getString("uname"));
                    buyGuardMsgBean.setVotes(map.getString("votestotal"));
                    buyGuardMsgBean.setGuardNum(map.getIntValue("guard_nums"));
                    buyGuardMsgBean.setGuardType(map.getIntValue("guard_type"));
                    mListener.onBuyGuard(buyGuardMsgBean);
                    break;
                case Constants.SOCKET_LINK_MIC://连麦
                    processLinkMic(map);
                    break;
                case Constants.SOCKET_LINK_MIC_ANCHOR://主播连麦
                    processLinkMicAnchor(map);
                    break;
                case Constants.SOCKET_LINK_MIC_PK://主播PK
                    processAnchorLinkMicPk(map);
                    break;
                case Constants.SOCKET_RED_PACK://红包消息
                    String uid = map.getString("uid");
                    if (TextUtils.isEmpty(uid)) {
                        return;
                    }
                    LiveChatBean liveChatBean = new LiveChatBean();
                    liveChatBean.setType(LiveChatBean.RED_PACK);
                    liveChatBean.setId(uid);
                    String name = uid.equals(mLiveUid) ? WordUtil.getString(R.string.live_anchor) : map.getString("uname");
                    liveChatBean.setContent(name + map.getString("ct"));
                    mListener.onRedPack(liveChatBean);
                    break;

                //游戏socket
                case Constants.SOCKET_GAME_ZJH://游戏 智勇三张
                    if (AppConfig.GAME_ENABLE) {
                        mListener.onGameZjh(map);
                    }
                    break;
                case Constants.SOCKET_GAME_HD://游戏 海盗船长
                    if (AppConfig.GAME_ENABLE) {
                        mListener.onGameHd(map);
                    }
                    break;
                case Constants.SOCKET_GAME_ZP://游戏 幸运转盘
                    if (AppConfig.GAME_ENABLE) {
                        mListener.onGameZp(map);
                    }
                    break;
                case Constants.SOCKET_GAME_NZ://游戏 开心牛仔
                    if (AppConfig.GAME_ENABLE) {
                        mListener.onGameNz(map);
                    }
                    break;
                case Constants.SOCKET_GAME_EBB://游戏 二八贝
                    if (AppConfig.GAME_ENABLE) {
                        mListener.onGameEbb(map);
                    }
                    break;
            }
        }


        /**
         * 接收到系统消息，显示在聊天栏中
         */
        private void systemChatMessage(String content) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(content);
            bean.setType(LiveChatBean.SYSTEM);
            mListener.onChat(bean);
        }

        /**
         * 处理观众与主播连麦逻辑
         */
        private void processLinkMic(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://主播收到观众连麦的申请
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(convertToUTF8(map.getString("uname")));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    mListener.onAudienceApplyLinkMic(u);
                    break;
                case 2://观众收到主播同意连麦的消息
                    if (map.getString("touid").equals(AppConfig.getInstance().getUid())) {
                        mListener.onAnchorAcceptLinkMic();
                    }
                    break;
                case 3://观众收到主播拒绝连麦的消息
                    if (map.getString("touid").equals(AppConfig.getInstance().getUid())) {
                        mListener.onAnchorRefuseLinkMic();
                    }
                    break;
                case 4://所有人收到连麦观众发过来的流地址
                    String uid = map.getString("uid");
                    if (!TextUtils.isEmpty(uid) && !uid.equals(AppConfig.getInstance().getUid())) {
                        mListener.onAudienceSendLinkMicUrl(uid, map.getString("uname"), map.getString("playurl"));
                    }
                    break;
                case 5://连麦观众自己断开连麦
                    mListener.onAudienceCloseLinkMic(map.getString("uid"), map.getString("uname"));
                    break;
                case 6://主播断开已连麦观众的连麦
                    mListener.onAnchorCloseLinkMic(map.getString("touid"), map.getString("uname"));
                    break;
                case 7://已申请连麦的观众收到主播繁忙的消息
                    if (map.getString("touid").equals(AppConfig.getInstance().getUid())) {
                        mListener.onAnchorBusy();
                    }
                    break;
                case 8://已申请连麦的观众收到主播无响应的消息
                    if (map.getString("touid").equals(AppConfig.getInstance().getUid())) {
                        mListener.onAnchorNotResponse();
                    }
                    break;
                case 9://所有人收到已连麦的观众退出直播间消息
                    mListener.onAudienceLinkMicExitRoom(map.getString("touid"));
                    break;
            }
        }

        /**
         * 处理主播与主播连麦逻辑
         *
         * @param map
         */
        private void processLinkMicAnchor(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://收到其他主播连麦的邀请的回调
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(convertToUTF8(map.getString("uname")));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicAnchorApply(u, map.getString("stream"));
                    break;
                case 3://对方主播拒绝连麦的回调
                    mListener.onLinkMicAnchorRefuse();
                    break;
                case 4://所有人收到对方主播的播流地址的回调
                    mListener.onLinkMicAnchorPlayUrl(map.getString("pkuid"), map.getString("pkpull"));
                    break;
                case 5://断开连麦的回调
                    mListener.onLinkMicAnchorClose();
                    break;
                case 7://对方主播正在忙的回调
                    mListener.onLinkMicAnchorBusy();
                    break;
                case 8://对方主播无响应的回调
                    mListener.onLinkMicAnchorNotResponse();
                    break;
            }
        }

        /**
         * 处理主播与主播PK逻辑
         *
         * @param map
         */
        private void processAnchorLinkMicPk(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://收到对方主播PK回调
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(convertToUTF8(map.getString("uname")));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicPkApply(u, map.getString("stream"));
                    break;
                case 3://对方主播拒绝PK的回调
                    mListener.onLinkMicPkRefuse();
                    break;
                case 4://所有人收到PK开始址的回调
                    mListener.onLinkMicPkStart(map.getString("pkuid"));
                    break;
                case 5://PK时候断开连麦的回调
                    mListener.onLinkMicPkClose();
                    break;
                case 7://对方主播正在忙的回调
                    mListener.onLinkMicPkBusy();
                    break;
                case 8://对方主播无响应的回调
                    mListener.onLinkMicPkNotResponse();
                    break;
                case 9://pk结束的回调
                    mListener.onLinkMicPkEnd(map.getString("win_uid"));
                    break;
            }
        }

        public void release() {
            mListener = null;
        }
    }
}
