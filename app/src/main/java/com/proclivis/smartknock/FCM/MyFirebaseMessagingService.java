package com.proclivis.smartknock.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.proclivis.smartknock.Activity.Member.MemberMainActivity;
import com.proclivis.smartknock.Activity.Member.MemberPagerActivity;
import com.proclivis.smartknock.Activity.Member.SettingMemberActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    public static final String BROADCAST_UPDATE_MEMBER_MAIN = "com.proclivis.smartknock.FCM";
    public static final String BROADCAST_UPDATE_USER = "com.proclivis.smartknock.FCM";
    public static final String BROADCAST_USER_PRO = "com.proclivis.smartknock.FCM";
    Intent intentMember , intentUser , intentPro;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {

            if (remoteMessage.getData() != null) {

                switch (remoteMessage.getData().get("type")) {
                    case "user":
                        Constant.UserNotifyId = Integer.parseInt(remoteMessage.getData().get("id"));
                        Constant.DRAWER_m_name = remoteMessage.getData().get("m_name");
                        Constant.DRAWER_mobile_no = remoteMessage.getData().get("mobile_no");
                        Constant.DRAWER_coming_from = remoteMessage.getData().get("coming_from");
                        Constant.DRAWER_purpose = remoteMessage.getData().get("purpose");
                        Constant.DRAWER_visitor_count = remoteMessage.getData().get("visitor_count");
                        Constant.DRAWER_date_time_in = remoteMessage.getData().get("date_time_in");
                        Constant.DRAWER_reason = remoteMessage.getData().get("reason");
                        Constant.DRAWER_status = remoteMessage.getData().get("status");
                        this.intentUser = new Intent(BROADCAST_UPDATE_USER);
                        sendBroadcast(intentUser);
                        break;

                    case "pro": {

                        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                        if (!Util.ReadSharePreference(getApplicationContext(), Constant.USER_ID).equals("")){
                            if (remoteMessage.getData().get("value").equalsIgnoreCase("pro")){
                                Util.WriteSharePreference(getApplicationContext() , Constant.USER_PRO , "yes");
                            } else {
                                Util.WriteSharePreference(getApplicationContext() , Constant.USER_PRO , "no");
                            }

                            this.intentPro = new Intent(BROADCAST_USER_PRO);
                            sendBroadcast(intentPro);

                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "")
                                    .setSmallIcon(R.drawable.ic_notification_logo_k_test)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentTitle("Smart Knock")
                                    .setContentText(remoteMessage.getData().get("msg"))
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri);

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            assert notificationManager != null;
                            notificationManager.notify(m, builder.build());
                        } else {

                            Intent intent = new Intent(MyFirebaseMessagingService.this, SettingMemberActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "")
                                    .setSmallIcon(R.drawable.ic_notification_logo_k_test)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentTitle("Smart Knock")
                                    .setContentText(remoteMessage.getData().get("msg"))
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                    .setNumber(5)
                                    .setSound(defaultSoundUri);

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            assert notificationManager != null;
                            notificationManager.notify(m, builder.build());
                        }


                        break;
                    }

                    default: {
                        this.intentMember = new Intent(BROADCAST_UPDATE_MEMBER_MAIN);
                        sendBroadcast(intentMember);

                        String title;
                        if (remoteMessage.getData().get("type").equals("VISITOR_IN")) {
                            title = "You have a new visitor";
                        } else {
                            title = "Your visitor has now left";
                        }

                        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                        Constant.NOTIFICATION_COUNT += 1 ;
                        Intent intent;
                        if (Constant.NOTIFICATION_COUNT==1){
                            intent = new Intent(MyFirebaseMessagingService.this, MemberPagerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("message", remoteMessage.getData().get("id"));
                            intent.putExtra("clicked", "no");
                            Constant.VISITOR_POS = remoteMessage.getData().get("id");
                        } else {
                            intent = new Intent(MyFirebaseMessagingService.this, MemberMainActivity.class);
                            intent.putExtra("clicked", "fromNotification");

//                            intent = new Intent(MyFirebaseMessagingService.this, MemberPagerActivity.class);
//                            intent.putExtra("message", remoteMessage.getData().get("id"));
//                            intent.putExtra("clicked", "no");
//                            Constant.VISITOR_POS = remoteMessage.getData().get("id");
                        }

                        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "")
                                .setSmallIcon(R.drawable.ic_notification_logo_k_test)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setContentTitle(title)
                                .setContentText(remoteMessage.getData().get("name"))
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                .setNumber(5)
                                .setSound(defaultSoundUri);





                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.notify(m, builder.build());
                        break;



                    }
                }

            }

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String body = data.getString("body");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "body: " + body);
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MemberMainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}