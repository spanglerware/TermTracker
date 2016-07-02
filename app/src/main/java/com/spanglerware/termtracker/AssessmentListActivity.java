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

public class AssessmentListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Assessment> assessments;
    private long courseId;
    private static final int ASSESS_DETAIL_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Assessment List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Assessment List", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId", Course.NEW_ENTRY);

        assessments = new ArrayList<>();
        loadAssessments();

        int arraySize = assessments.size();
        String[] tempArray = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            tempArray[i] = assessments.get(i).getAssessmentTitle();
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tempArray));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                Intent assessIntent = new Intent(AssessmentListActivity.this,
                        AssessmentDetailActivity.class);
                assessIntent.putExtra("assessId",
                        assessments.get(i).getAssessmentId());
                assessIntent.putExtra("courseId", courseId);
                startActivity(assessIntent);
            }
        });

        Log.d("Assessment List", "onCreate, courseid = " + courseId);
    } //end of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASSESS_DETAIL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //impl
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

    private void loadAssessments() {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        Cursor cursor = dbUtil.getAssessments(courseId);
        Assessment assessment;

        //Toast.makeText(this, "cursor count = " +
        //        cursor.getCount(), Toast.LENGTH_SHORT).show();

        while (cursor.moveToNext()) {
            assessment = new Assessment();
            assessment.setAssessmentId(cursor.getLong(
                    DatabaseUtil.COLUMN_ROW_ID));
            assessment.setAssessmentTitle(cursor.getString(
                    DatabaseUtil.COLUMN_ASSESS_TITLE));
            assessment.setDueDate(TimeUtil.millisToDate(
                    cursor.getLong(DatabaseUtil.COLUMN_ASSESS_DUE)));
            assessment.setAssessmentType(AssessmentType.valueOf(
                    cursor.getString(DatabaseUtil.COLUMN_ASSESS_TYPE)));
            assessment.setNotes(" ");
            assessment.setCourseId(courseId);
            assessments.add(assessment);
        }

        cursor.close();
        dbUtil.close();
    } //end of loadAssessments

    private void addAssessment() {
        Intent assessIntent = new Intent(this,
                AssessmentDetailActivity.class);
        assessIntent.putExtra("assessId", Assessment.NEW_ENTRY);
        assessIntent.putExtra("courseId", courseId);
        startActivity(assessIntent);
    }

} //end of AssessmentListActivity class
