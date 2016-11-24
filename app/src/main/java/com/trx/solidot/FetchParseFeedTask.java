package com.trx.solidot;


import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by TRX on 06/28/2016.
 */
class FetchParseFeedTask extends AsyncTask<String, Integer, ArrayList<RSSItem>> {

    private int flag = 0; // 0: Fragment, 1: Service
    private WeakReference<Fragment> weakFrgRef;
    private WeakReference<Service>  weakSrvRef;


    FetchParseFeedTask(Fragment frag, int flag) {
        weakFrgRef = new WeakReference<>(frag);
        this.flag = flag;
    }

    FetchParseFeedTask(Service serv, int flag) {
        weakSrvRef = new WeakReference<>(serv);
        this.flag = flag;
    }

    @Override
    protected ArrayList<RSSItem> doInBackground(String[] finalUrl) {

        ArrayList<RSSItem> articleList = new ArrayList<>();
        Context context;
        if (flag == 0) {
            SolidotListFragment frag = (SolidotListFragment) weakFrgRef.get();
            context = frag.getActivity();
        } else {
            CheckIntentService srv = (CheckIntentService) weakSrvRef.get();
            context = srv;
        }
        InputStream is = null;

        try {
            if (flag == 0) {
                publishProgress(0);
            }

            URL url = new URL(finalUrl[0]); //呼叫網址進來

            SAXParserFactory spf = SAXParserFactory.newInstance();//先蓋一個工廠
            SAXParser sp = spf.newSAXParser();//工廠有一個知識不太高的解析工人
            XMLReader xr = sp.getXMLReader();//也有一個閱讀工人
            RSSHandler myHandler = new RSSHandler(context);//用到了我們之後建立的分配工人
            xr.setContentHandler(myHandler);//將閱讀工人和分配工人做結合
            is = url.openStream();
            xr.parse(new InputSource(is));//閱讀工人用parse去開啟一個InputStream放資料
            articleList = myHandler.getParsedData();//getParsedData()方法會在myHandler裡看到
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (flag == 0) {
                publishProgress(1);
            }
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    is = null;
                }
            }
        }
        return articleList;
    }

    @Override
    protected void onPostExecute(ArrayList<RSSItem> rssItems) {
        super.onPostExecute(rssItems);
        if (flag == 0) {
            SolidotListFragment frag = (SolidotListFragment) weakFrgRef.get();
            frag.updateDataList(rssItems);
        } else {
            CheckIntentService srv = (CheckIntentService) weakSrvRef.get();
            srv.gotLatestList(rssItems);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (flag == 0) {
            int v = values[0];
            SolidotListFragment frag = (SolidotListFragment) weakFrgRef.get();
            frag.changeProgress(v);
        } else {
            ; // nothing
        }
    }
}
