package com.gmail.victorkusov.postcrossinghelper.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class ResponseHandler extends Handler {
    private static final String TAG = ResponseHandler.class.getSimpleName();

    private static final int DATA_RESPONSE = 2;
    private static final String KEY_MESSAGE = "message";
    private static final String OBJECT_SEPARATOR = "&";
    private static final String OBJECT_FIELDS_SEPARATOR = "%";

    private INewDataNotify mDataNotify;

    public ResponseHandler(INewDataNotify dataNotify) {
        mDataNotify = dataNotify;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DATA_RESPONSE: {
                String inputDistanceCodeString = msg.getData().getString(KEY_MESSAGE);
                List<DistanceCode> codeList = parseMessage(inputDistanceCodeString);

                if (codeList != null) {
                    mDataNotify.dataNotify(codeList);
                }
                break;
            }
            default: {
                super.handleMessage(msg);
            }
        }
    }

    @Nullable
    private List<DistanceCode> parseMessage(@Nullable String inputDistanceCodeString) {
        try {
            List<DistanceCode> codeList = new ArrayList<>();

            if (inputDistanceCodeString != null && !inputDistanceCodeString.isEmpty()) {
                Pattern pattern = Pattern.compile(OBJECT_SEPARATOR);
                String[] objectsData = pattern.split(inputDistanceCodeString);

                pattern = Pattern.compile(OBJECT_FIELDS_SEPARATOR);
                for (String data : objectsData) {
                    String[] fields = pattern.split(data);
                    DistanceCode code = new DistanceCode();

                    code.setId(Integer.parseInt(fields[0]));
                    code.setDistance(Double.parseDouble(fields[1]));
                    code.setRegion(fields[2]);
                    code.setPlace(fields[3]);
                    code.setLand(fields[4]);
                    code.setPostalCode(fields[5]);
                    code.setCountryCode(fields[6]);
                    code.setLatitude(Double.parseDouble(fields[7]));
                    code.setLongitude(Double.parseDouble(fields[8]));
                    code.setIso(fields[9]);

                    codeList.add(code);
                }
            }
            return codeList;

        } catch (Exception e){
            Log.d(TAG, "parseMessage: wrong data has come" + e.toString());
            return null;
        }
    }
}
