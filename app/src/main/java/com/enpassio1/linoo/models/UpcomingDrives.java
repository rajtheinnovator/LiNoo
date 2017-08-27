package com.enpassio1.linoo.models;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

//parcelable created using: http://www.parcelabler.com/
public class UpcomingDrives {
    private String driveDate;
    private String place;
    private String detailedDescription;
    private String jobPosition;
    private String companyName;


    public UpcomingDrives() {
        //required empty constructor, as Firebase was complaining about this being not present
    }

    public UpcomingDrives(String name, String date, String location, String jobTitle, String description) {
        companyName = name;
        driveDate = date;
        place = location;
        detailedDescription = description;
        jobPosition = jobTitle;
    }

    public String getDriveDate() {
        return driveDate;
    }

    public String getPlace() {
        return place;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public String getCompanyName() {
        return companyName;
    }


}