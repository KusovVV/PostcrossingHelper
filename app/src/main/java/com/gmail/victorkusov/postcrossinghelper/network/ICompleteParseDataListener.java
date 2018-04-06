package com.gmail.victorkusov.postcrossinghelper.network;

import android.support.annotation.NonNull;

import com.gmail.victorkusov.postcrossinghelper.model.InputData;

import java.util.List;


public interface ICompleteParseDataListener {
    void onDataReady(@NonNull List<InputData> dataList);
}
