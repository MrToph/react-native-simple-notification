package io.cmichel.simplenotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.facebook.react.bridge.ReadableMap;

/**
 * Created by Christoph on 06-Jan-17.
 */
public class NotificationBuilder {
    private static NotificationBuilder instance;
    private Uri soundUri = null;
    Context context;
    NotificationManager nm;
    NotificationCompat.Builder builder;
    ReadableMap updateOptions;

    public static NotificationBuilder getInstance() {
        if(instance == null) {
            instance = new NotificationBuilder();
        }
        return instance;
    }

    public void init(Context context, NotificationManager nm, Uri soundUri){
        this.context = context;
        this.nm = nm;
        this.soundUri = soundUri;
    }

    public void show(ReadableMap options) {
        builder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false);

        // create open intent
        String packageName = context.getApplicationContext().getPackageName();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        applyOptions(builder, options);

        nm.notify(Module.id, builder.build());
    }

    private void applyOptions(NotificationCompat.Builder builder, ReadableMap options) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName()));
        builder.setLargeIcon(bm);
        if(options.hasKey("text")) builder.setContentText(options.getString("text"));
        if(options.hasKey("title")) builder.setContentTitle(options.getString("title"));
        if(options.hasKey("when")) builder.setWhen((long)options.getDouble("when"));
        if(options.hasKey("smallIcon")) builder.setSmallIcon(context.getResources().getIdentifier(options.getString("smallIcon"), "mipmap", context.getPackageName()));
        if(options.hasKey("vibrate") && options.getBoolean("vibrate")) builder.setVibrate(new long[] {0, 1000, 1000, 1000, 1000});
        if(options.hasKey("lights") && options.getBoolean("lights")) builder.setLights(Color.RED, 1000, 1000);
        if(options.hasKey("sound") && options.getBoolean("sound") && soundUri != null) builder.setSound(soundUri);
    }

    public void update() {
        // If this was invoked through AlarmReceiver.OnReceive() and the app was closed, NotificationBuilder will not be initialized
        // onReceiver.NB.getInstance() will return a new instance will all null values
        if(builder == null || nm == null) return;
//        Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();
        if(updateOptions != null) applyOptions(builder, updateOptions);
        builder.setOngoing(false);
        nm.notify(Module.id, builder.build());
    }

    public void setUpdateOptions(ReadableMap options) {
        updateOptions = options;
    }

    public void clear() {
        nm.cancel(Module.id);
    }
}
