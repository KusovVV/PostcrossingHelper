package com.gmail.victorkusov.postcrossinghelper.ui.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.InputData;
import com.gmail.victorkusov.postcrossinghelper.network.ICompleteParseDataListener;
import com.gmail.victorkusov.postcrossinghelper.network.ParseThread;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.TrackCodeAdapter;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.BaseFragment;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgNearPlaces;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPlace;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPostalCode;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class WorkScreenActivity extends AppCompatActivity {

    private static final String TAG = "Log " + WorkScreenActivity.class.getSimpleName();
    private static final java.lang.String KEY_FRAGMENT_ID = "fragmentTag";
    private static final String KEY_USER_NAME = "display_name";
    public static final String CONNECTION_LOST_MESSAGE = "Network is unable! Please check your network connection and try again";
    public static final String[] GEO_SERVICE_PERMISSIONS = {"com.gmail.victorkusov.diffprocess.START_INNER_SERVICE"};
    private static final int GEO_SERVICE_PERMISSION_REQUEST = 578;


    private BottomNavigationView mNavigationMenu;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;

    private int mItemId = R.id.menu_navigator_code;

    private String frgTag = FrgPostalCode.TAG;
    private FirebaseAuth mFirebaseAuth;
    private ListView mTrackCodeListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mFragmentManager = getSupportFragmentManager();
        mFirebaseAuth = FirebaseAuth.getInstance();

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

        //[Navigation Drawer]
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        final String userName = intent.getStringExtra(KEY_USER_NAME);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                NavigationView navigationView = findViewById(R.id.navigation_view);
                MenuItem item = navigationView.getMenu().findItem(R.id.navigation_item_show_saved);
                item.setTitle(getTittleToDrawerField());

                TextView userNameText = findViewById(R.id.navigation_header_title);
                userNameText.setText(userName);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });


        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item_logout: {
                        mFirebaseAuth.signOut();
                        Intent intent = new Intent(WorkScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        WorkScreenActivity.this.finish();
                        break;
                    }
                    case R.id.navigation_item_show_saved: {
                        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(frgTag);
                        fragment.setShowSaved(!fragment.isShowSaved());
                        fragment.restoreAdapter();

                        item.setTitle(getTittleToDrawerField());
                        break;
                    }
                    case R.id.navigation_track_code: {
                        startActivity(new Intent(WorkScreenActivity.this, TrackCodeActivity.class));
                        break;
                    }
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_FRAGMENT_ID)) {
                mItemId = savedInstanceState.getInt(KEY_FRAGMENT_ID);
            }
        }

        if (Utils.isGeoServicePermissonEnabled(this)) {
            Utils.startGeoService(WorkScreenActivity.this);
        } else {
            requestForGeoServicePermission();
        }
    }

    private void requestForGeoServicePermission() {
        new AlertDialog.Builder(WorkScreenActivity.this)
                .setTitle(R.string.dialog_permission_service_title)
                .setMessage(R.string.dialog_permission_service_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.isGeoServicePermissonEnabled(WorkScreenActivity.this)) {
                            Utils.startGeoService(WorkScreenActivity.this);
                        } else {
                            ActivityCompat.requestPermissions(WorkScreenActivity.this,
                                    GEO_SERVICE_PERMISSIONS, GEO_SERVICE_PERMISSION_REQUEST);
                        }
                    }
                }).show();
    }

    private String getTittleToDrawerField() {
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(frgTag);
        return fragment.isShowSaved() ?
                getResources().getStringArray(R.array.show_firebase_entries)[1] :
                getResources().getStringArray(R.array.show_firebase_entries)[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GEO_SERVICE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.startGeoService(WorkScreenActivity.this);
            } else {
                Toast.makeText(this, R.string.permission_location, Toast.LENGTH_SHORT).show();
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
                if (Utils.hasNetworkConnection(WorkScreenActivity.this)) {
                    makeQuery(query);
                } else {
                    Toast.makeText(WorkScreenActivity.this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_SHORT).show();
                }
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
        fragment.refreshData(query);
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
