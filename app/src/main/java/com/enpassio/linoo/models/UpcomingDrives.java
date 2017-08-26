package com.enpassio.linoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

//parcelable created using: http://www.parcelabler.com/

public class UpcomingDrives implements Parcelable {
    private String mDriveDate;
    private String mDriveSummary;

    protected UpcomingDrives(Parcel in) {
        mDriveDate = in.readString();
        mDriveSummary = in.readString();
    }

    public UpcomingDrives(String driveDate, String driveSummary) {
        mDriveDate = driveDate;
        mDriveSummary = driveSummary;

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
}