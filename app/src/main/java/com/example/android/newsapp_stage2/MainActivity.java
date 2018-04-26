package com.example.android.newsapp_stage2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for news data from the The Guardian dataset
     */
    private static final String GUARDIEN_REQUEST_URL =
            "http://content.guardianapis.com";

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     * LoadManager for filter
     * Loadingindicator for filter
     * searchCategory for filter and change color
     * Toolbar - own solution
     */
    private TextView mEmptyStateTextView;
    private LoaderManager loaderManager;
    private View loadingIndicator;
    private String searchCategory;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Find the toolbar view inside the activity layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        //OverFlowIcon means 3 dots in default
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_filter_list));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Set current Date of device to layout tv_date
        // DateFormat tf = DateFormat.getDateInstance(DateFormat.LONG);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        final String currentDate = df.format(new Date());

        TextView dateCurrent = (TextView) findViewById(R.id.tv_date);
        dateCurrent.setText(getString(R.string.date) + " " + currentDate);

        //Set current time to Toolbar
        setCurrentTime();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News newsAdapter = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(newsAdapter.getmWebUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * When user use filter show loading indicator, load feed with his preferences
     * filterISChecked - change filter color when isChecked
     */
    @Override
    protected void onStart() {
        super.onStart();
        loadingIndicator.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(1, null, this);
        filterIsChecked();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        searchCategory = sharedPreferences.getString(
                getString(R.string.pick_category1),
                getString(R.string.all));
        searchCategory = parseCategory(searchCategory);

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIEN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //parts of URL which I used before filter: http://content.guardianapis.com/search?order-by=newest&api-key=test
        uriBuilder.appendPath("search")
                .appendQueryParameter("api-key", "test")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("show-tags", "contributor");
        if (!searchCategory.equals("")) {
            uriBuilder.appendQueryParameter("section", searchCategory);
        }

        // Create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    //parse category in filter with Guardian category
    private String parseCategory(String searchCategory) {
        switch (searchCategory) {
            case "All news":
                searchCategory = "";
                break;
            case "World news":
                searchCategory = "world";
                break;
            case "US news":
                searchCategory = "us-news";
                break;
            case "UK news":
                searchCategory = "uk-news";
                break;
            case "Business":
                searchCategory = "business";
                break;
            case "Science":
                searchCategory = "science";
                break;
            case "Society":
                searchCategory = "society";
                break;
            case "Sport":
                searchCategory = "sport";
                break;
            case "Art and design":
                searchCategory = "artanddesign";
                break;
            case "Technology":
                searchCategory = "technology";
                break;
            default:
                searchCategory = "";
        }
        return searchCategory;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);

            //Show update info
            Toast.makeText(MainActivity.this, "Added new articles",
                    Toast.LENGTH_LONG).show();

            //Update current time in Toolbar
            setCurrentTime();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //Set current time to Toolbar
    public void setCurrentTime() {
        //Take current time of device
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
        tf.setTimeZone(TimeZone.getDefault());
        final String currentTime = tf.format(new Date());

        //Take time of article
        DateFormat utc = DateFormat.getTimeInstance(DateFormat.SHORT);
        utc.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        String utcTime = utc.format(new Date());

        //Calculate difference UTC Time/Current time
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = null;
        try {
            date1 = format.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = format.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date1.getTime() - date2.getTime();

        //Set time difference in layout in tv_time
        DateFormat formatter = new SimpleDateFormat("H");
        String timeString = formatter.format(new Date(difference));

        TextView timeCurrent = (TextView) findViewById(R.id.tv_time);
        timeCurrent.setText(getString(R.string.time) + timeString + "/" + currentTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filterIsChecked() {
        if (searchCategory == "") {
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_filter_list));
        } else {
            Drawable drawable = toolbar.getOverflowIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), getResources().getColor(R.color.pink));
                toolbar.setOverflowIcon(drawable);
            }

        }
    }
}