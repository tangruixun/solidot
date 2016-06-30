package com.trx.solidot;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by TRX on 06/29/2016.
 */
public class RSSHandler extends DefaultHandler {

    private ArrayList<RSSItem> li;
    private RSSItem item;
    int currentstate = 0;
    final int RSS_STATUS_TITLE = 1;//若是title标签,记做 1,注意有两个title,但我们都保存在item的title成员变量中
    final int RSS_STATUS_LINK = 2;//若是link标签,记做 2
    final int RSS_STATUS_DESCRIPTION = 3;//若是description标签,记做 3
    final int RSS_STATUS_PUBDATE = 4; //若是pubdate标签,记做 4,注意有两个pubdate,但我们都保存在item的pubdate成员变量中
    final int RSS_STATUS_GUID = 5;
    final int RSS_STATUS_DC_CREATOR = 6;
    final int RSS_STATUS_DC_DATE = 7;
    final int RSS_STATUS_SLASH_DEPARTMENT = 8;

    public ArrayList<RSSItem> getParsedData() {//這個是我們自己寫的，沒有一定要覆寫
        return li;
    }

    @Override
    public void startDocument() throws SAXException {
        super.star
        li = new ArrayList<RSSItem>();//在程式解析之初，先建立一個ArrayList容器
    }

    @Override
    public void endDocument() throws SAXException {
        //这个方法在整个xml文档解析结束时执行,一般需要在该方法中返回或保存整个文档解析解析结果,但由于
        //我们已经在解析过程中把结果保持在rssFeed中,所以这里什么也不做
        super.endDocument();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        //这个方法在解析标签开始标记时执行,一般我们需要在该方法取得标签属性值,但由于我们的rss文档
        //中并没有任何我们关心的标签属性,因此我们主要在这里进行的是设置标记变量currentstate,以
        //标记我们处理到哪个标签
        item = new RSSItem();//在程式解析之初，先產生實體

        if (localName.equals("channel")) {
            //channel这个标签没有任何值得我们关心的内容，所以currentstate置为0
            currentstate = 0;
            return;
        }
        if (localName.equals("item")) {
            //若是item标签,则重新构造一个RSSItem,从而把已有(已经解析过的)item数据扔掉,当
            //然事先是已经保存到rssFeed的itemlist集合中了
            return;
        }
        if (localName.equals("title")) {
            //若是title标签,置currentstate为1,表明这是我们关心的数据,这样在characters
            //方法中会把元素内容保存到rssItem变量中

            currentstate = RSS_STATUS_TITLE;
            return;
        }
        if (localName.equals("description")) {
            //若是description标签,置currentstate为3,表明这是我们关心的数据,这样在characters
            //方法中会把元素内容保存到rssItem变量中

            currentstate = RSS_STATUS_DESCRIPTION;
            return;
        }
        if (localName.equals("link")) {
            //若是link标签,置currentstate为2,表明这是我们关心的数据,这样在characters
            //方法中会把元素内容保存到rssItem变量中

            currentstate = RSS_STATUS_LINK;
            return;
        }
        if (localName.equals("pubDate")) {
            //若是pubDate标签,置currentstate为5,表明这是我们关心的数据,这样在characters
            //方法中会把元素内容保存到rssItem变量中

            currentstate = RSS_STATUS_PUBDATE;
            return;
        }
        if (localName.equals("guid")) {
            currentstate = RSS_STATUS_GUID;
            return;
        }
        if (localName.equals("dc:creator")) {
            currentstate = RSS_STATUS_DC_CREATOR;
            return;
        }
        if (localName.equals("dc:date")) {
            currentstate = RSS_STATUS_DC_DATE;
            return;
        }
        if (localName.equals("slash:department")) {
            currentstate = RSS_STATUS_SLASH_DEPARTMENT;
            return;
        }
        currentstate = 0;//如果不是上面列出的任何标签,置currentstate为0,我们不关心
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        //如果解析一个item节点结束，就将rssItem添加到rssFeed中。
        if (localName.equals("item")) {
            li.add(item);
            return;
        }
        super.endElement(uri, localName, qName);
    }

    @Override  //取標籤內的字的方式
    public void characters(char ch[], int start, int length){
        //更多程式碼
        //这个方法在解析标签内容(即开始标记－结束标记之间的部分)时执行,一般我们在里这获取元素体内容
        String theString = new String(ch,start,length); //获取元素体内容
        switch (currentstate) {
            //根据currentstate标记判断这个元素体是属于我们关心的哪个元素
            case RSS_STATUS_TITLE:
                item.setTitle(theString);//若是title元素,放入rssItem的title属性
                currentstate = 0;
                break;
            case RSS_STATUS_LINK:
                item.setLink(theString);//若是link元素,放入rssItem的link属性
                currentstate = 0;
                break;
            case RSS_STATUS_DESCRIPTION:
                item.setDescription(theString);
                currentstate = 0;
                break;
            case RSS_STATUS_PUBDATE:
                item.setPubDate(theString);
                currentstate = 0;
                break;
            case RSS_STATUS_GUID:
                item.setPubDate(theString);
                currentstate = 0;
                break;
            case RSS_STATUS_DC_CREATOR:
                item.setPubDate(theString);
                currentstate = 0;
                break;
            case RSS_STATUS_DC_DATE:
                item.setPubDate(theString);
                currentstate = 0;
                break;
            case RSS_STATUS_SLASH_DEPARTMENT:
                item.setPubDate(theString);
                currentstate = 0;
                break;
            default:
                currentstate = 0;
                break;
        }
    }
}
