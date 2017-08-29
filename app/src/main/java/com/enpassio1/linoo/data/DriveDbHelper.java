package com.enpassio1.linoo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.enpassio1.linoo.data.DriveContract.DriveEntry;

/**
 * Created by ABHISHEK RAJ on 8/29/2017.
 */

/* code below referenced from my movie project
 * at the link: https://github.com/rajtheinnovator/Project-2-ShowMyShow/
 */

public class DriveDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "drives.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DriveDbHelper}.
     *
     * @param context of the app
     */
    public DriveDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the drives table
        String SQL_CREATE_DRIVE_TABLE = "CREATE TABLE " + DriveEntry.TABLE_NAME + " ("
                + DriveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + DriveEntry.COLUMN_COMPANY_NAME + " TEXT NOT NULL DEFAULT '', "
                + DriveEntry.COLUMN_DRIVE_DATE + " TEXT NOT NULL DEFAULT '', "
                + DriveEntry.COLUMN_DRIVE_LOCATION + " TEXT NOT NULL DEFAULT '', "
                + DriveEntry.COLUMN_JOB_POSITION + " TEXT NOT NULL DEFAULT '', "
                + DriveEntry.COLUMN_DRIVE_KEY + " TEXT NOT NULL DEFAULT '', "
                + DriveEntry.COLUMN_JOB_DESCRIPTION + " TEXT NOT NULL DEFAULT '' ); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_DRIVE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}