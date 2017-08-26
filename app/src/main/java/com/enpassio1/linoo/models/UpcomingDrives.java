package com.enpassio1.linoo.models;

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
    private String mDriveDate;
    private String mDriveSummary;
    private String mPlace;
    private String mDetailedDescription;
    private String mJobPosition;

    protected UpcomingDrives(Parcel in) {
        mDriveDate = in.readString();
        mDriveSummary = in.readString();
    }
    public UpcomingDrives(String driveDate, String driveSummary) {
        mDriveDate = driveDate;
        mDriveSummary = driveSummary;

    }

    public UpcomingDrives(String driveDate, String driveSummary, String place, String position, String detailedDescription) {
        mDriveDate = driveDate;
        mDriveSummary = driveSummary;
        mPlace = place;
        mDetailedDescription = detailedDescription;
        mJobPosition = position;

    }

    public String getDriveDate() {
        return mDriveDate;
    }

    public String getDriveSummary() {
        return mDriveSummary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDriveDate);
        dest.writeString(mDriveSummary);
    }
}