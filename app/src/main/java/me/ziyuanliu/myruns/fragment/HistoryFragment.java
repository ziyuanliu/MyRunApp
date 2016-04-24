package me.ziyuanliu.myruns.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import me.ziyuanliu.myruns.ExerciseEntryActivity;
import me.ziyuanliu.myruns.R;
import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ExerciseEntry>>{

    ListView entriesListView;
    ArrayAdapter<ExerciseEntry> listAdapter;
    ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<>();
    static ExerciseEntryDatasource datasource;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        datasource = new ExerciseEntryDatasource(getActivity());
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
                Intent intent = new Intent(getActivity(), ExerciseEntryActivity.class);
                ExerciseEntry exerciseEntry = exerciseEntries.get(position);
                intent.putExtra("rowId", exerciseEntry.getId());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public Loader<ArrayList<ExerciseEntry>> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

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
}

