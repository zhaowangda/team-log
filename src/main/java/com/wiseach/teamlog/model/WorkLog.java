package com.wiseach.teamlog.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Arlen Tan
 * 12-8-30 上午9:57
 */
public class WorkLog {
    private Long id;
    private String description,tags,completion;
    private Date startTime,endTime;
    private Long WorkTime;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCompletion() {
        return completion;
    }

    public void setCompletion(Long id) {
        this.completion = completion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
