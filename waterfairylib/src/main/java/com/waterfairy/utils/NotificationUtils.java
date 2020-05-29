package com.waterfairy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.RequiresApi;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/1/25
 * @Description: 注意:smallIcon 设置为图片文件  , 26之后会使用xml文件  报错
 */

public class NotificationUtils extends ContextWrapper {
    private NotificationManager manager;
    public static final String channelId = "channel_1";
    public static final String name = "channel_name_1";

    public NotificationUtils(Context context) {
        super(context);
    }

    /**
     * 获取通知管理类
     *
     * @return NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * 发送通知
     *
     * @param smallIcon
     * @param title
     * @param content
     */
    public Notification.Builder getNotificationBuilder(int smallIcon, String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            return getChannelNotificationBuilder
                    (smallIcon, title, content);
        } else {
            return getNotificationBuilderNormal(smallIcon, title, content);

        }
    }

    public void sendNotification(Notification notification) {
        getManager().notify(1, notification);
    }

    public void sendNotification(int smallIcon, String title, String content) {
        Notification.Builder notificationBuilder = getNotificationBuilder(smallIcon, title, content);
        sendNotification(notificationBuilder.build());
    }

    /**
     * 8.0及以上需要创建channel
     * 创建channel
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    /**
     * 通知之前调用 createNotificationChannel
     * 获取channel(26及以上) notification.builder
     *
     * @param smallIcon
     * @param title
     * @param content
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotificationBuilder(int smallIcon, String title, String content) {
        return new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);
    }

    /**
     * 获取channel(26以下) notification.builder
     *
     * @param smallIcon
     * @param title
     * @param content
     * @return
     */
    public Notification.Builder getNotificationBuilderNormal(int smallIcon, String title, String content) {
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);
    }
}
