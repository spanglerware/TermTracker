package com.spanglerware.termtracker;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Scott on 3/8/2016.
 */
public class Term {
    public static final long NEW_ENTRY = -1;
    private ArrayList<Course> courses;
    private String termTitle;
    private Date startDate;
    private Date endDate;
    private long termId;

    public Term() {
        //construct defaults
        setTermTitle("Term ");
        setStartDate(new Date());
        setEndDate(new Date());
        termId = 0;
    }

    public Term(String title, Date start, Date end, long termId) {
        termTitle = title;
        startDate = start;
        endDate = end;
        //term id set to -1 for a new entry, otherwise read from database
        this.termId = termId;

        courses = new ArrayList<Course>();
    }

    public void addCourse(Course course) {
        getCourses().add(course);
    }

    public void removeCourse(Course course) {
        //check for outstanding assessments in course, send alert if still pending
        getCourses().remove(course);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date start) {
        startDate = start;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date end) {
        //check end date is not before start date
        endDate = end;
    }

    public String getTermTitle() {
        return termTitle;
    }

    public void setTermTitle(String title) {
        termTitle = title;
    }

    public long getTermId() { return termId; }

    public void setTermId(long termId) {
        this.termId = termId;
    }


    public ArrayList<Course> getCourses() {
        return courses;
    }

}
