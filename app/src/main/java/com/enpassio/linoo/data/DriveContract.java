package com.enpassio.linoo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ABHISHEK RAJ on 8/29/2017.
 */

/* code below referenced from my movie project
 * at the link: https://github.com/rajtheinnovator/Project-2-ShowMyShow/
 */

public class DriveContract {


    public static final String CONTENT_AUTHORITY = "com.enpassio.linoo";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DRIVES = "drives";

    /**
     * Inner class that defines constant values for the drive database table.
     * Each entry in the table represents a single drive.
     */

    public static final class DriveEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DRIVES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of drive.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRIVES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single drive.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRIVES;

        public final static String TABLE_NAME = "drives";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_COMPANY_NAME = "company_name";
        public final static String COLUMN_DRIVE_DATE = "drive_date";
        public final static String COLUMN_DRIVE_LOCATION = "drive_location";
        public final static String COLUMN_JOB_POSITION = "job_position";
        public final static String COLUMN_JOB_DESCRIPTION = "job_description";
        public final static String COLUMN_DRIVE_KEY = "drive_key";

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
