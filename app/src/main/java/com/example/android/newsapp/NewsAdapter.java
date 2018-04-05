package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * The part of the date string from the Guardian service that we use to determine
     * which part is Date and which is Time.
     */
    private static final String DATE_SEPARATOR = "T";

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the new at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID is title
        TextView articleTitle = (TextView) listItemView.findViewById(R.id.tv_articleTitle);
        // Set title of news
        articleTitle.setText(currentNews.getmWebTitle());

        //Find information about date/time
        String articleDateTime = currentNews.getmWebPublicationDate();

// If the articleDateTime string ("doplnit celý string data a času, jak jej vraccí The Guradian") contains
        // a date (vypiš datum) and a time (vypiš čas)
        // then store the date separately from the articleDateTime in 2 Strings,
        // so they can be displayed in 2 TextViews.
        String articleDate;
        String articleTime;

        // Check whether the articleDateTime string contains the " T " text
        if (articleDateTime.contains(DATE_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            // based on the " T " text. We expect an array of 2 Strings, where
            // the first String will be "datum doplň datum" and the second String will be "čas doplň čas".
            String[] parts = articleDateTime.split(DATE_SEPARATOR);
            // Date should be "5km N " + " T " --> "5km N of"
            articleDate = "Date: " + parts[0];
            // Time should be "Cairo, Egypt"
            articleTime = "Time: " + parts[1].substring(0, 5);
        } else {
            // Otherwise, there is no " T " text in the articleDateTime string.
            // Hence, set the default articleDateTime.
            articleDate = articleDateTime;
            // The date will be the full location string "Pacific-Antarctic Ridge".
            articleTime = "";
        }

        // Find the TextView with view ID
        TextView articleDateView = (TextView) listItemView.findViewById(R.id.tv_articleDate);
        // Set text articleDate
        articleDateView.setText(articleDate);

        // Find the TextView with view ID
        TextView articleTimeView = (TextView) listItemView.findViewById(R.id.tv_articleTime);
        // Set text articleTime
        articleTimeView.setText(articleTime);

        // Find the TextView with view ID
        TextView sectionCategory = (TextView) listItemView.findViewById(R.id.tv_sectionCategory);
        // Set text PillarName
        sectionCategory.setText(currentNews.getmPillarName());

        // Find the TextView with view ID
        TextView sectionName = (TextView) listItemView.findViewById(R.id.tv_sectionName);
        // Set text SectionName
        sectionName.setText(currentNews.getmSectionName());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
}


