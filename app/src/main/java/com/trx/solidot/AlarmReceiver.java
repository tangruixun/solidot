package com.trx.solidot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by TRX on 07/06/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String freq = sharedPreferences.getString(context.getString(R.string.sync_frequency_key), "360");
//        int intervalTIme = Integer.valueOf(freq);
//        int millsec = intervalTIme * 60 * 1000;
//        //int millsec = intervalTIme * 1000;
//        //int millsec = 1000;
//        long triggerAtTime = SystemClock.elapsedRealtime() + millsec;
//
//        Intent serviceIntent = new Intent(context, CheckIntentService.class);
//        Bundle serviceBundle = new Bundle ();
//        serviceBundle.putParcelableArrayList(CheckIntentService.LIST_KEY, itemsList);
//        serviceIntent.putExtras(serviceBundle);
//
//        PendingIntent pintent = PendingIntent.getService(context, 0, serviceIntent, 0);
//        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, millsec, pintent);
//
//        Intent i = new Intent("com.trx.solidot.CheckIntentService");
//        i.setClass(context, CheckIntentService.class);
//        context.startService(i);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Register your reporting alarms here.
            Log.i("--->", "收到告白啦~");
            Intent startServiceIntent = new Intent(context, CheckIntentService.class);
            startWakefulService(context, startServiceIntent);
        }
    }
}
