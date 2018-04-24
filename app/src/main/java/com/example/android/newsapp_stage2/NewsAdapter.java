package com.example.android.newsapp_stage2;

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
    private Context context;

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
        this.context = context;
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

        // Find the TextView with view ID
        TextView articleDateView = (TextView) listItemView.findViewById(R.id.tv_articleDate);
        // Set text articleDate
        articleDateView.setText(currentNews.getArticleDate());

        // Find the TextView with view ID
        TextView articleTimeView = (TextView) listItemView.findViewById(R.id.tv_articleTime);
        // Set text articleTime
        articleTimeView.setText(currentNews.getArticleTime());

        if (currentNews.isRecent()) {
            articleTimeView.setText(currentNews.getArticleTime() + context.getString(R.string.last_news));
            //   articleTimeView.setAllCaps(true);
            //   articleTimeView.setTypeface(Typeface.DEFAULT_BOLD);
        }

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


