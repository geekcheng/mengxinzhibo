package com.tongchuang.phonelive.socket;

import com.tongchuang.phonelive.AppConfig;
import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.UserBean;
import com.tongchuang.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/10/9.
 * 直播间发言
 */

public class SocketChatUtil {

    /**
     * 发言
     */
    public static void sendChatMessage(SocketClient client, String content, boolean isAnchor, int userType, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("usertype", userType)
                .param("isAnchor", isAnchor ? 1 : 0)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("guard_type", guardType)
                .param("ct", content));
    }

    /**
     * 点亮
     */
    public static void sendLightMessage(SocketClient client, int heart, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("heart", heart)
                .param("guard_type", guardType)
                .param("ct", WordUtil.getString(R.string.live_lighted)));

    }

    /**
     * 发送飘心消息
     */
    public static void sendFloatHeart(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LIGHT)
                .param("action", 2)
                .param("msgtype", 0)
                .param("ct", ""));
    }

    /**
     * 发送弹幕消息
     */
    public static void sendDanmuMessage(SocketClient client, String danmuToken) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_BARRAGE)
                .param("action", 7)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("ct", danmuToken));
    }


    /**
     * 发送礼物消息
     */
    public static void sendGiftMessage(SocketClient client, int giftType, String giftToken, String liveUid) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_GIFT)
                .param("action", 0)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("evensend", giftType)
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("ct", giftToken)
                .param("roomnum", liveUid));
    }


    /**
     * 主播或管理员 踢人
     */
    public static void sendKickMessage(SocketClient client, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_KICK)
                .param("action", 2)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + WordUtil.getString(R.string.live_kicked)));
    }


    /**
     * 主播或管理员 禁言
     */
    public static void sendShutUpMessage(SocketClient client, String toUid, String toName, String shutTime) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SHUT_UP)
                .param("action", 1)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + WordUtil.getString(R.string.live_shut) + shutTime));
    }

    /**
     * 解除禁言
     */
    public static void sendCancelShutUpMessage(SocketClient client, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SHUT_UP)
                .param("action", 1)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + WordUtil.getString(R.string.live_shut_cancel)));
    }

    /**
     * 设置或取消管理员消息
     */
    public static void sendSetAdminMessage(SocketClient client, int action, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        String s = action == 1 ? WordUtil.getString(R.string.live_set_admin) : WordUtil.getString(R.string.live_set_admin_cancel);
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SET_ADMIN)
                .param("action", action)
                .param("msgtype", 1)
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + " " + s));
    }

    /**
     * 超管关闭直播间
     */
    public static void superCloseRoom(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_STOP_LIVE)
                .param("action", 19)
                .param("msgtype", 1)
                .param("ct", ""));
    }

    /**
     * 发系统消息
     */
    public static void sendSystemMessage(SocketClient client, String content) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SYSTEM)
                .param("action", 13)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("ct", content));
    }


    /**
     * 获取僵尸粉
     */
    public static void getFakeFans(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_FAKE_FANS)
                .param("action", "")
                .param("msgtype", ""));
    }


    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes, int first) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_UPDATE_VOTES)
                .param("action", 1)
                .param("msgtype", 26)
                .param("votes", votes)
                .param("uid", AppConfig.getInstance().getUid())
                .param("isfirst", first)
                .param("ct", ""));
    }

    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes) {
        sendUpdateVotesMessage(client, votes, 0);
    }

    /**
     * 发送购买守护成功消息
     */
    public static void sendBuyGuardMessage(SocketClient client, String votes, int guardNum, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_BUY_GUARD)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("uhead", u.getAvatar())
                .param("votestotal", votes)
                .param("guard_nums", guardNum)
                .param("guard_type", guardType));
    }

    /**
     * 发送发红包成功消息
     */
    public static void sendRedPackMessage(SocketClient client) {
        if (client == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_RED_PACK)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("ct", WordUtil.getString(R.string.red_pack_22))
        );

    }

}
