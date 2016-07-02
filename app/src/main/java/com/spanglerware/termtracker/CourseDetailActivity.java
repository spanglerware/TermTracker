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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {
    private Course course;
    private boolean newCourse;

    private EditText courseTitle;
    private EditText courseStart;
    private EditText courseEnd;
    private EditText mentorName;
    private EditText mentorEmail;
    private EditText mentorPhone;
    private Spinner courseStatus;
    private long termId;
    private long courseId;
    private static final int ASSESS_LIST_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Course Detail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Course Detail", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId", Course.NEW_ENTRY);
        termId = intent.getLongExtra("termId", Term.NEW_ENTRY);

        course = new Course(termId);
        if (courseId >= 0) {
            loadCourseDetails(courseId);
            newCourse = false;
        } else {
            newCourse = true;
        }
        loadUI();

        Log.d("Course Detail", "onCreate, courseId = " + courseId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASSESS_LIST_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //todo reload UI?
            }
        }
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
                addCourse();
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

    private void loadCourseDetails(long courseId) {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        course = dbUtil.loadCourse(courseId, termId);
        dbUtil.close();
    }

    private void loadUI() {
        String date;

        courseTitle = (EditText) findViewById(R.id.courseTitle);
        courseTitle.setText(course.getCourseTitle());

        courseStart = (EditText) findViewById(R.id.courseStart);
        date = TimeUtil.formatDate(course.getStartDate());
        courseStart.setText(date);

        courseEnd = (EditText) findViewById(R.id.courseEnd);
        date = TimeUtil.formatDate(course.getEndDate());
        courseEnd.setText(date);

        mentorName = (EditText) findViewById(R.id.courseMentorName);
        mentorName.setText(course.getMentorName());

        mentorEmail = (EditText) findViewById(R.id.courseMentorEmail);
        mentorEmail.setText(course.getMentorEmail());

        mentorPhone = (EditText) findViewById(R.id.courseMentorPhone);
        mentorPhone.setText(course.getMentorPhone());

        courseStatus = (Spinner) findViewById(R.id.courseStatus);
        loadSpinner();
    }

    public void goAssessmentList(View view) {
        if (newCourse) {
            Toast.makeText(this, "Please first save this new course",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent assessListIntent = new Intent(this, AssessmentListActivity.class);
        assessListIntent.putExtra("courseId", course.getCourseId());
        startActivity(assessListIntent);
    }

    public void goSave(View view) {
        course.setCourseTitle(courseTitle.getText().toString());
        course.setStartDate(TimeUtil.parseDate(courseStart.getText().toString()));
        course.setEndDate(TimeUtil.parseDate(courseEnd.getText().toString()));
        course.setMentorName(mentorName.getText().toString());
        course.setMentorEmail(mentorEmail.getText().toString());
        course.setMentorPhone(mentorPhone.getText().toString());
        course.setCourseStatus(CourseStatus.valueOf((String)
                courseStatus.getSelectedItem()));

        //save to database, show toast message
        //insert if new, otherwise update
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        long result = dbUtil.insertRow(course, newCourse);
        String msg = "";
        if (result >= 0) {
            msg = "Course successfully saved";
            course.setCourseId(result);
            newCourse = false;
        } else {
            msg = "Save failed";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    public void goDelete(View view) {
        String msg = "";
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();

        Cursor assessments = dbUtil.getAssessments(courseId);
        if (assessments.getCount() > 0) {
            msg = "Not able to delete, Course contains assessments";
        } else {
            long result = dbUtil.deleteRow(DatabaseUtil.TABLE_COURSES,
                    course.getCourseId());
            if (result >= 0) {
                msg = "Course successfully deleted, resetting to default values";
                //reset views to default values
                addCourse();
            } else {
                msg = "Delete failed";
            }
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    public void goStartAlert(View view) {
        //set alert for course start date
        goAlert(course.getStartDate());
    }

    public void goEndAlert(View view) {
        //set alert for course end date
        goAlert(course.getEndDate());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void goAlert(Date dueDate) {
        //set a notification using the course date
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        int notifTime = Integer.parseInt(shared.getString(SettingsActivity.KEY_NOTIF_TIME, "1"));

        //calculate alarm time based on due date, account for the preference time delay,
        // and adjust to 12pm from 12am
        long alarmTime = dueDate.getTime() - ((long) notifTime * 86400000) + 43200000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        String msgDate = TimeUtil.formatDate(dueDate);
        String msg = "Alert: " + course.getCourseTitle() +
                " on " + msgDate;
        alarmIntent.putExtra("message", msg);

        //use course id for unique broadcast id
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) courseId,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

        Toast.makeText(this, "Alert has been set", Toast.LENGTH_SHORT).show();
    }

    public void goNotes(View view) {
        Intent notesIntent = new Intent(this, NotesActivity.class);
        notesIntent.putExtra("courseId", course.getCourseId());
        notesIntent.putExtra("termId", termId);

        startActivity(notesIntent);
    }

    private void addCourse() {
        newCourse = true;
        course = new Course(termId);
        loadUI();
    }

    private void loadSpinner() {
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add(CourseStatus.IN_PROGRESS.name());
        spinnerList.add(CourseStatus.COMPLETED.name());
        spinnerList.add(CourseStatus.DROPPED.name());
        spinnerList.add(CourseStatus.PLANNED.name());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        courseStatus.setAdapter(spinnerAdapter);
        courseStatus.setSelection(course.getCourseStatus().ordinal());
        //todo select spinner item based on database, may need to impl enum id
    }

} //end of CourseDetailActivity class
