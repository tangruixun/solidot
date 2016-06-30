package com.trx.solidot;


import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by TRX on 06/28/2016.
 */
public class FetchParseFeedTask extends AsyncTask<String, Integer, ArrayList<RSSItem>> {

    private WeakReference<FragmentActivity> weakRef;
    private ArrayList<RSSItem> articleList;


    public FetchParseFeedTask(FragmentActivity activity) {
        weakRef = new WeakReference<>(activity);
    }

    @Override
    protected ArrayList<RSSItem> doInBackground(String[] finalUrl) {

        articleList = new ArrayList<>();

        try {
            URL url = new URL(finalUrl[0]); //呼叫網址進來

            SAXParserFactory spf = SAXParserFactory.newInstance();//先蓋一個工廠
            SAXParser sp = spf.newSAXParser();//工廠有一個知識不太高的解析工人
            XMLReader xr = sp.getXMLReader();//也有一個閱讀工人
            RSSHandler myHandler = new RSSHandler();//用到了我們之後建立的分配工人
            xr.setContentHandler(myHandler);//將閱讀工人和分配工人做結合
            xr.parse(new InputSource(url.openStream()));//閱讀工人用parse去開啟一個InputStream放資料

            articleList = myHandler.getParsedData();//getParsedData()方法會在myHandler裡看到


        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleList;
    }
}