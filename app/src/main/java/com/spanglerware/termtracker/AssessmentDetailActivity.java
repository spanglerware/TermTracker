package com.spanglerware.termtracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Date;

public class AssessmentDetailActivity extends AppCompatActivity {
    private Assessment assessment;
    private boolean newAssess;
    private long courseId;
    private long assessId;

    private EditText assessTitle;
    private EditText assessDue;
    private RadioGroup assessType;
    private RadioButton typePerf;
    private RadioButton typeObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Assessment Detail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Assessment Detail", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        assessId = intent.getLongExtra("assessId", Assessment.NEW_ENTRY);
        courseId = intent.getLongExtra("courseId", Course.NEW_ENTRY);

        assessment = new Assessment(courseId);
        if (assessId >= 0) {
            loadAssessDetails(assessId);
            newAssess = false;
        } else {
            newAssess = true;
        }
        loadUI();

        Log.d("Assessment Detail", "onCreate, assessid = " + assessId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_load);
        menu.removeItem(R.id.action_clear);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        switch(menuId) {
            case R.id.action_add:
                addAssessment();
                break;
            case R.id.action_home:
                Intent home = new Intent(this, MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAssessDetails(long assessId) {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        Cursor cursor = dbUtil.getAssessment(assessId);
        cursor.moveToFirst();

        assessment.setAssessmentId(assessId);
        assessment.setAssessmentTitle(cursor.getString(
                DatabaseUtil.COLUMN_ASSESS_TITLE));
        assessment.setDueDate(TimeUtil.millisToDate(
                cursor.getLong(DatabaseUtil.COLUMN_ASSESS_DUE)));
        assessment.setAssessmentType(AssessmentType.valueOf(
                cursor.getString(DatabaseUtil.COLUMN_ASSESS_TYPE)));

        cursor.close();
        dbUtil.close();
    }

    private void loadUI() {
        String date;

        assessTitle = (EditText) findViewById(R.id.assessTitle);
        assessTitle.setText(assessment.getAssessmentTitle());

        assessDue = (EditText) findViewById(R.id.assessDue);
        date = TimeUtil.formatDate(assessment.getDueDate());
        assessDue.setText(date);

        assessType = (RadioGroup) findViewById(R.id.assessType);
        typePerf = (RadioButton) findViewById(R.id.typePerf);
        typeObj = (RadioButton) findViewById(R.id.typeObj);
        if (assessment.getAssessmentType() == AssessmentType.PERFORMANCE) {
            typePerf.setSelected(true);
        } else {
            typeObj.setSelected(true);
        }

    } //end loadUI


    public void goSave(View view) {
        assessment.setAssessmentTitle(assessTitle.getText().toString());
        assessment.setDueDate(TimeUtil.parseDate(assessDue.getText().toString()));
        //todo set enum value from radio group
        if (typeObj.isSelected()) {
            assessment.setAssessmentType(AssessmentType.OBJECTIVE);
        } else {
            assessment.setAssessmentType(AssessmentType.PERFORMANCE);
        }

        //save to database, show toast message
        //insert if new, otherwise update
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        long result = dbUtil.insertRow(assessment, newAssess);
        String msg = "";
        if (result >= 0) {
            msg = "Assessment successfully saved";
            assessment.setAssessmentId(result);
            newAssess = false;
        } else {
            msg = "Save failed";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    public void goDelete(View view) {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        long result = dbUtil.deleteRow(DatabaseUtil.TABLE_ASSESSMENTS,
                assessment.getAssessmentId());
        String msg = "";
        if (result >= 0) {
            msg = "Assessment successfully deleted, resetting to default values";
            //reset views to default values
            addAssessment();
        } else {
            msg = "Delete failed";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void goAlert(View view) {
        //set a notification using the assessment date
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        int notifTime = Integer.parseInt(shared.getString(SettingsActivity.KEY_NOTIF_TIME, "1"));

        //calculate alarm time based on due date, account for the preference time delay,
        // and adjust to 12pm from 12am
        long alarmTime = assessment.getDueDate().getTime() - ((long) notifTime * 86400000) + 43200000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        String msgDate = TimeUtil.formatDate(assessment.getDueDate());
        String msg = "Alert: " + assessment.getAssessmentTitle() +
                " on " + msgDate;
        alarmIntent.putExtra("message", msg);

        //use assessment id for unique broadcast id
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) assessId,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

        Toast.makeText(this, "Alert has been set", Toast.LENGTH_SHORT).show();
    }

    private void addAssessment() {
        newAssess = true;
        assessment = new Assessment(courseId);
        loadUI();
    }

} //end of AssessmentDetailActivity class
