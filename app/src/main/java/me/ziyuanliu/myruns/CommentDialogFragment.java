package me.ziyuanliu.myruns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CommentDialogFragment extends DialogFragment {
    private static final String COMMENT_DIALOGUE_FRAGMENT = "COMMENT_DIALOGUE_FRAGMENT";

    private Fragment parentFrag;
    public static CommentDialogFragment newInstance(int title, Fragment parentFrag) {
        CommentDialogFragment frag = new CommentDialogFragment();
        frag.parentFrag = parentFrag;
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
        final EditText input = new EditText(getActivity());

        builder.setView(input);

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((SettingsFragment) parentFrag).doNegativeClick(COMMENT_DIALOGUE_FRAGMENT);
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((SettingsFragment) parentFrag).doNegativeClick(COMMENT_DIALOGUE_FRAGMENT);
                    }
                });
        Dialog d = builder.create();

        return d;

    }
}