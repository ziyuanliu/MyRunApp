package me.ziyuanliu.myruns.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.ziyuanliu.myruns.ExerciseEntryActivity;
import me.ziyuanliu.myruns.MapExerciseEntryActivity;
import me.ziyuanliu.myruns.R;
import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

import com.example.ziyuanliu.myapplication.backend.registration.Registration;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;


public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ExerciseEntry>>{
    public static String SERVER_ADDR = "http://10.0.2.2:8080";
    private BroadcastReceiver mMessageReceiver;
    ListView entriesListView;
    ArrayAdapter<ExerciseEntry> listAdapter;
    ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<>();
    static ExerciseEntryDatasource datasource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        datasource = new ExerciseEntryDatasource(getActivity());
        new GcmRegistrationAsyncTask(getActivity()).execute();
        getLoaderManager().initLoader(0, null, this);
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        entriesListView = (ListView)view.findViewById(R.id.exercise_entries);
        listAdapter = new ArrayAdapter<ExerciseEntry>(getActivity(), R.layout.list_view_row, exerciseEntries){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row=inflater.inflate(R.layout.list_view_row, null);

                // set the item row
                TextView title = (TextView)row.findViewById(R.id.text_view_title);
                TextView subtitle = (TextView)row.findViewById(R.id.text_view_subtitle);
                CheckBox btn = (CheckBox) row.findViewById(R.id.text_view_checkbox);
                btn.setEnabled(false);
                btn.setVisibility(View.GONE);

                ExerciseEntry entry = exerciseEntries.get(position);
                String toStr = entry.toString();
                String[] splittedStr = toStr.split("\\$");
                title.setText(splittedStr[0]);
                subtitle.setText(splittedStr[1]);
                return row;
            }
        };
        entriesListView.setAdapter(listAdapter);
        entriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExerciseEntry exerciseEntry = exerciseEntries.get(position);

                Intent intent = new Intent(getActivity(), exerciseEntry.getmInputType() == 0 ? ExerciseEntryActivity.class : MapExerciseEntryActivity.class);
                intent.putExtra("rowId", exerciseEntry.getId());

                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * checks for visibility here
     * @param visible
     */
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            // if the view is visible reload the database onto the listview
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    //This is the handler that will manager to process the broadcast intent
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public Loader<ArrayList<ExerciseEntry>> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    /**
     * on load finish notifies the change, and the listAdapter is notified of the changes
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<ExerciseEntry>> loader, ArrayList<ExerciseEntry> data) {
        exerciseEntries.clear();
        for (ExerciseEntry entry: data){
            exerciseEntries.add(entry);
        }

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ExerciseEntry>> loader) {

    }

    /**
     * sets the operation in the background to fetch everything.
     */
    public static class DataLoader extends AsyncTaskLoader<ArrayList<ExerciseEntry>>{

        public DataLoader(Context context){
            super(context);
        }

        @Override
        public void onStartLoading(){
            forceLoad();
        }

        @Override
        public ArrayList<ExerciseEntry> loadInBackground() {
            datasource.open();
            ArrayList<ExerciseEntry> retval = (ArrayList<ExerciseEntry>) datasource.getAllEntries();
            datasource.close();
            return retval;
        }
    }

    /**
     * create the registration for GCM, code taken from demo as inspiration
     */
    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        private static final String SENDER_ID = "374255966973";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl(SERVER_ADDR + "/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                regService = builder.build();
            }

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;
                Log.d("TFD", msg);
                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                regService.register(regId).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
                Log.d("TFD", msg);

            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }
}

