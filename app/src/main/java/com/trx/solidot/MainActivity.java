package com.trx.solidot;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SolidotListFragment.OnTitleSelectedListener,
        ArticleFragment.OnArticleFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener{

    private boolean viewIsAtHome;
    private AdView adView;
    private ArrayList<RSSItem> itemList;
    private ProgressBar pbr;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        pbr = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        setSupportActionBar(toolbar);

        itemList = new ArrayList<>();

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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_home);
        getFragmentManager().addOnBackStackChangedListener(this);

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

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected (item);
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
            SolidotListFragment sf = SolidotListFragment.newInstance();
            transaction.replace(R.id.content_frame, newFragment);
            transaction.addToBackStack("detail");

            // Commit the transaction
            transaction.commit();
        }
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

        Intent startServiceIntent = new Intent(this, CheckIntentService.class);
        Bundle serviceBundle = new Bundle ();
        serviceBundle.putInt(CheckIntentService.INTERVAL_TIMER_KEY, Integer.valueOf(freq));
        serviceBundle.putParcelableArrayList(CheckIntentService.LIST_KEY, itemsList);
        startServiceIntent.putExtras(serviceBundle);
        startService(startServiceIntent);
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
        }
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
    public void onBackStackChanged() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            if (getSupportActionBar () != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }else {
            if (getSupportActionBar () != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            toggle.syncState();
        }
    }
}
