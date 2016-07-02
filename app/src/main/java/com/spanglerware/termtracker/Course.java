package com.spanglerware.termtracker;

import android.database.Cursor;
import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Scott on 3/8/2016.
 */
public class Course {
    public static final long NEW_ENTRY = -1;
    private String courseTitle;
    private Date startDate;
    private Date endDate;
    private CourseStatus courseStatus;
    private String mentorName;
    private String mentorEmail;
    private String mentorPhone;
    private String courseNotes;
    private long termId;
    private long courseId;
    private Uri photo;

    private ArrayList<Assessment> assessments;

    public Course() {
        //set defaults
        setCourseTitle("Course ");
        setStartDate(new Date());
        setEndDate(new Date());
        setMentorName("Mentor");
        setMentorPhone("");
        setMentorEmail("");
        setCourseStatus(CourseStatus.PLANNED);
        setCourseNotes("");
        setCourseId(NEW_ENTRY);
        setPhoto(null);
    }

    public Course(long termId) {
        this();
        setTermId(termId);
    }

    public Course(String title, Date start, Date end, String name, String email,
                  CourseStatus status, String phone, String notes, long termId,
                  long courseId, Uri photo) {
        setCourseTitle(title);
        setStartDate(start);
        setEndDate(end);
        setCourseStatus(status);
        setMentorName(name);
        setMentorEmail(email);
        setMentorPhone(phone);
        setCourseNotes(notes);
        setTermId(termId);
        //course id set to -1 for a new entry, otherwise read from database
        setCourseId(courseId);
        setPhoto(photo);

        assessments = new ArrayList<Assessment>();
    }

    public void addAssessment(Assessment assessment) {
        getAssessments().add(assessment);
    }

    public void removeAssessment(Assessment assessment) {
        getAssessments().remove(assessment);
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public String getMentorName() {
        return mentorName;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public String getMentorPhone() {
        return mentorPhone;
    }

    public String getCourseNotes() {
        return courseNotes;
    }

    public long getTermId() { return termId; }

    public long getCourseId() { return courseId; }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public void setMentorPhone(String mentorPhone) {
        this.mentorPhone = mentorPhone;
    }

    public void setCourseNotes(String courseNotes) {
        this.courseNotes = courseNotes;
    }

    public void setTermId(long termId) { this.termId = termId; }

    public void setCourseId(long courseId) { this.courseId = courseId; }

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

} //end of Course class
