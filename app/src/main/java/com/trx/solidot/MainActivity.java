package com.trx.solidot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SolidotListFragment.OnTitleSelectedListener,
        ArticleFragment.OnArticleFragmentInteractionListener
        {

    private boolean viewIsAtHome;
    private AdView adView;
    private ArrayList<RSSItem> itemList;
    private ProgressBar pbr;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        pbr = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int num = getSupportFragmentManager().getBackStackEntryCount();
//
//            }
//        });

        itemList = new ArrayList<>();

        adView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

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
                    transaction.add(R.id.content_frame, newFragment, "submit");
                    transaction.addToBackStack("submit");

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_home);
        //getFragmentManager().addOnBackStackChangedListener(this);
        //shouldDisplayHomeUp();

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
                //fragment = getFragmentManager().findFragmentByTag(Constant.);
                fragment = SolidotListFragment.newInstance();
                title = getString(R.string.menu_nav_home);
                viewIsAtHome = true;

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace (R.id.content_frame, fragment);
                ft.commit();

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

        //ArticleFragment articleFrag = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);
        ArticleFragment articleFrag = ArticleFragment.newInstance(strLink, strTitle);

        // Otherwise, we're in the one-pane layout and must swap frags...
        // Create fragment and give it an argument for the selected article
        //            Bundle args = new Bundle();
        //            args.putString(ArticleFragment.ARG_LINK, strLink);
        //            args.putString(ArticleFragment.ARG_TITLE, strTitle);
        //            newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        // SolidotListFragment sf = SolidotListFragment.newInstance();
        transaction.add(R.id.content_frame, articleFrag, "detail");
        transaction.addToBackStack("detail");

        // Commit the transaction
        transaction.commit();
        /*
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
    }

    @Override
    public void onArticleFragmentInteraction(Uri uri) {

    }

    @Override
    public void changeDrawerTitle(String title) {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void sendBackLastList(ArrayList<RSSItem> itemsList) {
        this.itemList = itemsList;
        startFetchService (itemsList);
    }

    private void startFetchService(ArrayList<RSSItem> itemsList) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String freq = sharedPreferences.getString(getString(R.string.sync_frequency_key), "360");

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int intervalTIme = Integer.valueOf(freq);
        //int millsec = intervalTIme * 60 * 1000;
        //int millsec = intervalTIme * 1000;
        int millsec = 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + millsec;

        Intent serviceIntent = new Intent(this, CheckIntentService.class);
        Bundle serviceBundle = new Bundle ();
        serviceBundle.putParcelableArrayList(CheckIntentService.LIST_KEY, itemsList);
        serviceIntent.putExtras(serviceBundle);

        PendingIntent pintent = PendingIntent.getService(this, 0, serviceIntent, 0);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, millsec, pintent);

        //startService(serviceIntent);
    }

    @Override
    public void changeProgressBar(int isShow) {
        if (isShow!=0) {
            pbr.setVisibility(View.GONE);
        } else {
            pbr.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setDrawerIcon (boolean b) {

        /*
        if (getSupportActionBar()!=null) {

            if (b) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                toggle.setDrawerIndicatorEnabled(false);

            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                    public void onDrawerClosed(View view) {
                        supportInvalidateOptionsMenu();
                        //drawerOpened = false;
                    }

                    public void onDrawerOpened(View drawerView) {
                        supportInvalidateOptionsMenu();
                        //drawerOpened = true;
                    }
                };
                toggle.setDrawerIndicatorEnabled(true);
                drawer.setDrawerListener(toggle);
                toggle.syncState();
            }
        }*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        FragmentManager fgmr = getSupportFragmentManager ();
        int num = fgmr.getBackStackEntryCount();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (num > 0) {
                fgmr.popBackStack();
            } else {
                if (doubleBackToExitPressedOnce) {
                    //super.onBackPressed();
                    finish();

                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString (R.string.exitconfirm), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

//    @Override
//    public void onBackStackChanged() {
//        shouldDisplayHomeUp();
//    }
//
//    public void shouldDisplayHomeUp() {
//        //Enable Up button only  if there are entries in the back stack
//        int num = getSupportFragmentManager().getBackStackEntryCount();
//
//        boolean bBack;
//        if (num > 0) {
//            bBack = true;
//        } else {
//            bBack = false;
//        }
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(bBack);
//        }
//    }

    /*
    @Override
    public void onBackPressed() {

    }



    @Override
    public boolean onNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        shouldDisplayHomeUp ();
        return true;
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        getSupportFragmentManager().popBackStack();
        /*
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
        //}
    }*/
}
