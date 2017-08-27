package com.enpassio1.linoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

//parcelable created using: http://www.parcelabler.com/
public class UpcomingDrives implements Parcelable {
    private String mDriveDate;
    private String mPlace;
    private String mDetailedDescription;
    private String mJobPosition;
    private String mCompanyName;

    public UpcomingDrives() {
        //required empty constructor, as Firebase was complaining about this being not present
    }

    public UpcomingDrives(String companyName, String driveDate, String place, String position, String detailedDescription) {
        mCompanyName = companyName;
        mDriveDate = driveDate;
        mPlace = place;
        mDetailedDescription = detailedDescription;
        mJobPosition = position;
    }

    public String getDriveDate() {
        return mDriveDate;
    }

    public String getPlace() {
        return mPlace;
    }

    public String getDetailedDescription() {
        return mDetailedDescription;
    }

    public String getJobPosition() {
        return mJobPosition;
    }

    public String getCompanyName() {
        return mCompanyName;
    }


    protected UpcomingDrives(Parcel in) {
        mDriveDate = in.readString();
        mPlace = in.readString();
        mDetailedDescription = in.readString();
        mJobPosition = in.readString();
        mCompanyName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDriveDate);
        dest.writeString(mPlace);
        dest.writeString(mDetailedDescription);
        dest.writeString(mJobPosition);
        dest.writeString(mCompanyName);
    }    @SuppressWarnings("unused")
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