package com.trx.solidot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SolidotListFragment.OnTitleSelectedListener,
        ArticleFragment.OnArticleFragmentInteractionListener {

    private boolean viewIsAtHome;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSSItem tempItem = new RSSItem (getString(R.string.submit), getString(R.string.submiturl), "", "", "", "", "", "");
                String strLink = tempItem.getLink();
                String strTitle = tempItem.getTitle();

                // The user selected the headline of an article from the HeadlinesFragment
                // Do something here to display that article

                ArticleFragment articleFrag = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);

                if (articleFrag != null) {
                    // If article frag is available, we're in two-pane layout...
                    // Call a method in the ArticleFragment to update its content
                    articleFrag.updateArticleView(strLink);
                } else {
                    // Otherwise, we're in the one-pane layout and must swap frags...
                    // Create fragment and give it an argument for the selected article
                    ArticleFragment newFragment = new ArticleFragment();
                    Bundle args = new Bundle();
                    args.putString(ArticleFragment.ARG_LINK, strLink);
                    args.putString(ArticleFragment.ARG_TITLE, strTitle);
                    newFragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.content_frame, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_home);

        /////////////////////////////////////////////////////////////////////////////////////

        //Step 1：获得Service:
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Step 2：通过set方法设置定时任务
        int anHour = 2 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        manager.set(AlarmManager.RTC_WAKEUP,triggerAtTime,pendingIntent);
        //参数详解：
        //set(int type,long startTime,PendingIntent pi)
        //①type:
        //有五个可选值:
        //AlarmManager.ELAPSED_REALTIME:
        //闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3;
        //AlarmManager.ELAPSED_REALTIME_WAKEUP
        //闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
        //AlarmManager.RTC
        //闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
        //AlarmManager.RTC_WAKEUP
        //表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0;
        //AlarmManager.POWER_OFF_WAKEUP
        //表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，
        //该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
        //PS:第一个参数决定第二个参数的类型,如果是REALTIME的话就用：
        //SystemClock.elapsedRealtime( )方法可以获得系统开机到现在经历的毫秒数
        //如果是RTC的就用:System.currentTimeMillis()可获得从1970.1.1 0点到    现在做经历的毫秒数
        //②startTime：
        //闹钟的第一次执行时间，以毫秒为单位，可以自定义时间，不过一般使用当前时间。
        //需要注意的是,本属性与第一个属性（type）密切相关,如果第一个参数对应的闹钟
        //使用的是相对时间（ELAPSED_REALTIME和ELAPSED_REALTIME_WAKEUP），那么本属
        //性就得使用相对时间（相对于系统启动时间来说）,比如当前时间就表示为:
        //SystemClock.elapsedRealtime()；如果第一个参数对应的闹钟使用的是绝对时间 (RTC、RTC_WAKEUP、POWER_OFF_WAKEUP）,
        //那么本属性就得使用绝对时间，
        //比如当前时间就表示为：System.currentTimeMillis()。
        //③PendingIntent:
        //绑定了闹钟的执行动作，比如发送一个广播、给出提示等等。PendingIntent
        //是Intent的封装类。需要注意的是，如果是通过启动服务来实现闹钟提示的话，
        //PendingIntent对象的获取就应该采用Pending.getService
        //        (Context c,int i,Intent intent,int j)方法；如果是通过广播来实现闹钟提示的话，
        //PendingIntent对象的获取就应该采用 PendingIntent.getBroadcast
        //        (Context c,int i,Intent intent,int j)方法；
        //如果是采用Activity的方式来实现闹钟提示的话，PendingIntent对象的获取
        //就应该采用 PendingIntent.getActivity(Context c,int i,Intent intent,int j)
        //方法。如果这三种方法错用了的话，虽然不会报错，但是看不到闹钟提示效果。
        //另外:
        //从4.4版本后(API 19),Alarm任务的触发时间可能变得不准确,有可能会延时,是系统
        //对于耗电性的优化,如果需要准确无误可以调用setExtra()方法~

        //Step 3：定义一个Service
        // 在onStartCommand中开辟一条事务线程,用于处理一些定时逻辑
        //Step 4：定义一个Broadcast(广播)，用于启动Service
        // 最后别忘了，在AndroidManifest.xml中对这Service与Boradcast进行注册！

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    public void displayView(int viewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = SolidotListFragment.newInstance();
                title = getString(R.string.menu_nav_home);
                viewIsAtHome = true;

                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

                // set the toolbar title
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
                break;

            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.nav_share_this_app:
                Intent shareIntent=new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_thisappsubject));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_thisapptext));
                startActivity(Intent.createChooser(shareIntent, getString (R.string.share_via)));
                break;

            case R.id.nav_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);
        return true;
    }

    @Override
    public void onArticleSelected(RSSItem item) {
        String strLink = item.getLink();
        String strTitle = item.getTitle();

        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article

        ArticleFragment articleFrag = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(strLink);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putString(ArticleFragment.ARG_LINK, strLink);
            args.putString(ArticleFragment.ARG_TITLE, strTitle);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.content_frame, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    public void onArticleFragmentInteraction(Uri uri) {

    }
}
