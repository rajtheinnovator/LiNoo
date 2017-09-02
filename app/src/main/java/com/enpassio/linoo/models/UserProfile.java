package com.enpassio.linoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 9/1/2017.
 */

public class UserProfile implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
    private String usersName;
    private String usersCity;
    private String usersEmail;

    public UserProfile(String name, String city, String email) {
        usersName = name;
        usersCity = city;
        usersEmail = email;
    }

    public UserProfile() {
        //an empty constructor as required by Firebase
    }

    protected UserProfile(Parcel in) {
        usersName = in.readString();
        usersCity = in.readString();
        usersEmail = in.readString();
    }

    public String getUsersName() {
        return usersName;
    }

    public String getUsersCity() {
        return usersCity;
    }

    public String getUsersEmail() {
        return usersEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(usersName);
        dest.writeString(usersCity);
        dest.writeString(usersEmail);
    }
}



