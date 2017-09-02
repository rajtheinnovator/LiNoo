package com.enpassio.linoo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.enpassio.linoo.R;
import com.enpassio.linoo.data.DriveContract.DriveEntry;

/**
 * Created by ABHISHEK RAJ on 8/29/2017.
 */

/* code below referenced from my movie project
 * at the link: https://github.com/rajtheinnovator/Project-2-ShowMyShow/
 */
public class DriveProvider extends ContentProvider {
    /**
     * URI matcher code for the content URI for the drives table
     */

    private static final int DRIVES = 100;

    /**
     * URI matcher code for the content URI for a single pet in the drives table
     */
    private static final int DRIVE_ID = 200;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.drives/drive" will map to the
        // integer code {@link #DRIVES}. This URI is used to provide access to MULTIPLE rows
        // of the drives table.
        sUriMatcher.addURI(DriveContract.CONTENT_AUTHORITY, DriveContract.PATH_DRIVES, DRIVES);

        // The content URI of the form "content://com.example.android.drives/drive/#" will map to the
        // integer code {@link #DRIVES}. This URI is used to provide access to ONE single row
        // of the drives table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.drives/drive/3" matches, but
        // "content://com.example.android.drives/drive" (without a number at the end) doesn't match.
        sUriMatcher.addURI(DriveContract.CONTENT_AUTHORITY, DriveContract.PATH_DRIVES + "/#", DRIVE_ID);
    }


    /**
     * Database helper object
     */
    private DriveDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DriveDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case DRIVES:
                // For the DRIVES code, query the drives table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the drives table.
                cursor = database.query(DriveEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DRIVE_ID:
                // For the DRIVE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.drives/drive/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DriveEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the drives table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(DriveEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_unknown_uri) + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DRIVES:
                return insertDrive(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_insert_not_supported) + uri);
        }
    }


    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertDrive(Uri uri, ContentValues values) {
        // Check that the drives name is not null
        String name = values.getAsString(DriveEntry.COLUMN_COMPANY_NAME);
        if (name == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_a_name));
        }

        // Check that the drives name is not null
        String date = values.getAsString(DriveEntry.COLUMN_DRIVE_DATE);
        if (date == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_a_date));
        }

        // If the price is provided, check that it's greater than or equal to 0
        String location = values.getAsString(DriveEntry.COLUMN_DRIVE_LOCATION);
        if (location == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_a_location));
        }

        String jobPosition = values.getAsString(DriveEntry.COLUMN_JOB_POSITION);
        if (jobPosition == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_a_job_profile));
        }
        String jobDescription = values.getAsString(DriveEntry.COLUMN_JOB_DESCRIPTION);
        if (jobDescription == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_drive_description));
        }
        String driveKey = values.getAsString(DriveEntry.COLUMN_DRIVE_KEY);
        if (driveKey == null) {
            throw new IllegalArgumentException(getContext()
                    .getResources().getString(R.string.exception_drive_requires_a_valid_drive_key));
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new drive with the given values
        long id = database.insert(DriveEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        // Notify all listeners that the data has changed for the drive content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case DRIVES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DriveEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DRIVE_ID:
                // Delete a single row given by the ID in the URI
                selection = DriveEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DriveEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_deletion_not_supported) + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DRIVES:
                return updateDrive(uri, contentValues, selection, selectionArgs);
            case DRIVE_ID:
                // For the DRIVE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DriveEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDrive(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_update_not_supported) + uri);
        }
    }

    /**
     * Update drives in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more drives).
     * Return the number of rows that were successfully updated.
     */
    private int updateDrive(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(DriveEntry.COLUMN_COMPANY_NAME)) {
            String companyName = values.getAsString(DriveEntry.COLUMN_COMPANY_NAME);
            if (companyName == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_a_name));
            }
        }

        if (values.containsKey(DriveEntry.COLUMN_DRIVE_DATE)) {
            String driveDate = values.getAsString(DriveEntry.COLUMN_DRIVE_DATE);
            if (driveDate == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_a_date));
            }
        }
        if (values.containsKey(DriveEntry.COLUMN_DRIVE_LOCATION)) {
            String driveLocation = values.getAsString(DriveEntry.COLUMN_DRIVE_LOCATION);
            if (driveLocation == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_a_location));
            }
        }
        if (values.containsKey(DriveEntry.COLUMN_JOB_POSITION)) {
            String jobPosition = values.getAsString(DriveEntry.COLUMN_JOB_POSITION);
            if (jobPosition == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_a_job_profile));
            }
        }
        if (values.containsKey(DriveEntry.COLUMN_JOB_DESCRIPTION)) {
            String jobDescription = values.getAsString(DriveEntry.COLUMN_JOB_DESCRIPTION);
            if (jobDescription == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_drive_description));
            }
        }
        if (values.containsKey(DriveEntry.COLUMN_DRIVE_KEY)) {
            String driveFirebasePushIdKey = values.getAsString(DriveEntry.COLUMN_DRIVE_KEY);
            if (driveFirebasePushIdKey == null) {
                throw new IllegalArgumentException(getContext()
                        .getResources().getString(R.string.exception_drive_requires_a_valid_drive_key));
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DriveEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DRIVES:
                return DriveEntry.CONTENT_LIST_TYPE;
            case DRIVE_ID:
                return DriveEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
