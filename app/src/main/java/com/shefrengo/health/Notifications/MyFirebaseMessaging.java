package com.shefrengo.health.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shefrengo.health.Activities.MessagesActivity;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessaging";


    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    private void updateToken(String refreshToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Token");
        Token token = new Token(refreshToken);
        assert user != null;
        collectionReference.document(user.getUid()).set(token, SetOptions.merge())
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String sented = remoteMessage.getData().get("body");


        Log.d(TAG, "onMessageReceived: " + sented);
        /**FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

         if (user != null) {


         if (sented.equals(user.getUid())) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         sendOreoNotification(remoteMessage);
         } else {
         sendMessage(remoteMessage);
         }

         }
         }**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoNotification(remoteMessage);
        } else {
            sendMessage(remoteMessage);
        }
    }

    @Override
    public void onMessageSent(@NonNull @NotNull String s) {
        super.onMessageSent(s);

        Log.d(TAG, "onMessageSent: message sent " + s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        assert user != null;
        //  int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        int j = 1;
        Intent intent = new Intent(this, MessagesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", "");
        intent.putExtras(bundle);
        int icondrawable = R.drawable.icc;

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification("Health App", "You have a new Message", pendingIntent, defaultSound, "1");
        int i = 0;
        if (j > 0) {
            i = j;
        }
        oreoNotification.getNotificationManager().notify(i, builder.build());

    }

    private void sendMessage(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessagesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", "");
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert icon != null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icc)
                .setContentTitle("Message")
                .setContentText("You have a new Message")
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService
                (Context.NOTIFICATION_SERVICE);
        int i = 0;
        if (j > 0) {
            i = j;
        }
        notificationManager.notify(i, builder.build());


    }
}
