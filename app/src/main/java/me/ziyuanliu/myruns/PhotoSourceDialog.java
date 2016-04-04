package me.ziyuanliu.myruns;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PhotoSourceDialog extends DialogFragment {
    // Dialog ID return statuses
    public static final int PHOTO_DIALOG_ID_ERROR = -1;
    public static final int PHOTO_DIALOG_ID_PHOTO_PICKER = 1;

    // for the choices
    public static final int PHOTO_ID_PICK_FROM_CAMERA = 0;
    public static final int PHOTO_ID_PICK_FROM_GALLERY = 1;

    private static final String PHOTO_DIALOG_ID_KEY = "photo_dialog_id";

    public static PhotoSourceDialog newInstance(int dialog_id) {
        PhotoSourceDialog frag = new PhotoSourceDialog();
        Bundle args = new Bundle();
        args.putInt(PHOTO_DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(PHOTO_DIALOG_ID_KEY);
        final Activity parent = getActivity();

        switch (dialog_id) {
            case PHOTO_DIALOG_ID_PHOTO_PICKER:
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ((MainActivity) parent).onPhotoSourceDialogSelected(item);
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
                return builder.create();
            default:
                return null;
        }
    }
}
