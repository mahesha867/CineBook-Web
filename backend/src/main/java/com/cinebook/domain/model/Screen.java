package com.cinebook.domain.model;

/**
 * OOP Entity: Screen.
 */
public class Screen {

    private Integer screenId;
    private String screenName;
    private int totalRows;
    private int seatsPerRow;

    public Screen() {
    }

    public Screen(Integer screenId, String screenName, int totalRows, int seatsPerRow) {
        this.screenId = screenId;
        this.screenName = screenName;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
    }

    public int getTotalCapacity() {
        return totalRows * seatsPerRow;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }
}

