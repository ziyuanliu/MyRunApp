package me.ziyuanliu.myruns.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import android.text.format.DateFormat;
import java.util.Calendar;

import me.ziyuanliu.myruns.ManualExerciseActivity;

/**
 * Created by ziyuanliu on 4/11/16.
 * s
 */
public class DateDialogPicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        ManualExerciseActivity.cal.set(year, monthOfYear, dayOfMonth);
    }
}