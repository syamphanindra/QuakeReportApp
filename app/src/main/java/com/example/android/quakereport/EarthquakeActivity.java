package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;

public class EarthquakeActivity extends AppCompatActivity  implements LoaderCallbacks<List<Earthquake>> {
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mAdapter;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    TextView mEmptyStateTextView;
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);



        ListView earthquakeListView = (ListView) findViewById(R.id.list);


        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());


        earthquakeListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);



        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Earthquake currentEarthquake = mAdapter.getItem(position);


                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());


                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);


                startActivity(websiteIntent);
            }
        });

    }




    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {


        @Override
        protected List<Earthquake> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Earthquake> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Earthquake> data) {

            mAdapter.clear();


            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);


        mEmptyStateTextView.setText(R.string.no_earthquakes);


        mAdapter.clear();


        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        mAdapter.clear();
    }

}
