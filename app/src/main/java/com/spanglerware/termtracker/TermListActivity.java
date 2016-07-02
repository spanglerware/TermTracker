package com.spanglerware.termtracker;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class TermListActivity extends AppCompatActivity {
    private ArrayList<Term> terms;
    private ListView listView;
    private static final int TERM_DETAIL_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Term List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Term List", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        terms = new ArrayList<>();
        loadTerms();

        int arraySize = terms.size();
        //todo double check array init size
        String[] tempArray = new String[arraySize];
        for (int i = 0; i < arraySize; i++) {
            tempArray[i] = terms.get(i).getTermTitle();
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, tempArray));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TermListActivity.this,
                        TermDetailActivity.class);
                intent.putExtra("termId", terms.get(i).getTermId());
                startActivityForResult(intent, TERM_DETAIL_REQUEST_CODE);
            }
        });

        Log.d("Term List", "onCreate");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TERM_DETAIL_REQUEST_CODE) {
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

    private void loadTerms() {
        //load array from database
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        Cursor cursor = dbUtil.getTerms();
        Term term;

        while (cursor.moveToNext()) {
            term = new Term();
            term.setTermId(cursor.getLong(DatabaseUtil.COLUMN_ROW_ID));
            term.setTermTitle(cursor.getString(DatabaseUtil.COLUMN_TERM_TITLE));
            term.setStartDate(TimeUtil.millisToDate(
                    cursor.getInt(DatabaseUtil.COLUMN_TERM_START)));
            term.setEndDate(TimeUtil.millisToDate(
                    cursor.getInt(DatabaseUtil.COLUMN_TERM_END)));
            terms.add(term);
        }

        cursor.close();
        dbUtil.close();
    }

    private void addTerm() {
        Intent intent = new Intent(this,
                TermDetailActivity.class);
        intent.putExtra("termId", Term.NEW_ENTRY);
        startActivity(intent);
    }

} //end of TermListActivity class
