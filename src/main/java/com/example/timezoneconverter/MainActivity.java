package com.example.timezoneconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private Spinner sourceSpinner;
    private Spinner targetSpinner;
    private TimePicker timePicker;
    private TextView resultTextView;
    private Button convertButton;
    private List<String> timeZoneIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        sourceSpinner = findViewById(R.id.sourceSpinner);
        targetSpinner = findViewById(R.id.targetSpinner);
        timePicker = findViewById(R.id.timePicker);
        resultTextView = findViewById(R.id.resultTextView);
        convertButton = findViewById(R.id.convertButton);

        // Set up time picker
        timePicker.setIs24HourView(true);

        // Initialize timezone list
        initializeTimeZoneList();

        // Set up spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getFormattedTimeZoneList()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sourceSpinner.setAdapter(adapter);
        targetSpinner.setAdapter(adapter);

        // Set default selections to user's timezone and UTC
        String localTimeZone = TimeZone.getDefault().getID();
        int localIndex = timeZoneIds.indexOf(localTimeZone);
        int utcIndex = timeZoneIds.indexOf("UTC");

        sourceSpinner.setSelection(localIndex != -1 ? localIndex : 0);
        targetSpinner.setSelection(utcIndex != -1 ? utcIndex : 0);

        // Set up convert button click listener
        convertButton.setOnClickListener(v -> convertTime());
    }

    private void initializeTimeZoneList() {
        timeZoneIds = new ArrayList<>();
        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            TimeZone tz = TimeZone.getTimeZone(id);
            if (id.length() > 3 && !timeZoneIds.contains(id)) {
                timeZoneIds.add(id);
            }
        }
        Collections.sort(timeZoneIds);
    }

    private List<String> getFormattedTimeZoneList() {
        List<String> formattedList = new ArrayList<>();
        for (String id : timeZoneIds) {
            TimeZone tz = TimeZone.getTimeZone(id);
            int offset = tz.getRawOffset() / 3600000;
            String sign = offset >= 0 ? "+" : "";
            formattedList.add(String.format("%s (GMT%s%d:00)", id, sign, offset));
        }
        return formattedList;
    }

    private void convertTime() {
        // Get selected timezones
        String sourceId = timeZoneIds.get(sourceSpinner.getSelectedItemPosition());
        String targetId = timeZoneIds.get(targetSpinner.getSelectedItemPosition());

        TimeZone sourceTimeZone = TimeZone.getTimeZone(sourceId);
        TimeZone targetTimeZone = TimeZone.getTimeZone(targetId);

        // Create calendar with source time
        Calendar calendar = Calendar.getInstance(sourceTimeZone);
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        // Convert to target timezone
        Date sourceDate = calendar.getTime();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm");
        sourceFormat.setTimeZone(sourceTimeZone);
        targetFormat.setTimeZone(targetTimeZone);

        String sourceDateStr = sourceFormat.format(sourceDate);
        String targetDateStr = targetFormat.format(sourceDate);

        // Display result
        String result = String.format(
                "When it's %s in %s\nIt's %s in %s",
                sourceDateStr,
                sourceId,
                targetDateStr,
                targetId
        );
        resultTextView.setText(result);
    }
}