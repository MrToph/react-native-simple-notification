package io.cmichel.simplenotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Christoph on 06-Jan-17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("SIMPLE_NOTIFICATION", "ON_RECEIVE");
        NotificationBuilder.getInstance().update();
    }
}
