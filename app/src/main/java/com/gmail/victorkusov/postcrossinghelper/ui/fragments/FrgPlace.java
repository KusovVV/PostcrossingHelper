package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.RecyclerViewAdapter;
import com.gmail.victorkusov.postcrossinghelper.ui.network.IGetInformation;
import com.gmail.victorkusov.postcrossinghelper.ui.network.RetrofitHelper;
import com.gmail.victorkusov.postcrossinghelper.ui.network.Stuff;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgPlace extends Fragment {


    private static final String KEY_VALUE = "placeName";
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    private SearchView searchView;
    private TextView mMessageText;
    private ProgressBar mBar;

    String place;


    public FrgPlace() {
    }

    public static FrgPlace newInstance() {
        FrgPlace instance = new FrgPlace();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_place, container, false);

        mRecyclerView = v.findViewById(R.id.frg_place_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter(new ArrayList<PostalCode>());

        mMessageText = v.findViewById(R.id.frg_postalcode_txt);
        mMessageText.setVisibility(View.VISIBLE);

        mBar = v.findViewById(R.id.frg_postalcode_bar);
        mBar.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            place = savedInstanceState.getString(KEY_VALUE);
            if (place != null && place.length() > 0) {
                getPlaceInformation(place);
            }
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQuery(place, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                place = query;
                getPlaceInformation(query);
                Stuff.hideKeyboard(getActivity());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchMenuItem.setVisible(true);
    }

    private void getPlaceInformation(String placeName) {
        mBar.setVisibility(View.VISIBLE);

        RetrofitHelper helper = RetrofitHelper.getInstance();
        IGetInformation codesList = helper.getRetrofit().create(IGetInformation.class);
        Call<PostalCodesList> listCall = codesList.getDataByPlaceName(placeName, RetrofitHelper.USER_NAME);
        listCall.enqueue(new Callback<PostalCodesList>() {
            @Override
            public void onResponse(@NonNull Call<PostalCodesList> call, @NonNull Response<PostalCodesList> response) {
                Log.d(RetrofitHelper.TAG, "got some result");

                //((DataListAdapter)mListView.getAdapter()).setCodes(response.body().getPostalCodes());
                if (getActivity() != null) {
                    adapter.setData(response.body().getPostalCodes());
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    updateUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostalCodesList> call, @NonNull Throwable t) {
                Log.d(RetrofitHelper.TAG, t.getMessage());
            }
        });
    }

    private void updateUI() {
        mBar.setVisibility(View.GONE);
        mMessageText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

       // if (searchView != null) {
            outState.putString(KEY_VALUE, place);
       // }
    }
}
