package com.spanglerware.termtracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TermDetailActivity extends AppCompatActivity {
    private Term term;
    long termId;
    private EditText termTitle;
    private EditText termStart;
    private EditText termEnd;
    private boolean newTerm;
    private static final int COURSE_LIST_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Term Detail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Term Detail", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId", Term.NEW_ENTRY);

        term = new Term();
        if (termId >= 0) {
            loadTermDetails(termId);
            newTerm = false;
        } else {
            newTerm = true;
        }
        loadUI();

        Log.d("Term Detail", "onCreate, termid = " + termId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COURSE_LIST_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //may need to implement in future version
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
                addTerm();
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

    private void loadTermDetails(long id) {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        Cursor cursor = dbUtil.getTerm(id);
        cursor.moveToFirst();
        term.setTermId(id);
        term.setTermTitle(cursor.getString(DatabaseUtil.COLUMN_TERM_TITLE));
        term.setStartDate(TimeUtil.millisToDate(
                cursor.getLong(DatabaseUtil.COLUMN_TERM_START)));
        term.setEndDate(TimeUtil.millisToDate(
                cursor.getLong(DatabaseUtil.COLUMN_TERM_END)));
        cursor.close();
        dbUtil.close();
    }

    private void loadUI() {
        String date;

        termTitle = (EditText) findViewById(R.id.textTitle);
        termTitle.setText(term.getTermTitle());

        termStart = (EditText) findViewById(R.id.textStart);
        date = TimeUtil.formatDate(term.getStartDate());
        termStart.setText(date);

        termEnd = (EditText) findViewById(R.id.textEnd);
        date = TimeUtil.formatDate(term.getEndDate());
        termEnd.setText(date);
    }

    public void goCourseList(View view) {
        if (newTerm) {
            Toast.makeText(this, "Please first save this new term",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent courseListIntent = new Intent(this, CourseListActivity.class);
        courseListIntent.putExtra("termId", term.getTermId());
        startActivity(courseListIntent);
    }

    public void goSave(View view) {
        term.setTermTitle(termTitle.getText().toString());
        term.setStartDate(TimeUtil.parseDate(termStart.getText().toString()));
        term.setEndDate(TimeUtil.parseDate(termEnd.getText().toString()));

        //save to database, show toast message
        //insert if new, otherwise update
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        long result = dbUtil.insertRow(term, newTerm);
        String msg = "";
        if (result >= 0) {
            msg = "Term successfully saved";
            term.setTermId(result);
            newTerm = false;
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

        Cursor courses = dbUtil.getCourses(termId);
        if (courses.getCount() > 0) {
            msg = "Not able to delete, Term contains courses";
        } else {
            long result = dbUtil.deleteRow(DatabaseUtil.TABLE_TERMS, term.getTermId());

            if (result >= 0) {
                msg = "Term successfully deleted, resetting to default values";
                //reset views to default values
                addTerm();
            } else {
                msg = "Delete failed";
            }
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    private void addTerm() {
        newTerm = true;
        term = new Term();
        loadUI();
    }

} //end of TermDetailActivity class
