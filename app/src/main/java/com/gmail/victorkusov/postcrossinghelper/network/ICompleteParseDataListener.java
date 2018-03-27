package com.gmail.victorkusov.postcrossinghelper.network;

import com.gmail.victorkusov.postcrossinghelper.model.InputData;

import java.util.List;


public interface ICompleteParseDataListener {
    void onDataReady(List<InputData> dataList);
}
