package com.example.android.newsapp;

public class News {

    public String mSectionName;
    public String mPillarName;
    public String mWebPublicationDate;
    public String mWebTitle;
    public String mWebUrl;

    //Pořadí parametrů v objektu pak určuje jak budou parsovány informace
    public News(String title, String date, String pillar, String section, String url) {
        mWebTitle = title;
        mWebPublicationDate = date;
        mPillarName = pillar;
        mSectionName = section;
        mWebUrl = url;
    }

    public String getmWebTitle() {
        return mWebTitle;
    }

    public String getmWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getmPillarName() {
        return mPillarName;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }
}
