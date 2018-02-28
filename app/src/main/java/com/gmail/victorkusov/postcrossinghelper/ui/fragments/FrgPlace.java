package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.ui.adapters.OnItemClickListener;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.Utils;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.RecyclerViewAdapter;
import com.gmail.victorkusov.postcrossinghelper.network.RetrofitHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgPlace extends BaseFragment {

    public static final String TAG = "LOG " + FrgPlace.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mViewAdapter;


    public FrgPlace() {
    }


    public static FrgPlace newInstance() {
        FrgPlace instance = new FrgPlace();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_place, container, false);

        mRecyclerView = v.findViewById(R.id.frg_place_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (!Utils.hasNetworkConnection(v.getContext())) {
            getDataFromRealm();
        }

        TextView textView = v.findViewById(R.id.msg_for_empty_list);
        String text = String.format(getResources().getString(R.string.empty_list), getResources().getText(R.string.place_name));
        textView.setText(text);

        return v;
    }

    @Override
    public void getListData(String query) {
        mRecyclerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "getPlaceData: " + query);

        Call<PostalCodesList> listCall = RetrofitHelper.getInstance().getDataByPlaceName(query);
        listCall.enqueue(new Callback<PostalCodesList>() {
            @Override
            public void onResponse(@NonNull Call<PostalCodesList> call, @NonNull Response<PostalCodesList> response) {
                Log.d(TAG, "got some result");
                List<PostalCode> queryList = response.body().getPostalCodes();

                if (getActivity() != null && queryList != null) {
                    mViewAdapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();
                    if (mViewAdapter == null) {
                        mViewAdapter = new RecyclerViewAdapter(queryList, getSaveRealmListener());
                        mRecyclerView.setAdapter(mViewAdapter);
                        return;
                    }
                    mViewAdapter.setData(queryList);
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
                mViewAdapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();
                mViewAdapter.setData(getPostalCodeListDataFromRealm());
            }
        };
    }

    @Override
    public void getDataFromRealm() {
        mViewAdapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();
        if (mViewAdapter == null) {
            mRecyclerView.setAdapter(new RecyclerViewAdapter(getPostalCodeListDataFromRealm(), new OnItemClickListener() {
                @Override
                public void onItemClick(PostalCode postalCode) {
                    deleteEntry(postalCode);
                }
            }));
        } else {
            mViewAdapter.setData(getPostalCodeListDataFromRealm());
        }
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
