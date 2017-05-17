package com.example.borntodieee.zhiwuya.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.borntodieee.zhiwuya.R;
import com.example.borntodieee.zhiwuya.bookmarks.BookmarksFragment;
import com.example.borntodieee.zhiwuya.bookmarks.BookmarksPresenter;
import com.example.borntodieee.zhiwuya.service.CacheService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private HomepageFragment homepageFragment;
    private BookmarksFragment bookmarksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        // 恢复fragment的状态
        if (savedInstanceState != null) {
            homepageFragment = (HomepageFragment) getSupportFragmentManager().getFragment(savedInstanceState, "HomepageFragment");
            bookmarksFragment = (BookmarksFragment) getSupportFragmentManager().getFragment(savedInstanceState, "BookmarksFragment");
        } else {
            homepageFragment = new HomepageFragment();
            bookmarksFragment = new BookmarksFragment();
        }
        if (!homepageFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment, homepageFragment, "HomepageFragment").commit();
        }
        if (!bookmarksFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment, bookmarksFragment, "BookmarksFragment").commit();
        }

        // 实例化BookmarksPresenter
        new BookmarksPresenter(MainActivity.this, bookmarksFragment);
        // 实例化HomepagePresenter
        new HomepagePresenter(MainActivity.this, homepageFragment);

        // 默认显示首页内容
        showHomepageFragment();

        startService(new Intent(this, CacheService.class));
    }

    //初始化控件
    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 显示MainFragment并设置Title
    private void showHomepageFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(homepageFragment);
        fragmentTransaction.hide(bookmarksFragment);
        fragmentTransaction.commit();
        fab.show();
        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    // 显示BookmarksFragment并设置Title
    private void showBookmarksFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(bookmarksFragment);
        fragmentTransaction.hide(homepageFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(getResources().getString(R.string.nav_bookmarks));
        fab.hide();

        if (bookmarksFragment.isAdded()) {
            bookmarksFragment.notifyDataChanged();
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (homepageFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "HomepageFragment", homepageFragment);
        }
        if (bookmarksFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "BookmarksFragment", bookmarksFragment);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            showHomepageFragment();
        } else if (id == R.id.nav_bookmarks) {
            showBookmarksFragment();
        } else if (id == R.id.nav_change_theme) {
//
//            // change the day/night mode after the drawer closed
//            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
//                @Override
//                public void onDrawerSlide(View drawerView, float slideOffset) {
//
//                }
//
//                @Override
//                public void onDrawerOpened(View drawerView) {
//
//                }
//
//                @Override
//                public void onDrawerClosed(View drawerView) {
//                    SharedPreferences sp =  getSharedPreferences("user_settings",MODE_PRIVATE);
//                    if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
//                            == Configuration.UI_MODE_NIGHT_YES) {
//                        sp.edit().putInt("theme", 0).apply();
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    } else {
//                        sp.edit().putInt("theme", 1).apply();
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    }
//                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
//                    recreate();
//                }
//
//                @Override
//                public void onDrawerStateChanged(int newState) {
//
//                }
//            });

        } else if (id == R.id.nav_settings) {
//            startActivity(new Intent(this,SettingsPreferenceActivity.class));
        } else if (id == R.id.nav_about) {
//            startActivity(new Intent(this,AboutPreferenceActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
