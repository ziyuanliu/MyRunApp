package me.ziyuanliu.myruns.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import me.ziyuanliu.myruns.R;
import me.ziyuanliu.myruns.SettingsActivity;

public class UnitDialogFragment extends DialogFragment {
    private static final String UNIT_DIALOGUE_FRAGMENT = "UNIT_DIALOGUE_FRAGMENT";
    private Fragment parentFrag;
    private static SharedPreferences pref;

    public static UnitDialogFragment newInstance(int title, Fragment parentFrag) {
        UnitDialogFragment frag = new UnitDialogFragment();
        frag.parentFrag = parentFrag;
        pref = parentFrag.getActivity().getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    //XD: onCreateDialog() is called after .show()
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        List<String> choices = Arrays.asList(getResources().getStringArray(R.array.system_unit_type));
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.system_unit_type), itemChoice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE, item);
                editor.commit();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        ((SettingsFragment) parentFrag).doNegativeClick(UNIT_DIALOGUE_FRAGMENT);
                    }
                });
        Dialog d = builder.create();

        return d;

    }
}