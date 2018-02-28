package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.ui.adapters.OnItemClickListener;
import com.gmail.victorkusov.postcrossinghelper.Utils;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.DataListAdapter;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.network.RetrofitHelper;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgPostalCode extends BaseFragment {

    public static final String TAG = "LOG " + FrgPostalCode.class.getSimpleName();

    private ListView mListView;
    private DataListAdapter mViewAdapter;
    private ProgressBar mBar;


    public FrgPostalCode() {
    }


    public static FrgPostalCode newInstance() {
        FrgPostalCode instance = new FrgPostalCode();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_postalcode, container, false);

        mBar = v.findViewById(R.id.bar_for_empty_list);
        mBar.setVisibility(View.INVISIBLE);
        mListView = v.findViewById(R.id.list_view);

        if (!Utils.hasNetworkConnection(v.getContext())) {
            getDataFromRealm();
        }

        TextView textView = v.findViewById(R.id.msg_for_empty_list);
        String text = String.format(getResources().getString(R.string.empty_list), getResources().getText(R.string.postal_code));
        textView.setText(text);

        return v;
    }

    @Override
    public void getDataFromRealm() {
        mViewAdapter = (DataListAdapter) mListView.getAdapter();
        if (mViewAdapter == null) {
            mListView.setAdapter(new DataListAdapter(getPostalCodeListDataFromRealm(), new OnItemClickListener() {
                @Override
                public void onItemClick(PostalCode postalCode) {
                    deleteEntry(postalCode);
                }
            }));
        } else {
            mViewAdapter.setPostalCodeList(getPostalCodeListDataFromRealm());
        }
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void getListData(String query) {

        mListView.setVisibility(View.VISIBLE);
        Log.d(TAG, "getPlaceData for: " + query);
        Call<PostalCodesList> listCall = RetrofitHelper.getInstance().getByPostalCode(query);
        listCall.enqueue(new Callback<PostalCodesList>() {
            @Override
            public void onResponse(@NonNull Call<PostalCodesList> call, @NonNull Response<PostalCodesList> response) {
                Log.d(TAG, "got some result");

                List<PostalCode> queryData = response.body().getPostalCodes();
                if (getActivity() != null && queryData != null) {
                    mViewAdapter = (DataListAdapter) mListView.getAdapter();
                    if (mViewAdapter == null) {
                        mViewAdapter = new DataListAdapter(queryData, getSaveRealmListener());
                        mListView.setAdapter(mViewAdapter);
                        return;
                    }
                    mViewAdapter.setPostalCodeList(queryData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostalCodesList> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected DialogInterface.OnClickListener getDialogDeleteListener(final PostalCode postalCode) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), TEXT_SAVED, Toast.LENGTH_SHORT).show();
                deletePostalCodeFromRealm(postalCode);
                mViewAdapter = (DataListAdapter) mListView.getAdapter();
                mViewAdapter.setPostalCodeList(getPostalCodeListDataFromRealm());
            }
        };
    }
}
