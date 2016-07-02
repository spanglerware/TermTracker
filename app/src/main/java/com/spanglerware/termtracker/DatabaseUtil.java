package com.spanglerware.termtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Scott on 3/8/2016.
 */
public class DatabaseUtil {
    private static final String FIELD_ROWID = "_id";
    private static final String FIELD_TERM_ID = "term_id";
    private static final String FIELD_COURSE_ID = "course_id";
    private static final String FIELD_TERM = "term";
    private static final String FIELD_START = "start_date";
    private static final String FIELD_END = "end_date";
    private static final String FIELD_COURSE = "course";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_MENTOR_NAME = "mentor_name";
    private static final String FIELD_MENTOR_EMAIL = "mentor_email";
    private static final String FIELD_MENTOR_PHONE = "mentor_phone";
    private static final String FIELD_NOTES = "notes";
    private static final String FIELD_ASSESSMENT = "assessment";
    private static final String FIELD_DUE_DATE = "due_date";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_PHOTO = "photo";

    public static final int COLUMN_ROW_ID = 0;
    public static final int COLUMN_TERM_TITLE = 1;
    public static final int COLUMN_TERM_START = 2;
    public static final int COLUMN_TERM_END = 3;
    public static final int COLUMN_COURSE_TITLE = 1;
    public static final int COLUMN_COURSE_START = 2;
    public static final int COLUMN_COURSE_END = 3;
    public static final int COLUMN_COURSE_NAME = 4;
    public static final int COLUMN_COURSE_EMAIL = 5;
    public static final int COLUMN_COURSE_PHONE = 6;
    public static final int COLUMN_COURSE_NOTES = 7;
    public static final int COLUMN_COURSE_STATUS = 8;
    public static final int COLUMN_COURSE_TERMID = 9;
    public static final int COLUMN_COURSE_PHOTO = 10;
    public static final int COLUMN_ASSESS_TITLE = 1;
    public static final int COLUMN_ASSESS_DUE = 2;
    public static final int COLUMN_ASSESS_TYPE = 3;
    public static final int COLUMN_ASSESS_COURSEID = 4;
    public static final int COLUMN_ASSESS_NOTES = 5;
    public static final int COLUNM_ASSESS_PHOTO = 6;


    private static final String DATABASE_NAME = "dbTermTracker";
    public static final String TABLE_TERMS = "terms";
    public static final String TABLE_COURSES = "courses";
    public static final String TABLE_ASSESSMENTS = "assessments";
    private static final int DATABASE_VERSION = 1;

    //sql for creating tables
    private static final String CREATE_TERMS =
            "CREATE TABLE " + TABLE_TERMS + " (" + FIELD_ROWID +
            " INTEGER PRIMARY KEY, " + FIELD_TERM +
            " TEXT, " + FIELD_START + " INTEGER, " +
            FIELD_END + " INTEGER)";

    private static final String CREATE_COURSES =
            "CREATE TABLE " + TABLE_COURSES + " (" + FIELD_ROWID +
            " INTEGER PRIMARY KEY, " + FIELD_COURSE +
            " TEXT, " + FIELD_START + " INTEGER, " +
            FIELD_END + " INTEGER, " + FIELD_MENTOR_NAME +
            " TEXT, " + FIELD_MENTOR_EMAIL + " TEXT, " +
            FIELD_MENTOR_PHONE + " TEXT, " + FIELD_NOTES +
            " TEXT, " + FIELD_STATUS + " TEXT, " +
            FIELD_TERM_ID + " INTEGER, " + FIELD_PHOTO + " TEXT)";

    private static final String CREATE_ASSESSMENTS =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" + FIELD_ROWID +
            " INTEGER PRIMARY KEY, " + FIELD_ASSESSMENT +
            " TEXT, " + FIELD_DUE_DATE + " INTEGER, " +
            FIELD_TYPE + " TEXT, " + FIELD_COURSE_ID + " INTEGER, " +
            FIELD_NOTES + " TEXT, " + FIELD_PHOTO + " TEXT)";

    private static Context mContext;
    private MyDatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    public DatabaseUtil(Context context) {
        mContext = context;
        mDBHelper = new MyDatabaseHelper(mContext);
    }

    public DatabaseUtil() {
        mDBHelper = new MyDatabaseHelper(mContext);
    }

    public DatabaseUtil open() {
        mDb = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDBHelper.close();
    }

    public long insertRow(Object object, Boolean insert) {
        if (object instanceof Term) {
            //insert term
            return insertTerm((Term) object, insert);
        } else if (object instanceof Course) {
            //insert course
            return insertCourse((Course) object, insert);
        } else if (object instanceof Assessment) {
            //insert assessment
            return insertAssessment((Assessment) object, insert);
        }

        //otherwise return error
        return -1;
    }

    public long deleteRow(String table, long rowId) {
        //cascade delete rows associated with rowId except for Terms
        if (table == TABLE_TERMS) {
            //todo return values, exceptions
            return mDb.delete(TABLE_TERMS, FIELD_ROWID + " = " + rowId, null);
        } else if (table == TABLE_COURSES) {
            deleteAssessments(rowId);
            //todo return values, exceptions
            return mDb.delete(TABLE_COURSES, FIELD_ROWID + " = " + rowId, null);
        } else if (table == TABLE_ASSESSMENTS) {
            return deleteAssessment(rowId);
        }

        //todo return values, exceptions
        return -1;
    }

    public long clearData() {
        mDb.delete(TABLE_ASSESSMENTS, null, null);
        mDb.delete(TABLE_COURSES, null, null);
        mDb.delete(TABLE_TERMS, null, null);
        return 0;
    }

    private long deleteCourses(long termId) {
        Cursor cursor = getCourses(termId);
        long courseId = 0;
        for (int i = 0; i < cursor.getCount(); i++) {
            courseId = cursor.getLong(COLUMN_ROW_ID);
            deleteAssessments(courseId);
            cursor.moveToNext();
        }
        //todo return values, exceptions
        mDb.delete(TABLE_COURSES, FIELD_TERM_ID + " = " + termId, null);
        return 0;
    }

    private long deleteAssessments(long courseId) {
        //todo return values, exceptions
        mDb.delete(TABLE_ASSESSMENTS, FIELD_COURSE_ID + " = " + courseId, null);

        return 0;
    }

    private long deleteAssessment(long rowId) {
        return mDb.delete(TABLE_ASSESSMENTS, FIELD_ROWID + " = " + rowId, null);
    }

    public Cursor getTerms() {
        return mDb.query(TABLE_TERMS, null, null, null, null, null, null);
    }

    public Cursor getTerm(long termId) {
        return mDb.query(TABLE_TERMS, null, FIELD_ROWID + " = " + termId, null, null,
                null, null);
    }

    public Cursor getCourses(long termId) {
        return mDb.query(TABLE_COURSES, null, FIELD_TERM_ID + " = " + termId,
                null, null, null, null);
    }

    public Cursor getCourse(long courseId) {
        return mDb.query(TABLE_COURSES, null, FIELD_ROWID + " = " + courseId,
                null, null, null, null);
    }

    public Cursor getAssessments(long courseId) {
        return mDb.query(TABLE_ASSESSMENTS, null, FIELD_COURSE_ID +
                " = " + courseId, null, null, null, null);
    }

    public Cursor getAssessment(long assessId) {
        return mDb.query(TABLE_ASSESSMENTS, null, FIELD_ROWID +
                " = " + assessId, null, null, null, null);
    }

    private long insertTerm(Term term, Boolean insert) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TERM, term.getTermTitle());
        values.put(FIELD_START, term.getStartDate().getTime());
        values.put(FIELD_END, term.getEndDate().getTime());

        if (insert) {
            return mDb.insert(TABLE_TERMS, null, values);
        } else { //update
            String where = FIELD_ROWID + " = " + term.getTermId();
            return mDb.update(TABLE_TERMS, values, where, null);
        }
    }

    private long insertCourse(Course course, Boolean insert) {
        ContentValues values = new ContentValues();
        values.put(FIELD_COURSE, course.getCourseTitle());
        values.put(FIELD_START, course.getStartDate().getTime());
        values.put(FIELD_END, course.getEndDate().getTime());
        values.put(FIELD_STATUS, course.getCourseStatus().name());
        values.put(FIELD_MENTOR_NAME, course.getMentorName());
        values.put(FIELD_MENTOR_EMAIL, course.getMentorEmail());
        values.put(FIELD_MENTOR_PHONE, course.getMentorPhone());
        values.put(FIELD_NOTES, course.getCourseNotes());
        values.put(FIELD_TERM_ID, course.getTermId());
        values.put(FIELD_PHOTO, course.getPhotoString());

        if (insert) {
            return mDb.insert(TABLE_COURSES, null, values);
        } else { //update
            String where = FIELD_ROWID + " = " + course.getCourseId();
            return mDb.update(TABLE_COURSES, values, where, null);
        }
    }

    private long insertAssessment(Assessment assessment, Boolean insert) {
        ContentValues values = new ContentValues();
        values.put(FIELD_ASSESSMENT, assessment.getAssessmentTitle());
        values.put(FIELD_DUE_DATE, assessment.getDueDate().getTime());
        values.put(FIELD_TYPE, assessment.getAssessmentType().name());
        values.put(FIELD_COURSE_ID, assessment.getCourseId());
        values.put(FIELD_PHOTO, assessment.getPhotoString());
        values.put(FIELD_NOTES, assessment.getNotes());

        if (insert) {
            return mDb.insert(TABLE_ASSESSMENTS, null, values);
        } else { //update
            String where = FIELD_ROWID + " = " + assessment.getAssessmentId();
            return mDb.update(TABLE_ASSESSMENTS, values, where, null);
        }
    }

    public Course loadCourse(long courseId, long termId) {
        Course course = new Course(termId);
        Cursor cursor = getCourse(courseId);
        cursor.moveToFirst();

        course.setCourseId(courseId);
        course.setCourseTitle(cursor.getString(DatabaseUtil.COLUMN_COURSE_TITLE));
        course.setCourseTitle(cursor.getString(DatabaseUtil.COLUMN_COURSE_TITLE));
        course.setStartDate(TimeUtil.millisToDate(
                cursor.getLong(DatabaseUtil.COLUMN_COURSE_START)));
        course.setEndDate(TimeUtil.millisToDate(
                cursor.getLong(DatabaseUtil.COLUMN_COURSE_END)));
        course.setMentorName(cursor.getString(DatabaseUtil.COLUMN_COURSE_NAME));
        course.setMentorEmail(cursor.getString(DatabaseUtil.COLUMN_COURSE_EMAIL));
        course.setMentorPhone(cursor.getString(DatabaseUtil.COLUMN_COURSE_PHONE));
        course.setCourseNotes(cursor.getString(DatabaseUtil.COLUMN_COURSE_NOTES));
        course.setCourseStatus(CourseStatus.valueOf(
                cursor.getString(DatabaseUtil.COLUMN_COURSE_STATUS)));
        course.setPhoto(Uri.parse(cursor.getString(DatabaseUtil.COLUMN_COURSE_PHOTO)));

        cursor.close();

        return course;
    }

    private static class MyDatabaseHelper extends SQLiteOpenHelper {

        MyDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(CREATE_TERMS);
            _db.execSQL(CREATE_COURSES);
            _db.execSQL(CREATE_ASSESSMENTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
            _db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
            _db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        }
    } //end of MyDatabaseHelper private class

}//end of DatabaseUtil class
