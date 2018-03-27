package com.gmail.victorkusov.postcrossinghelper.ui.activities;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.network.ICompleteParseDataListener;
import com.gmail.victorkusov.postcrossinghelper.model.InputData;
import com.gmail.victorkusov.postcrossinghelper.network.ParseThread;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
import com.gmail.victorkusov.postcrossinghelper.service.LocalGeoService;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.TrackCodeAdapter;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.BaseFragment;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgNearPlaces;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPlace;
import com.gmail.victorkusov.postcrossinghelper.ui.fragments.FrgPostalCode;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class WorkScreenActivity extends AppCompatActivity {

    private static final String TAG = "Log " + WorkScreenActivity.class.getSimpleName();
    private static final java.lang.String KEY_FRAGMENT_ID = "fragmentTag";
    private static final String KEY_USER_NAME = "display_name";
    private static final int GRANTED = 111;
    public static final String CONNECTION_LOST_MESSAGE = "Network is unable! Please check your network connection and try again";

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

                        Dialog dialog = getMessageDialog();

                        dialog.show();
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

        if (!Utils.isNetworkPermissonEnabled(this)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GRANTED);
        } else {
            startService(new Intent(this, LocalGeoService.class));
        }
    }

    private Dialog getMessageDialog() {
        final Dialog dialog = new Dialog(WorkScreenActivity.this);
        dialog.setContentView(R.layout.dialog_track_code);
        dialog.setTitle("Title !!!!");

        mTrackCodeListView = dialog.findViewById(R.id.dialog_track_list_results);

        dialog.findViewById(R.id.dialog_track_btn_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackCode = ((EditText) dialog.findViewById(R.id.dialog_track_edit_track)).getText().toString();

                // insert work with jsoup
                getTrackCodeListResults(trackCode);
            }
        });

        return dialog;
    }

    private void getTrackCodeListResults(String trackCode) {

        ParseThread thread = new ParseThread(trackCode, new ICompleteParseDataListener() {
            @Override
            public void onDataReady(final List<InputData> dataList) {
                WorkScreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //make adapter
                        TrackCodeAdapter adapter = (TrackCodeAdapter) mTrackCodeListView.getAdapter();
                        if (adapter == null) {
                            adapter = new TrackCodeAdapter();
                            adapter.setDataList(dataList);
                            mTrackCodeListView.setAdapter(adapter);
                        } else {
                            adapter.setDataList(dataList);
                        }
                        }
                    });
                }
            });
        thread.start();
        }


    private String getTittleToDrawerField() {
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(frgTag);
        return fragment.isShowSaved() ?
                getResources().getStringArray(R.array.show_firebase_entries)[1] :
                getResources().getStringArray(R.array.show_firebase_entries)[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, LocalGeoService.class));
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
