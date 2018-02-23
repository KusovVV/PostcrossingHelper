package com.gmail.victorkusov.postcrossinghelper.ui.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.BaseFragment;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgNearPlaces;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPlace;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPostalCode;


public class WorkScreenActivity extends AppCompatActivity {

    private static final String TAG = "Log " + WorkScreenActivity.class.getSimpleName();
    private static final java.lang.String KEY_FRAGMENT_ID = "fragmentTag";

    private BottomNavigationView mNavigationMenu;
    private FragmentManager mFragmentManager;

    private int mItemId = R.id.menu_navigator_code;

    private String frgTag = FrgPostalCode.TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mFragmentManager = getSupportFragmentManager();

        //[Setup ]
        mNavigationMenu = findViewById(R.id.main_screen_navigationview);
        mNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mItemId = item.getItemId();
                manageFragments(mItemId);
                return true;
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_FRAGMENT_ID)) {
                mItemId = savedInstanceState.getInt(KEY_FRAGMENT_ID);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavigationMenu.setSelectedItemId(mItemId);
    }

    private void manageFragments(int itemId) {
        switch (itemId) {
            case R.id.menu_navigator_code: {
                frgTag = FrgPostalCode.TAG;
                break;
            }
            case R.id.menu_navigator_place: {
                frgTag = FrgPlace.TAG;
                break;
            }
            case R.id.menu_navigator_near_places: {
                frgTag = FrgNearPlaces.TAG;
                break;
            }
        }
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(frgTag);
        if (fragment == null) {
            fragment = BaseFragment.newFragment(frgTag);
        }
        mFragmentManager.beginTransaction().replace(R.id.main_screen_fragment_container, fragment, frgTag).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "looking for " + query);
                // mQuery = query;
                searchMenuItem.collapseActionView();
                makeQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void makeQuery(String query) {
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentById(R.id.main_screen_fragment_container);
        fragment.setQuery(query);
        fragment.refreshData();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt(KEY_FRAGMENT_ID, mItemId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        this.finish();
    }
}
