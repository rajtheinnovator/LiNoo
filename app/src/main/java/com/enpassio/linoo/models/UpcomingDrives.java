package com.enpassio.linoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

//parcelable created using: http://www.parcelabler.com/
public class UpcomingDrives implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UpcomingDrives> CREATOR = new Parcelable.Creator<UpcomingDrives>() {
        @Override
        public UpcomingDrives createFromParcel(Parcel in) {
            return new UpcomingDrives(in);
        }

        @Override
        public UpcomingDrives[] newArray(int size) {
            return new UpcomingDrives[size];
        }
    };
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

    protected UpcomingDrives(Parcel in) {
        driveDate = in.readString();
        place = in.readString();
        detailedDescription = in.readString();
        jobPosition = in.readString();
        companyName = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(driveDate);
        dest.writeString(place);
        dest.writeString(detailedDescription);
        dest.writeString(jobPosition);
        dest.writeString(companyName);
    }
}