package io.cmichel.simplenotification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

public class Module extends ReactContextBaseJavaModule {

  private final NotificationBuilder nb;
  static final int id = 1;

  public Module(ReactApplicationContext reactContext) {
    super(reactContext);
    nb = NotificationBuilder.getInstance();
    Uri soundUri = getNotificationSound(reactContext);
    nb.init(reactContext, (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE), soundUri);
  }

  public Uri getNotificationSound(Context context) {
    RingtoneManager manager = new RingtoneManager(context);
    manager.setType(RingtoneManager.TYPE_NOTIFICATION);
    Cursor cursor = manager.getCursor();

    Uri activeUri = null;
    while (cursor.moveToNext()) {
      String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
      Uri ringtoneURI = manager.getRingtoneUri(cursor.getPosition());
//      Log.e("RINGTONEDING", notificationTitle + " - URI: " + ringtoneURI);
      if(notificationTitle.equals("On The Hunt") && ringtoneURI != null) return ringtoneURI;
      activeUri = ringtoneURI;
    }

    return activeUri;
  }

  @Override
  public String getName() {
    return "SNotification";
  }

  @ReactMethod
  public void show(ReadableMap options) {
    nb.show(options);
    clearAlarm();
  }

  @ReactMethod
  public void scheduleUpdate(ReadableMap options, double delayInMillis) {
    long delayLong = (long)delayInMillis;   // React Bridge doesn't understand longs
    nb.setUpdateOptions(options);
    setAlarm(delayLong);
  }

  @ReactMethod
  public void clear() {
    nb.clear();
    clearAlarm();
  }

  @ReactMethod
  public final void clearAlarm() {
    PendingIntent pendingIntent = createPendingIntent(Integer.toString(id));
    getAlarmManager().cancel(pendingIntent);
  }

  private void setAlarm(long delayInMillis) {
    PendingIntent pendingIntent = createPendingIntent(Integer.toString(id));
    delayInMillis +=  System.currentTimeMillis();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
      getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delayInMillis, pendingIntent);
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
      getAlarmManager().setExact(AlarmManager.RTC_WAKEUP, delayInMillis, pendingIntent);
    else
      getAlarmManager().set(AlarmManager.RTC_WAKEUP, delayInMillis, pendingIntent);
  }

  private PendingIntent createPendingIntent(String id) {
    Context context = getReactApplicationContext();
    // create the pending intent
    Intent intent = new Intent(context, AlarmReceiver.class);
    // set unique alarm ID to identify it. Used for clearing and seeing which one fired
    // public boolean filterEquals(Intent other) compare the action, data, type, package, component, and categories, but do not compare the extra
    intent.setData(Uri.parse("id://" + id));
    intent.setAction("UPDATE_NOTIFICATION");
    return PendingIntent.getBroadcast(context, 0, intent, 0);
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) getReactApplicationContext().getSystemService(Context.ALARM_SERVICE);
  }
}