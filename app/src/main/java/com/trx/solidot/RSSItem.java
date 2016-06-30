package com.trx.solidot;

/**
 * Created by TRX on 06/28/2016.
 */
public class RSSItem {

    private String id;
    private String title;
    private String link;
    private String description;
    private String pubDate;
    private String guid;
    private String dc_creator;
    private String dc_date;
    private String slash_department;


    public RSSItem () {

    }

    public RSSItem(String title, String link, String description, String pubDate, String guid, String dc_creator, String dc_date, String slash_department) {
        this.dc_creator = dc_creator;
        this.dc_date = dc_date;
        this.description = description;
        this.guid = guid;
        this.link = link;
        this.pubDate = pubDate;
        this.slash_department = slash_department;
        this.title = title;
    }

    public void setDc_creator(String dc_creator) {
        this.dc_creator = dc_creator;
    }

    public void setDc_date(String dc_date) {
        this.dc_date = dc_date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setSlash_department(String slash_department) {
        this.slash_department = slash_department;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDc_creator() {
        return dc_creator;
    }

    public String getDc_date() {
        return dc_date;
    }

    public String getDescription() {
        return description;
    }

    public String getGuid() {
        return guid;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getSlash_department() {
        return slash_department;
    }

    public String getTitle() {
        return title;
    }
}

