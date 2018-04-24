package com.example.android.newsapp_stage2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class News {

    private String mSectionName;
    private String mPillarName;
    private String mWebPublicationDate;
    private String mWebTitle;
    private String mWebUrl;
    private String articleDate;
    private String articleTime;
    private String arTime;
    private boolean isRecent;

    private static final String DATE_SEPARATOR = "T";

    //Constructor - order paramterers in constructor determined order of parsing information
    public News(String title, String date, String pillar, String section, String url) {
        mWebTitle = title;
        mWebPublicationDate = date;
        mPillarName = pillar;
        mSectionName = section;
        mWebUrl = url;
        parsePublicationDate(date);
        calculateTimeDifference();
    }

    //Helped comparation current time and article time
    public boolean isRecent() {
        return isRecent;
    }

    private void calculateTimeDifference() {

        //Set current Time and its format
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
        tf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String currentTime = tf.format(new Date());

        //Parsing String to Date
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        //Represent current The Guardian time (current time)
        Date date1 = null;
        try {
            date1 = format.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Represent article time
        Date date2 = null;
        try {
            date2 = format.parse(arTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Calculate difference UTC Time/Current time in Date format HH:MM
        long difference = date1.getTime() - date2.getTime();

        //If current time is less 10 minutes, set "isRecent" = it is HOT NEWS
        int i = (int) (date2.getTime());

        if (i > 0) {
            difference = date1.getTime() - date2.getTime();
        }
        if (difference < 600001 && difference > 0) {
            isRecent = true;
        } else {
            isRecent = false;
        }
    }

    public String getArticleDate() {
        return articleDate;
    }

    public String getArticleTime() {
        return articleTime;
    }

    private void parsePublicationDate(String publicationDate) {
        Date date = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        // Article date and time
        if (publicationDate.contains(DATE_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            // based on the " T " text. We expect an array of 2 Strings, where
            // the first String will be "datum doplň datum" and the second String will be "čas doplň čas".
            String[] parts = publicationDate.split(DATE_SEPARATOR);
            // Date should be "5km N " + " T " --> "5km N of"
            articleDate = "Date: " + parts[0];
            // Time should be "Cairo, Egypt"
            articleTime = "Time: " + parts[1].substring(0, 5);

            //This variable declaration is for comparsion current time and article time
            arTime = parts[1].substring(0, 5);

        } else {
            // Otherwise, there is no " T " text in the articleDateTime string.
            // Hence, set the default articleDateTime.
            articleDate = publicationDate;
            // The date will be the full location string "Pacific-Antarctic Ridge".
            articleTime = "";
        }

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
