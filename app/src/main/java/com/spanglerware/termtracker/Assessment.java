package com.spanglerware.termtracker;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Scott on 3/8/2016.
 */
public class Assessment {
    public static final long NEW_ENTRY = -1;
    private String assessmentTitle;
    private Date dueDate;
    private String notes;
    private AssessmentType assessmentType;
    private long courseId;
    private long assessmentId;
    private Uri photo;

    public Assessment() {
        //defaults
        setAssessmentTitle("");
        setDueDate(new Date());
        setNotes("");
        setAssessmentType(AssessmentType.OBJECTIVE);
        setAssessmentId(NEW_ENTRY);
        setPhoto(null);
    }

    public Assessment(long courseId) {
        this();
        setCourseId(courseId);
    }

    public Assessment(String title, Date due, String notes, AssessmentType type,
                      long courseId, long assessmentId, Uri photo) {
        setAssessmentTitle(title);
        setDueDate(due);
        setNotes(notes);
        setAssessmentType(type);
        //assess id set to -1 for new entry, otherwise read from database
        setAssessmentId(assessmentId);
        setCourseId(courseId);
        setPhoto(photo);
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Uri getPhoto() {
        return photo;
    }

    public String getPhotoString() {
        if (photo == null) return "";
        return photo.toString();
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }
}
