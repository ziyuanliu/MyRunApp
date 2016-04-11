package me.ziyuanliu.myruns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CommentDialogFragment extends DialogFragment {
    private static final String COMMENT_DIALOGUE_FRAGMENT = "COMMENT_DIALOGUE_FRAGMENT";

    private int inputType;
    private String hint;
    public static CommentDialogFragment newInstance(int title, String hint, int inputType) {
        CommentDialogFragment frag = new CommentDialogFragment();
        frag.inputType = inputType;
        frag.hint = hint;
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
        input.setHint(hint);
        input.setInputType(inputType);
        builder.setView(input);

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        Dialog d = builder.create();

        return d;

    }
}