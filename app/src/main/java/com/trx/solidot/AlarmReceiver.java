package com.trx.solidot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by TRX on 07/06/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int time = intent.getIntExtra(CheckIntentService.INTERVAL_TIMER_KEY, 360);
        Intent i = new Intent(context, CheckIntentService.class);
        i.putExtra(CheckIntentService.INTERVAL_TIMER_KEY, time);
        context.startService(i);
        Toast.makeText(context,"收到告白啦~", Toast.LENGTH_SHORT).show();
        Log.i("--->", "收到告白啦~");
    }
}
