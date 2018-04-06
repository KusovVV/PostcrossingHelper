package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.model.ModelConstants;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.OnItemClickListener;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.DataListAdapter;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.network.retrofit.RetrofitHelper;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;
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

public class FrgPostalCode extends BaseFragment {

    public static final String TAG = "LOG " + FrgPostalCode.class.getSimpleName();

    private ListView mListView;
    private DataListAdapter mViewAdapter;
    private ProgressBar mBar;
    private OnItemClickListener onItemClickListener;

    public static FrgPostalCode newInstance() {
        FrgPostalCode instance = new FrgPostalCode();
        return instance;
    }

    public FrgPostalCode() {
        super();
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDialog(position);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_postalcode, container, false);

        mBar = v.findViewById(R.id.bar_for_empty_list);
        mBar.setVisibility(View.INVISIBLE);
        mListView = v.findViewById(R.id.list_view);

        if (Utils.hasNetworkConnection(v.getContext())) {
            TextView textView = v.findViewById(R.id.msg_for_empty_list);
            String text = String.format(getResources().getString(R.string.empty_list), getResources().getText(R.string.postal_code));
            textView.setText(text);
        }
        return v;
    }

    @Override
    public void restoreAdapter() {
        if (isShowSaved()) {
            makeAdapterWithFirebaseData();
        } else {
            PostalCodesList codesList = new PostalCodesList();
            codesList.getListFromRealm();
            makeAdapter(codesList.getPostalCodes());
        }
    }

    private void makeAdapter(List<PostalCode> queryList) {
        if (!queryList.isEmpty()) {
            mViewAdapter = (DataListAdapter) mListView.getAdapter();
            if (mViewAdapter == null) {
                mViewAdapter = new DataListAdapter();
                mViewAdapter.setPostalCodeList(queryList, onItemClickListener);
                mListView.setAdapter(mViewAdapter);
            } else {
                mViewAdapter.setPostalCodeList(queryList, onItemClickListener);
            }
            mListView.setVisibility(View.VISIBLE);
            checkFirebase(queryList);
            //[check Dialog is shown]
            int position = getElementPosition();
            if (position != -1) {
                showDialog(position);
            }
        }
    }

    private void makeAdapterWithFirebaseData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ModelConstants.TABLE_NAME_POSTALCODE);
        Query query = reference.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PostalCode> queryList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    PostalCode code = new PostalCode();

                    code.setPostalCode((String) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_POSTAL_CODE).getValue());
                    code.setCountryCode((String) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_COUNTRY_CODE).getValue());
                    code.setLatitude((Double) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_LATITUDE).getValue());
                    code.setLongitude((Double) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_LONGITUDE).getValue());
                    code.setPlace((String) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_PLACE).getValue());
                    code.setRegion((String) next.child(ModelConstants.TABLE_POSTALCODE_FIELD_REGION).getValue());
                    code.setIsSavedToFirebase(true);

                    queryList.add(code);
                }
                makeAdapter(queryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getListData(final String query) {

        //  mListView.setVisibility(View.VISIBLE);
        Log.d(TAG, "getPlaceData for: " + query);
        Call<PostalCodesList> listCall = RetrofitHelper.getInstance().getByPostalCode(query);
        listCall.enqueue(new Callback<PostalCodesList>() {
            @Override
            public void onResponse(@NonNull Call<PostalCodesList> call, @NonNull Response<PostalCodesList> response) {
                Log.d(TAG, "got some result");

                PostalCodesList body = response.body();
                if (body != null) {
                    List<PostalCode> queryData = body.getPostalCodes();
                    if (queryData != null) {

                        if (getActivity() != null) {
                            makeAdapter(queryData);
                            body.saveListToRealm();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostalCodesList> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void checkFirebase(final List<PostalCode> codeList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ModelConstants.TABLE_NAME_POSTALCODE);

        for (final PostalCode code : codeList) {
            Query query = reference.child(code.getPostalCode()).orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        code.setIsSavedToFirebase(true);
                    } else {
                        code.setIsSavedToFirebase(false);
                    }
                    mViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    protected void showDialog(final int position) {
        setElementPosition(position);

        final Context context = getContext();
        // get dialog reference
        AlertDialog.Builder dialog;

        //get managed element
        final PostalCode postalCode = mViewAdapter.getItem(position);

        //[Make title]
        String titleMessage = null;
        if (context != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                dialog = new AlertDialog.Builder(context, android.R.style.TextAppearance_Theme_Dialog);
            }
            Resources resources = context.getResources();
            if (resources != null) {
                titleMessage = String.format(resources.getString(R.string.manage_item),
                        resources.getString(postalCode.isSavedToFirebase() ? R.string.want_to_delete : R.string.want_to_save));
            }

            dialog.setTitle(titleMessage).setMessage(postalCode.getPostalCode() + " " + postalCode.getPlace());

            //[Set buttons]
            dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (!postalCode.isSavedToFirebase()) {
                        Toast.makeText(context, "save element #" + position, Toast.LENGTH_SHORT).show();
                        postalCode.setIsSavedToFirebase(true);
                        postalCode.addNoteToFirebase();
                        mViewAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            postalCode.deleteNoteFromFirebase();
                            postalCode.setIsSavedToFirebase(false);
                            mViewAdapter.notifyDataSetChanged();
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
}
