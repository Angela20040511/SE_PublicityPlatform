package com.se.publicityplatform.vo;

public class StatsSummary {

    private int requestCount;
    private int taskCount;
    private int completedTaskCount;
    private int overdueTaskCount;
    private int revisionTaskCount;
    private int archivedAssetCount;

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(int completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public int getOverdueTaskCount() {
        return overdueTaskCount;
    }

    public void setOverdueTaskCount(int overdueTaskCount) {
        this.overdueTaskCount = overdueTaskCount;
    }

    public int getRevisionTaskCount() {
        return revisionTaskCount;
    }

    public void setRevisionTaskCount(int revisionTaskCount) {
        this.revisionTaskCount = revisionTaskCount;
    }

    public int getArchivedAssetCount() {
        return archivedAssetCount;
    }

    public void setArchivedAssetCount(int archivedAssetCount) {
        this.archivedAssetCount = archivedAssetCount;
    }

    public double getCompletionRate() {
        if (taskCount == 0) {
            return 0;
        }
        return completedTaskCount * 100.0 / taskCount;
    }
}
