package com.spanglerware.termtracker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.sql.Date;

public class MainActivity extends AppCompatActivity {
    public static final int COURSE_TYPE = 1;
    public static final int ASSESS_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean result;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Term Tracker");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_add);
        menu.removeItem(R.id.action_home);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        switch(menuId) {
            case R.id.action_load:
                loadTestData(null);
                break;
            case R.id.action_clear:
                clearDb(null);
                break;
            case R.id.action_settings:
                loadSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goTermList(View view) {
        Intent termListIntent = new Intent(this, TermListActivity.class);
        startActivity(termListIntent);
    }

    public void clearDb(View view) {
        DatabaseUtil db = new DatabaseUtil(this);
        db.open();

        db.clearData();
        db.close();
    }

    public void loadTestData(View view) {
        boolean result = insertTestData();

        Toast.makeText(this, "Database insert = " + result,
                Toast.LENGTH_SHORT).show();
    }

    private boolean insertTestData() {
        //insert
        long result = 0;
        DatabaseUtil db = new DatabaseUtil(this);
        db.open();

        Cursor cursor = db.getTerms();
        if (cursor.getCount() != 0) return false;

        Term term = new Term("term 1", Date.valueOf("2016-01-01"),
                Date.valueOf("2016-01-31"), Term.NEW_ENTRY);
        db.insertRow(term, true);
        term = new Term("term 2", Date.valueOf("2016-02-01"),
                Date.valueOf("2016-02-29"), Term.NEW_ENTRY);
        db.insertRow(term, true);

        Course course = new Course("db design", Date.valueOf("2016-01-01"),
                Date.valueOf("2016-01-07"), "Prof. Jones", "jones@example.com",
                CourseStatus.IN_PROGRESS, "555-1234", null, 1,
                Course.NEW_ENTRY, null);
        db.insertRow(course, true);
        course = new Course("android", Date.valueOf("2016-01-08"),
                Date.valueOf("2016-01-15"), "Prof. Scott", "scott@example.com",
                CourseStatus.PLANNED, "555-1234", null, 1,
                Course.NEW_ENTRY, null);
        db.insertRow(course, true);
        course = new Course("java", Date.valueOf("2016-02-01"),
                Date.valueOf("2016-02-07"), "Prof. Doe", "doe@example.com",
                CourseStatus.PLANNED, "555-1234", null, 2,
                Course.NEW_ENTRY, null);
        db.insertRow(course, true);

        Assessment assess = new Assessment("db obj", Date.valueOf("2016-01-07"),
                "notes 1", AssessmentType.OBJECTIVE, 1, Assessment.NEW_ENTRY,
                null);
        db.insertRow(assess, true);
        assess = new Assessment("db perf", Date.valueOf("2016-01-07"),
                "notes 2", AssessmentType.PERFORMANCE, 1, Assessment.NEW_ENTRY,
                null);
        db.insertRow(assess, true);
        assess = new Assessment("android obj", Date.valueOf("2016-01-15"),
                "notes 3", AssessmentType.OBJECTIVE, 2, Assessment.NEW_ENTRY,
                null);
        db.insertRow(assess, true);
        assess = new Assessment("java obj", Date.valueOf("2016-02-07"),
                "notes 4", AssessmentType.OBJECTIVE, 3, Assessment.NEW_ENTRY,
                null);
        db.insertRow(assess, true);

        db.close();
        return true;
    }

    private void loadSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

} //end of MainActivity
