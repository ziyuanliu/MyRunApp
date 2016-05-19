package me.ziyuanliu.myruns;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;
import me.ziyuanliu.myruns.fragment.HistoryFragment;

/**
 * Created by Varun on 2/18/16.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String mId = extras.getString("message");
                long rowId = Long.valueOf(mId);
                ExerciseEntryDatasource datasource = new ExerciseEntryDatasource(getApplicationContext());
                datasource.open();
                datasource.deleteExerciseEntry(rowId);
                showToast(extras.getString("message")+" deleted");

                Intent in = new Intent("refresh.intent");

                //put whatever data you want to send, if any
                in.putExtra("refresh.intent", "");
                in.setClass(this, HistoryFragment.class);
                datasource.close();

                //send broadcast
                sendBroadcast(intent);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}