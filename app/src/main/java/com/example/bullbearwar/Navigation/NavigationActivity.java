package com.example.bullbearwar.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullbearwar.Chatroom.ChatroomActivity;
import com.example.bullbearwar.CurrencyConverter.CurrencyActivity;
import com.example.bullbearwar.Firebase.FirebaseMaster;
import com.example.bullbearwar.News.NewsActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Record.SearchRecordActivity;
import com.example.bullbearwar.Top10.Top10Fragment;
import com.example.bullbearwar.Searching.YahooHsiActivity;
import com.example.bullbearwar.Searching.YahooSearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.bullbearwar.Firebase.FirebaseMaster.mAuth;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.currentUser;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            View hView =  navigationView.getHeaderView(0);
            TextView nav_user = hView.findViewById(R.id.nav_userName);
            nav_user.setText(currentUser.getDisplayName());
            TextView nav_userEmail = hView.findViewById(R.id.nav_userEmail);
            nav_userEmail.setText(currentUser.getEmail());
        }

        YahooHsiActivity homeFragment = new YahooHsiActivity();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, homeFragment).commit();

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);

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
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearSearchRecord) {
            onClearSharedPreference();
            SearchRecordActivity searchRecordFragment = new SearchRecordActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, searchRecordFragment).commit();

        } else if (id == R.id.action_signOut) {
            FirebaseAuth.getInstance().signOut();
            FirebaseMaster smarttraderFragment = new FirebaseMaster();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, smarttraderFragment).commit();
            Toast.makeText(this, "You haved signed out.", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            YahooHsiActivity homeFragment = new YahooHsiActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, homeFragment).commit();

        }  else if (id == R.id.nav_search) {
            YahooSearchActivity searchRecordFragment = new YahooSearchActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, searchRecordFragment).commit();

        } else if (id == R.id.nav_record) {
            SearchRecordActivity searchRecordFragment = new SearchRecordActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, searchRecordFragment).commit();

        } else if (id == R.id.nav_smarttrader) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("logInSource","trading");
            editor.commit();
            FirebaseMaster smarttraderFragment = new FirebaseMaster();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, smarttraderFragment).commit();

        } else if (id == R.id.nav_news) {
            NewsActivity newsFragment = new NewsActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, newsFragment).commit();

        } else if (id == R.id.nav_top10) {
            Top10Fragment top10Fragment = new Top10Fragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, top10Fragment).commit();

        } else if (id == R.id.nav_setting) {
            SettingFragment settingFragment = new SettingFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, settingFragment).commit();

        } else if (id == R.id.nav_currency) {
            CurrencyActivity currencyFragment = new CurrencyActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, currencyFragment).commit();

        } else if (id == R.id.nav_chatroom) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("logInSource","chartroom");
            editor.commit();
            ChatroomActivity chatroomFragment = new ChatroomActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, chatroomFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onClearSharedPreference(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "SharedPreference clear", Toast.LENGTH_SHORT).show();
    }
}
