package me.ziyuanliu.myruns;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // XD: If set to true then when your layout is inflated it will be automatically added to the view hierarchy
        // of the ViewGroup specified in the 2nd parameter as a child.
        //XD:if set to false, they are not added as direct children of the parent and
        // the parent doesn't recieve any touch events from the views.
        return inflater.inflate(R.layout.fragment_history, container, false);

    }
}

