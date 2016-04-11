package me.ziyuanliu.myruns;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends Activity {
    public static final String TAG = "SettingsActivity";

    // sharedpref keys
    public static final String PREF_KEYS_LAST_IMG = "PREF_KEYS_LAST_IMG";
    public static final String PREF_KEYS_IS_FROM_CAMERA = "PREF_KEYS_IS_FROM_CAMERA";
    public static final String PREF_KEYS_USER_DETAIL = "PREF_KEYS_USER_DETAIL";
    public static final String PREF_KEYS_PROF_IMG = "PREF_KEYS_PROF_IMG";
    public static final String PREF_KEYS_USER_NAME = "PREF_KEYS_USER_NAME";
    public static final String PREF_KEYS_USER_EMAIL = "PREF_KEYS_USER_EMAIL";
    public static final String PREF_KEYS_USER_PHONE = "PREF_KEYS_USER_PHONE";
    public static final String PREF_KEYS_USER_IS_MALE = "PREF_KEYS_USER_GENDER";
    public static final String PREF_KEYS_USER_GRAD_CLASS = "PREF_KEYS_USER_GRAD_CLASS";
    public static final String PREF_KEYS_USER_MAJOR = "PREF_KEYS_USER_MAJOR";
    public static final String PREF_KEYS_USER_UNIT_TYPE = "PREF_KEYS_USER_UNIT_TYPE";
    public static final String PREF_KEYS_USER_ANON = "PREF_KEYS_USER_ANON";
    public static final String PREF_KEYS_USER_INPUT_TYPE = "PREF_KEYS_USER_INPUT_TYPE";
    public static final String PREF_KEYS_USER_ACTIVITY_TYPE = "PREF_KEYS_USER_ACTIVITY_TYPE";


    // keys copied from camera demo app for the lack of better naming :(
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private ImageView imageView;
    private Uri imageUri;
    private boolean isFromCamera;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    private SharedPreferences pref;

    // UI links
    private EditText nameET;
    private EditText emailET;
    private EditText phoneET;
    private RadioButton femaleRB;
    private RadioButton maleRB;
    private EditText gradClassET;
    private EditText majorET;

    /**
     * Checks if the app has permission to write to device storage
     * help from stackoverflow to deal with API 23
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void initializeVars(){
        imageView = (ImageView)findViewById(R.id.profileImageView);
        nameET = (EditText)findViewById(R.id.nameEditText);
        emailET = (EditText)findViewById(R.id.emailEditText);
        phoneET = (EditText)findViewById(R.id.phoneEditText);
        gradClassET = (EditText)findViewById(R.id.classEditText);
        majorET = (EditText)findViewById(R.id.majorEditText);
        femaleRB = (RadioButton)findViewById(R.id.femaleRadioButton);
        maleRB = (RadioButton)findViewById(R.id.maleRadioButton);

        pref = getSharedPreferences(PREF_KEYS_USER_DETAIL, MODE_PRIVATE);
    }

    private void fillInFields(){

        nameET.setText(pref.getString(PREF_KEYS_USER_NAME, ""));
        emailET.setText(pref.getString(PREF_KEYS_USER_EMAIL, ""));
        phoneET.setText(pref.getString(PREF_KEYS_USER_PHONE, ""));
        gradClassET.setText(pref.getString(PREF_KEYS_USER_EMAIL, ""));
        majorET.setText(pref.getString(PREF_KEYS_USER_MAJOR, ""));

        maleRB.setChecked(pref.getBoolean(PREF_KEYS_USER_IS_MALE, false));
        femaleRB.setChecked(!pref.getBoolean(PREF_KEYS_USER_IS_MALE, true));

        // better be safe than sorry
        isFromCamera = pref.getBoolean(PREF_KEYS_IS_FROM_CAMERA, false);
        loadSnap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.verifyStoragePermissions(this);

        if (savedInstanceState!=null){
            String lastImgStr = savedInstanceState.getString(PREF_KEYS_LAST_IMG, null);
            if (lastImgStr != null){
                imageUri = Uri.parse(lastImgStr);
            }

            isFromCamera = savedInstanceState.getBoolean(PREF_KEYS_IS_FROM_CAMERA, false);
        }

        initializeVars();
        fillInFields();

    }

    private void loadSnap(){
        if (imageUri!=null){
            imageView.setImageURI(imageUri);
        }else {
            imageView.setImageResource(R.drawable.questions);
        }
    }


    public void retakePhoto(View view){
        DialogFragment fragment = PhotoSourceDialog.newInstance(PhotoSourceDialog.PHOTO_DIALOG_ID_PHOTO_PICKER);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_photo_picker));
    }

    // handle return from other activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                sendToCrop();
                break;

            case REQUEST_CODE_CROP_PHOTO:
                // Update image view after image crop
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case REQUEST_CODE_TAKE_FROM_GALLERY:

                try {
                    imageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            default:

                break;

        }

        Editor editor = pref.edit();
        editor.commit();
    }

    private void sendToCrop() {
        // code taken from the camera demo app
        // Use existing crop activity.
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, IMAGE_UNSPECIFIED);

        // Specify image size
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        // Specify aspect ratio, 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        // REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
        // identify the activity in onActivityResult() when it returns
        startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);
    }

    public void onPhotoSourceDialogSelected(int item){
        // this is where we load the activity for the camera :)
        Intent intent;
        switch (item){
            case PhotoSourceDialog.PHOTO_ID_PICK_FROM_CAMERA:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String tempName = "temp_"+String.valueOf(System.currentTimeMillis())+".jpg";
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), tempName));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("return-data", true);

                try {
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                isFromCamera = true;
                break;

            case PhotoSourceDialog.PHOTO_ID_PICK_FROM_GALLERY:
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, REQUEST_CODE_TAKE_FROM_GALLERY);
                isFromCamera = false;

                break;
            default:
                break;
        }

        Editor editor = pref.edit();
        editor.putBoolean(PREF_KEYS_IS_FROM_CAMERA, isFromCamera);
        editor.commit();
    }

    private void goHome(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void saveProfile(View view){
        Editor editor = pref.edit();
        if (imageUri!=null){
            editor.putString(PREF_KEYS_PROF_IMG, imageUri.toString());
        }

        editor.putString(PREF_KEYS_USER_NAME, nameET.getText().toString());
        editor.putString(PREF_KEYS_USER_EMAIL, emailET.getText().toString());
        editor.putString(PREF_KEYS_USER_PHONE, phoneET.getText().toString());
        editor.putString(PREF_KEYS_USER_GRAD_CLASS, gradClassET.getText().toString());
        editor.putString(PREF_KEYS_USER_MAJOR, majorET.getText().toString());
        editor.putBoolean(PREF_KEYS_USER_IS_MALE, femaleRB.isChecked() == false);
        editor.remove(PREF_KEYS_IS_FROM_CAMERA);

        editor.commit();
        Toast.makeText(this, R.string.profile_saved_text, Toast.LENGTH_LONG).show();

        goHome();
    }

    public void cancelChanges(View view){
        Editor editor = pref.edit();

        // Delete temporary image taken by camera after crop.
        if (isFromCamera) {
            File f = new File(imageUri.getPath());
            if (f.exists())
                f.delete();
        }

        editor.commit();
        goHome();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri!=null) {
            outState.putString(PREF_KEYS_LAST_IMG, imageUri.toString());
        }

        if (isFromCamera){
            outState.putBoolean(PREF_KEYS_IS_FROM_CAMERA, isFromCamera);
        }
    }
}
