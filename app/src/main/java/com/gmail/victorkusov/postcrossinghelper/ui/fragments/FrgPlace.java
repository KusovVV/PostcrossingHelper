package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.utils.MessageDialog;
import com.gmail.victorkusov.postcrossinghelper.model.Place;
import com.gmail.victorkusov.postcrossinghelper.model.ModelConstants;
import com.gmail.victorkusov.postcrossinghelper.model.PlaceList;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.OnItemClickListener;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.RecyclerViewAdapter;
import com.gmail.victorkusov.postcrossinghelper.network.RetrofitHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgPlace extends BaseFragment {

    public static final String TAG = "LOG " + FrgPlace.class.getSimpleName();
    private final OnItemClickListener onItemClickListener;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mViewAdapter;


    public FrgPlace() {
        super();

        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDialog(position);
            }
        };
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
            restoreAdapter();
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

        Call<PlaceList> listCall = RetrofitHelper.getInstance().getDataByPlaceName(query);
        listCall.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(@NonNull Call<PlaceList> call, @NonNull Response<PlaceList> response) {
                Log.d(TAG, "got some result");

                PlaceList body = response.body();
                if (body != null) {
                    List<Place> queryList = body.getPlaces();
                    if (queryList != null) {
                        if (getActivity() != null) {
                            makeAdapter(queryList);
                            body.saveListToRealm();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceList> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void restoreAdapter() {
        if (isShowSaved()) {
            makeAdapterWithFirebaseData();
        } else {
            PlaceList placeList = new PlaceList();
            placeList.getListFromRealm();
            makeAdapter(placeList.getPlaces());
        }
    }

    private void makeAdapter(List<Place> queryList) {
        if (!queryList.isEmpty()){
            mViewAdapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();

            if (mViewAdapter == null) {
                mViewAdapter = new RecyclerViewAdapter();
                mViewAdapter.setData(queryList, onItemClickListener);
                mRecyclerView.setAdapter(mViewAdapter);
            } else {
                mViewAdapter.setData(queryList, onItemClickListener);
            }
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        int position = getElementPosition();
        if (position != -1) {
            showDialog(position);
        }
    }

    private void makeAdapterWithFirebaseData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ModelConstants.TABLE_NAME_PLACE);
        Query query = reference.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Place> queryList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    Place code = new Place();

                    code.setPostalCode((String) next.child(ModelConstants.TABLE_PLACE_FIELD_POSTAL_CODE).getValue());
                    code.setCountryCode((String) next.child(ModelConstants.TABLE_PLACE_FIELD_COUNTRY_CODE).getValue());
                    code.setPlaceName((String) next.child(ModelConstants.TABLE_PLACE_FIELD_PLACE_NAME).getValue());
                    code.setSavedToFirebase(true);

                    queryList.add(code);
                }
                makeAdapter(queryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void showDialog(final int position) {
        setElementPosition(position);

        final Context context = getContext();
        // get dialog reference
        AlertDialog.Builder dialog = MessageDialog.getDialog(context);

        //get managed element
        final Place place = mViewAdapter.getItem(position);

        //[Make title]
        String titleMessage = null;
        if (context != null) {
            Resources resources = context.getResources();
            if (resources != null) {
                titleMessage = String.format(resources.getString(R.string.manage_item),
                        resources.getString(place.isSavedToFirebase() ? R.string.want_to_delete : R.string.want_to_save));
            }
        }
        dialog.setTitle(titleMessage).setMessage(place.getPostalCode() + " " + place.getPlaceName());

        //[Set buttons]
        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!place.isSavedToFirebase()) {
                    Toast.makeText(context, "save element #" + position, Toast.LENGTH_SHORT).show();
                    place.setSavedToFirebase(true);
                    mViewAdapter.notifyItemChanged(position);
                    place.addNoteToFirebase();
                } else {
                    try {
                        place.deleteNoteFromFirebase();
                        place.setSavedToFirebase(false);
                        mViewAdapter.notifyItemChanged(position);
                    } catch (Exception ignored) {
                    }
                }
                clrElemPosition();
            }
        });
        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clrElemPosition();
            }
        });

        dialog.show();
    }

}
