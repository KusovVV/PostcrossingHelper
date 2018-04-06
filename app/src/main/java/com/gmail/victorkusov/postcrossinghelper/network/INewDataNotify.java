package com.gmail.victorkusov.postcrossinghelper.network;

import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;

import java.util.List;

public interface INewDataNotify {

    void dataNotify(List<DistanceCode> dataList);
}
