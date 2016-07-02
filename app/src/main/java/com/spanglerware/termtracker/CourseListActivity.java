package com.spanglerware.termtracker;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Course> courses;
    private long termId;
    private static final int COURSE_DETAIL_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Course List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Course List", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId", Term.NEW_ENTRY);

        courses = new ArrayList<>();
        loadCourses();

        int arraySize = courses.size();
        String[] tempArray = new String[arraySize];
        for (int i = 0; i < arraySize; i++) {
            tempArray[i] = courses.get(i).getCourseTitle();
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tempArray));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                Intent intent = new Intent(CourseListActivity.this,
                        CourseDetailActivity.class);
                intent.putExtra("courseId", courses.get(i).getCourseId());
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });

        Log.d("Course List", "onCreate, termid = " + termId);
    } //end of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COURSE_DETAIL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //todo refresh UI to see added terms?
                //todo action bar back button may not call finish
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

    private void loadCourses() {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        Cursor cursor = dbUtil.getCourses(termId);
        Course course;

        while (cursor.moveToNext()) {
            course = new Course();
            course.setCourseId(cursor.getLong(DatabaseUtil.COLUMN_ROW_ID));
            course.setCourseTitle(cursor.getString(DatabaseUtil.COLUMN_COURSE_TITLE));
            course.setStartDate(TimeUtil.millisToDate(
                    cursor.getInt(DatabaseUtil.COLUMN_COURSE_START)));
            course.setEndDate(TimeUtil.millisToDate(
                    cursor.getInt(DatabaseUtil.COLUMN_COURSE_END)));
            course.setMentorName(cursor.getString(DatabaseUtil.COLUMN_COURSE_NAME));
            course.setMentorEmail(cursor.getString(DatabaseUtil.COLUMN_COURSE_EMAIL));
            course.setMentorPhone(cursor.getString(DatabaseUtil.COLUMN_COURSE_PHONE));
            course.setCourseNotes(cursor.getString(DatabaseUtil.COLUMN_COURSE_NOTES));
            course.setCourseStatus(CourseStatus.valueOf(
                    cursor.getString(DatabaseUtil.COLUMN_COURSE_STATUS)));
            course.setTermId(termId);
            courses.add(course);
        }

        cursor.close();
        dbUtil.close();
    } //end of loadCourses

    private void addCourse() {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("courseId", Course.NEW_ENTRY);
        intent.putExtra("termId", termId);
        startActivity(intent);
    }

} //end of CourseListActivity
