package com.spanglerware.termtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private long courseId;
    private long termId;
    private Course course;

    private EditText notes;
    private ImageView photo;
    private Uri photoUri;
    private Button buttonPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Notes", "toolbar onclick");
                setResult(RESULT_OK);
                finish();
            }
        });

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId", Course.NEW_ENTRY);
        termId = intent.getLongExtra("termId", Term.NEW_ENTRY);
        if (courseId >= 0) {
            loadNotes();
        }

        loadUI();
    }

    private void loadNotes() {
        DatabaseUtil db = new DatabaseUtil(this);
        db.open();
        course = db.loadCourse(courseId, termId);
        db.close();
    }

    private void loadUI() {
        notes = (EditText) findViewById(R.id.notes);
        notes.setText(course.getCourseNotes());

        buttonPhoto = (Button) findViewById(R.id.buttonPhoto);

        photo = (ImageView) findViewById(R.id.photo);
        photoUri = course.getPhoto();
        if (photoUri == null || photoUri.toString() == "") {
            buttonPhoto.setText("Take Photo");
        } else {
            photo.setImageURI(photoUri);
            buttonPhoto.setText("Replace Photo");
        }
    }

    public void goSave(View view) {
        //impl
        course.setCourseNotes(notes.getText().toString());
        course.setPhoto(photoUri);

        DatabaseUtil dbUtil = new DatabaseUtil(this);
        dbUtil.open();
        long result = dbUtil.insertRow(course, false); //update record
        String msg = "";
        if (result >= 0) {
            msg = "Notes successfully saved";
        } else {
            msg = "Save failed";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        dbUtil.close();
    }

    public void goPhoto(View view) {
        //impl
        photoUri = getOutputUri();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void goShare(View view) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String emailAddress = shared.getString(SettingsActivity.KEY_PREF_EMAIL, "");
        //email notes
        String emailContent = "mailto:" + emailAddress +
                "?subject=" + Uri.encode("Term Tracker notes") +
                "&body=" + Uri.encode("from course: " +
                course.getCourseTitle() + "\nnotes: " +
                notes.getText().toString());

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(emailContent));

        startActivity(emailIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //photoUri = data.getData();
                Log.d("Notes Activity", "return from photo: " + photoUri.toString());
                photo.setImageURI(photoUri);
                photo.invalidate();
            }
        }

        //need to update database with image file

    }

    private Uri getOutputUri() {
        //get or create picture directory
        File dataDirectory = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "TermTracker");
/*
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                Log.d("Notes Activity", "directory creation failure");
                return null;
            }
        }
*/
        //create file name using a current timestamp
        String timeStamp = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(
                new Date());
        File photoFile = new File(dataDirectory.getPath() +
                File.separator + "IMG_" + timeStamp + ".jpg");
        Log.d("Notes getOutputUri", "file: " + photoFile.toString());
        return Uri.fromFile(photoFile);
    }

} //end NotesActivity class

