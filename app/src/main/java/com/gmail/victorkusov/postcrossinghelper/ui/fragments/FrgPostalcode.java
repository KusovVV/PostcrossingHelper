package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.DataListAdapter;
import com.gmail.victorkusov.postcrossinghelper.ui.network.IGetInformation;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.ui.network.RetrofitHelper;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;
import com.gmail.victorkusov.postcrossinghelper.ui.network.Stuff;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgPostalcode extends Fragment {

    private static final String LOG = "LOG " + FrgPostalcode.class.getSimpleName();
    private static final String LIST_STATE = "listState";
    private static final String CODE_VALUE = "code";


    private ListView mListView;
    private DataListAdapter adapter;
    private TextView mMessageText;
    private ProgressBar mBar;
    private SearchView mSearchView;

    private String code;


    public FrgPostalcode() {
    }

    public static FrgPostalcode newInstance() {
        FrgPostalcode instance = new FrgPostalcode();

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
        View v = inflater.inflate(R.layout.frg_postalcode, container, false);
        mMessageText = v.findViewById(R.id.frg_postalcode_txt);
        mMessageText.setVisibility(View.VISIBLE);

        mBar = v.findViewById(R.id.frg_postalcode_bar);
        mBar.setVisibility(View.INVISIBLE);

        mListView = v.findViewById(R.id.list_view);

        if (getActivity() != null) {
            adapter = new DataListAdapter(getActivity(), R.id.list_view, new ArrayList<PostalCode>());
        }

        if (savedInstanceState != null) {
            code = savedInstanceState.getString(CODE_VALUE);
            if (code != null && code.length() > 0) {
                getPostalCodeData(code);

            }
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSearchView.setQuery(code, false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                code = query;
                Stuff.hideKeyboard(getActivity());
                getPostalCodeData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchMenuItem.setVisible(true);
    }

    private void getPostalCodeData(String postalCode) {
        mBar.setVisibility(View.VISIBLE);

        RetrofitHelper helper = RetrofitHelper.getInstance();
        IGetInformation codesList = helper.getRetrofit().create(IGetInformation.class);
        Call<PostalCodesList> listCall = codesList.getByPostalCode(postalCode, RetrofitHelper.USER_NAME);
        listCall.enqueue(new Callback<PostalCodesList>() {
            @Override
            public void onResponse(@NonNull Call<PostalCodesList> call, @NonNull Response<PostalCodesList> response) {
                Log.d(RetrofitHelper.TAG, "got some result");

                //((DataListAdapter)mListView.getAdapter()).setCodes(response.body().getPostalCodes());

                if (getActivity() != null) {
                    adapter = new DataListAdapter(getActivity(), R.id.list_view, response.body().getPostalCodes());
                    mListView.setAdapter(adapter);
                    updateUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostalCodesList> call, @NonNull Throwable t) {
                Log.d(RetrofitHelper.TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putInt(LIST_POSITION,mListView.getFirstVisiblePosition());

        // outState.putParcelable(LIST_STATE, mListView.onSaveInstanceState());

       // if (mSearchView != null) {
            outState.putString(CODE_VALUE, code);
      //  }
    }

    private void updateUI() {
        mBar.setVisibility(View.GONE);
        mMessageText.setVisibility(View.GONE);

        mListView.setVisibility(View.VISIBLE);
    }
}
