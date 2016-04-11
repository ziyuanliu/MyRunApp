package me.ziyuanliu.myruns;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends Activity {
    public static final String TAG = "SettingsActivity";

    // sharedpref keys
    public static final String PREF_KEYS_PF_URI = "PREF_KEYS_PF_URI";
    public static final String PREF_KEYS_IS_FROM_CAMERA = "PREF_KEYS_IS_FROM_CAMERA";
    public static final String PREF_KEYS_USER_DETAIL = "PREF_KEYS_USER_DETAIL";
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.verifyStoragePermissions(this);

        if (savedInstanceState!=null){
            imageUri = savedInstanceState.getParcelable(PREF_KEYS_PF_URI);
            isFromCamera = savedInstanceState.getBoolean(PREF_KEYS_IS_FROM_CAMERA, false);
        }

        initializeVars();
        fillInFields();
        loadSnap();
    }

    /*
    * inspiration from the lab 1 solution on logic
    * In here we do not use a third party cropper like soundcloud, so the development will overwrite
    * the original image when cropping*/
    private void loadSnap(){
        try{
            if (imageUri!=null && !Uri.EMPTY.equals(imageUri)) {
                imageView.setImageURI(imageUri);
            }else{
                FileInputStream f = openFileInput(getString(R.string.button_profile_photo_filename));
                Log.d("wtf", String.valueOf(f.getChannel().size()));
                Bitmap bmap = BitmapFactory.decodeStream(f);
                imageView.setImageBitmap(bmap);
                f.close();
            }

        }catch (IOException e) {
            // Default profile photo if no photo saved before.
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
    }

    /*
    * This saves the profile image from the image view*/
    public void saveProfileImage(){
        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.button_profile_photo_filename), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
    Let's save the profile now
     */
    public void saveProfile(View view){
        Editor editor = pref.edit();
        saveProfileImage();
        deleteImage(imageUri);
        editor.putString(PREF_KEYS_USER_NAME, nameET.getText().toString());
        editor.putString(PREF_KEYS_USER_EMAIL, emailET.getText().toString());
        editor.putString(PREF_KEYS_USER_PHONE, phoneET.getText().toString());
        editor.putString(PREF_KEYS_USER_GRAD_CLASS, gradClassET.getText().toString());
        editor.putString(PREF_KEYS_USER_MAJOR, majorET.getText().toString());
        editor.putBoolean(PREF_KEYS_USER_IS_MALE, femaleRB.isChecked() == false);
        editor.remove(PREF_KEYS_IS_FROM_CAMERA);

        editor.commit();
        Toast.makeText(this, R.string.profile_saved_text, Toast.LENGTH_LONG).show();

        finish();
    }

    public void deleteImage(Uri imageUri){
        if (isFromCamera){
            // don't tamper with gallery stuff
            File f = new File(imageUri.getPath());
            if (f.exists()){
                f.delete();
            }
        }
    }
    public void cancelChanges(View view){
        deleteImage(imageUri);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PREF_KEYS_PF_URI, imageUri);
        outState.putBoolean(PREF_KEYS_IS_FROM_CAMERA, isFromCamera);
    }
}
