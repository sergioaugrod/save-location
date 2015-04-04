package br.com.sergioaugrod.savelocation.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import br.com.sergioaugrod.savelocation.R;

public class Notificacao {

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void criar(Context context, String tickerText, String title, String message, int icon, int id, Intent intent) {
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);
        Notification n = null;
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 11) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(tickerText);
            builder.setContentText(message);
            builder.setSmallIcon(icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            builder.setContentIntent(p);
            if (apiLevel >= 17) {
                n = builder.build();
            } else {
                n = builder.getNotification();
            }
        } else {
            n = new Notification(icon, tickerText, System.currentTimeMillis());
            n.setLatestEventInfo(context, title, message, p);
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        nm.notify(id, n);
    }

}
