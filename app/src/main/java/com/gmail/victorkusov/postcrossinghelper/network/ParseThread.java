package com.gmail.victorkusov.postcrossinghelper.network;


import com.gmail.victorkusov.postcrossinghelper.model.InputData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseThread extends Thread{
    private static final String URL_ADDRESS = "https://webservices.belpost.by/searchRu/";

    private ICompleteParseDataListener mHandler;
    private String mTrackCode;


    public ParseThread(String trackCode, ICompleteParseDataListener handler) {
        mHandler = handler;
        mTrackCode = trackCode;
    }

    @Override
    public void run() {
        try {
            String url = URL_ADDRESS.concat(mTrackCode);
            Document document = Jsoup.connect(url).get();

            List<InputData> dataList = new ArrayList<>();

            Element table = document.getElementById("GridInfo");
            Elements row = table.select("tr");

            int size = row.size();
            for (int i = 1; i < size; i++) {
                Elements ths = row.get(i).select("td");

                InputData inputData = new InputData();
                inputData.setDateTime(ths.get(0).text());
                inputData.setEvent(ths.get(1).text());
                inputData.setPlace(ths.get(2).text());
                dataList.add(inputData);
            }

            table = document.getElementById("GridInfo0");
            row = table.select("tr");

            size = row.size();
            for (int i = 1; i < size; i++) {
                Elements ths = row.get(i).select("td");

                InputData inputData = new InputData();
                inputData.setDateTime(ths.get(0).text());
                inputData.setEvent(ths.get(1).text());
                dataList.add(inputData);
            }
            mHandler.onDataReady(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
