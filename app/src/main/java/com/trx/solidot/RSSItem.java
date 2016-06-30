package com.trx.solidot;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TRX on 06/28/2016.
 */
public class RSSItem implements Parcelable {

    private String id;
    private String title;
    private String link;
    private String description;
    private String pubDate;
    private String guid;
    private String dc_creator;
    private String dc_date;
    private String slash_department;

    private Context context;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(description);
        parcel.writeString(pubDate);
        parcel.writeString(guid);
        parcel.writeString(dc_creator);
        parcel.writeString(dc_date);
        parcel.writeString(slash_department);
    }

    public static final Parcelable.Creator<RSSItem> CREATOR = new Parcelable.Creator<RSSItem>() {
        public RSSItem createFromParcel(Parcel in) {
            return new RSSItem(in);
        }

        public RSSItem[] newArray(int size) {
            return new RSSItem[size];
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////////////


    public RSSItem (Context c) {
        context = c;
    }

    public RSSItem (Parcel parcel) {
        id = parcel.readString();
        title = parcel.readString();
        link = parcel.readString();
        description = parcel.readString();
        pubDate = parcel.readString();
        guid = parcel.readString();
        dc_creator = parcel.readString();
        dc_date = parcel.readString();
        slash_department = parcel.readString();
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
        if (!slash_department.trim().equals("")) {
            // do not change slash_department itself
            return context.getString(R.string.from) + slash_department + context.getString(R.string.department);
        }

        return slash_department;
    }

    public String getTitle() {
        return title;
    }


}

