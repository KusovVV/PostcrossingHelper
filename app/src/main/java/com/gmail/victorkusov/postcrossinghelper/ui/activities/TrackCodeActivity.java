package com.gmail.victorkusov.postcrossinghelper.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.InputData;
import com.gmail.victorkusov.postcrossinghelper.network.ICompleteParseDataListener;
import com.gmail.victorkusov.postcrossinghelper.network.ParseThread;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.TrackCodeAdapter;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;

import java.io.File;
import java.util.List;

public class TrackCodeActivity extends AppCompatActivity {
    private static final String TAG = TrackCodeActivity.class.getSimpleName();

    public static final String FILEPROVIDER_AUTHORITIES = "com.gmail.victorkusov.postcrossinghelper.fileprovider";
    private static final String KEY_TRACK_CODE = "data_list";

    private static final int REQUEST_SHARED = 13;
    private static final int FILES_PERMISSION_REQUEST = 354;
    public static final String TYPE_PDF = "application/pdf";

    private AppCompatEditText mEditText;
    private AppCompatButton mButtonTrack;
    private ListView mTrackCodeListView;
    private MenuItem mItemShare;

    private String mTrackCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_code);

        mEditText = findViewById(R.id.activity_track_edit_track);
        mButtonTrack = findViewById(R.id.activity_track_btn_track);
        mTrackCodeListView = findViewById(R.id.activity_track_list_results);

        mButtonTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackCode = mEditText.getText().toString();
                getListData(mTrackCode);
            }
        });

        if (savedInstanceState != null) {
            mTrackCode = savedInstanceState.getString(KEY_TRACK_CODE, "");
            getListData(mTrackCode);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_code, menu);
        mItemShare = menu.findItem(R.id.track_menu_action_share);
        mItemShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                makeExportReport();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void makeExportReport() {
        if(Utils.isFilesPermissonEnabled(TrackCodeActivity.this)) {
            final TrackCodeAdapter adapter = (TrackCodeAdapter) mTrackCodeListView.getAdapter();
            if (adapter != null) {
                final List<InputData> dataList = adapter.getDataList();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File file = Utils.makeFile(mTrackCode, dataList);

                        TrackCodeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    uri = FileProvider.getUriForFile(TrackCodeActivity.this, FILEPROVIDER_AUTHORITIES, file);
                                } else {
                                    uri = Uri.fromFile(file);
                                }
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType(TYPE_PDF);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivityForResult(intent, REQUEST_SHARED);
                            }
                        });
                    }
                }).start();
            }
        } else {
            ActivityCompat.requestPermissions(TrackCodeActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILES_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TRACK_CODE, mTrackCode);
        super.onSaveInstanceState(outState);
    }

    private void getListData(String trackCode) {
        if (Utils.isTrackCodeValid(mTrackCode)) {
            ParseThread thread = new ParseThread(trackCode, new ICompleteParseDataListener() {
                @Override
                public void onDataReady(@NonNull final List<InputData> dataList) {

                    TrackCodeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeAdapter(dataList);
                            mTrackCodeListView.setVisibility(View.VISIBLE);
                            mItemShare.setVisible(true);
                            if (!dataList.isEmpty()) {
                                mItemShare.setEnabled(true);
                            } else {
                                Toast.makeText(TrackCodeActivity.this,
                                        "Nothing found! Make sure you code is correct", Toast.LENGTH_SHORT).show();
                                mItemShare.setEnabled(false);
                            }
                        }
                    });
                }
            });
            thread.start();
        } else {
            Toast.makeText(TrackCodeActivity.this, "VERIFICATION FAILED!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeAdapter(@NonNull List<InputData> dataList) {
        TrackCodeAdapter adapter = (TrackCodeAdapter) mTrackCodeListView.getAdapter();
        if (adapter == null) {
            adapter = new TrackCodeAdapter();
            adapter.setDataList(dataList);
            mTrackCodeListView.setAdapter(adapter);
        } else {
            adapter.setDataList(dataList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHARED) {
                Toast.makeText(TrackCodeActivity.this, "Report has been exported", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == FILES_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeExportReport();
            } else {
                Toast.makeText(this, R.string.permission_files, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
