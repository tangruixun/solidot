package com.trx.solidot;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CheckIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String INTERVAL_TIMER_KEY = "INTERVAL_TIMER_KEY";
    public static final String LIST_KEY = "LIST_KEY";

    private static final String TAG = "--->";
    private int intervalTIme = 0;
    private SharedPreferences sharedPreferences;
    private int mId = 0;

    public CheckIntentService() {
        super("CheckIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Intent是从Activity发过来的，携带识别参数，根据参数不同执行不同的任务
        Log.i("--->", "onHandleIntent");

        ArrayList <RSSItem> list = new ArrayList<>();
        ArrayList <RSSItem> latestList = new ArrayList<>();

        if (intent != null) {
            intervalTIme = intent.getIntExtra(INTERVAL_TIMER_KEY, 360);
            list = intent.getParcelableArrayListExtra(LIST_KEY);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bAlterFeed = sharedPreferences.getBoolean(getString(R.string.pref_feed_select_key), false);
        String strURL;
        if (bAlterFeed) {
            strURL = getString(R.string.rss2url);
        } else {
            strURL = getString(R.string.rssurl);
        }

        try {
            URL url = new URL(strURL); //呼叫網址進來
            SAXParserFactory spf = SAXParserFactory.newInstance();//先蓋一個工廠
            SAXParser sp = spf.newSAXParser();//工廠有一個知識不太高的解析工人
            XMLReader xr = sp.getXMLReader();//也有一個閱讀工人
            RSSHandler myHandler = new RSSHandler(this);//用到了我們之後建立的分配工人
            xr.setContentHandler(myHandler);//將閱讀工人和分配工人做結合
            xr.parse(new InputSource(url.openStream()));//閱讀工人用parse去開啟一個InputStream放資料
            latestList = myHandler.getParsedData();//getParsedData()方法會在myHandler裡看到
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        //RSSItem test = new RSSItem("test", "test", "test", "test", "test", "test", "test", "test");
        //latestList.add(0, test);

        boolean bDiff = compareList (latestList, list);
        if (bDiff) {
            showTray();
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int millsec = intervalTIme * 60 * 1000;
        //int millsec = intervalTIme * 1000;
        //int millsec = 1000;

        long triggerAtTime = SystemClock.elapsedRealtime() + millsec;

        Intent broadcastIntent = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
        b.putInt(INTERVAL_TIMER_KEY, intervalTIme);
        broadcastIntent.putExtras(b);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        DateFormat format = DateFormat.getTimeInstance();
        Log.i(TAG, "onHandleIntent完成:" + format.format(new Date()));

//        //Step 1：获得Service:
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        //Step 2：通过set方法设置定时任务
//        int triggerInterval = 2 * 1000;
//        long triggerAtTime = SystemClock.elapsedRealtime() + triggerInterval;
//        alarmManager.set(AlarmManager.RTC_WAKEUP,triggerAtTime,pendingIntent);
//        //参数详解：
//        //set(int type,long startTime,PendingIntent pi)
//        //①type:
//        //有五个可选值:
//        //AlarmManager.ELAPSED_REALTIME:
//        //闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3;
//        //AlarmManager.ELAPSED_REALTIME_WAKEUP
//        //闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
//        //AlarmManager.RTC
//        //闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
//        //AlarmManager.RTC_WAKEUP
//        //表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0;
//        //AlarmManager.POWER_OFF_WAKEUP
//        //表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，
//        //该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
//        //PS:第一个参数决定第二个参数的类型,如果是REALTIME的话就用：
//        //SystemClock.elapsedRealtime( )方法可以获得系统开机到现在经历的毫秒数
//        //如果是RTC的就用:System.currentTimeMillis()可获得从1970.1.1 0点到    现在做经历的毫秒数
//        //②startTime：
//        //闹钟的第一次执行时间，以毫秒为单位，可以自定义时间，不过一般使用当前时间。
//        //需要注意的是,本属性与第一个属性（type）密切相关,如果第一个参数对应的闹钟
//        //使用的是相对时间（ELAPSED_REALTIME和ELAPSED_REALTIME_WAKEUP），那么本属
//        //性就得使用相对时间（相对于系统启动时间来说）,比如当前时间就表示为:
//        //SystemClock.elapsedRealtime()；如果第一个参数对应的闹钟使用的是绝对时间 (RTC、RTC_WAKEUP、POWER_OFF_WAKEUP）,
//        //那么本属性就得使用绝对时间，
//        //比如当前时间就表示为：System.currentTimeMillis()。
//        //③PendingIntent:
//        //绑定了闹钟的执行动作，比如发送一个广播、给出提示等等。PendingIntent
//        //是Intent的封装类。需要注意的是，如果是通过启动服务来实现闹钟提示的话，
//        //PendingIntent对象的获取就应该采用Pending.getService
//        //        (Context c,int i,Intent intent,int j)方法；如果是通过广播来实现闹钟提示的话，
//        //PendingIntent对象的获取就应该采用 PendingIntent.getBroadcast
//        //        (Context c,int i,Intent intent,int j)方法；
//        //如果是采用Activity的方式来实现闹钟提示的话，PendingIntent对象的获取
//        //就应该采用 PendingIntent.getActivity(Context c,int i,Intent intent,int j)
//        //方法。如果这三种方法错用了的话，虽然不会报错，但是看不到闹钟提示效果。
//        //另外:
//        //从4.4版本后(API 19),Alarm任务的触发时间可能变得不准确,有可能会延时,是系统
//        //对于耗电性的优化,如果需要准确无误可以调用setExtra()方法~
//
//        //Step 3：定义一个Service
//        // 在onStartCommand中开辟一条事务线程,用于处理一些定时逻辑
//        //Step 4：定义一个Broadcast(广播)，用于启动Service
//        // 最后别忘了，在AndroidManifest.xml中对这Service与Boradcast进行注册！
    }

    private boolean compareList(ArrayList<RSSItem> latestList, ArrayList<RSSItem> list) {
        String regex = sharedPreferences.getString(getString(R.string.keywords_key), ".*");
        if (latestList != null && list != null) {
            if (!latestList.isEmpty() && !list.isEmpty()) {
                if (!latestList.get(0).getLink().equals(list.get(0).getLink()) || (latestList.size () != list.size ())) {
                    // has change
                    if (regex.equals(".*")) {
                        return true;
                    } else {
                        for (RSSItem item : latestList) {
                            Pattern p = Pattern.compile(regex);
                            Matcher m = p.matcher(item.getDescription());
                            if (m.matches()) {
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    private void showTray() {

        boolean bNotification = sharedPreferences.getBoolean(getString(R.string.notifications_new_message_key), true);
        boolean bVibration = sharedPreferences.getBoolean(getString(R.string.notifications_new_message_vibrate_key), true);
        String alarms = sharedPreferences.getString(getString(R.string.notifications_new_message_ringtone_key), "default ringtone");

        if (bNotification) {
            Uri uri = Uri.parse(alarms);
            playSound(uri);
            if (bVibration) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.smallnotifybarsolidot)
                        .setContentTitle(getString (R.string.app_name))
                        .setContentText(getString (R.string.notification));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
        mId++;
    }

    private void playSound(Uri uri) {

        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset ();
                    mediaPlayer.release ();
                }
            });
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        Log.i("--->", "handleActionFoo");
//
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        Log.i("--->", "handleActionBaz");
//
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    // 这里还重写了onDestroy，记录日志用于观察Service何时销毁
    @Override
    public void onDestroy() {
        Log.i("--->", "onDestroy");
        super.onDestroy();
    }

}
