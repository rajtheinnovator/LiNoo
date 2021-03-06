
package com.enpassio.linoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpassio.linoo.R;
import com.enpassio.linoo.activities.DriveDetailsActivity;
import com.enpassio.linoo.activities.HiringListActivity;
import com.enpassio.linoo.fragments.DriveDetailsFragment;
import com.enpassio.linoo.models.UpcomingDrives;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

public class UpcomingHiresListAdapter extends RecyclerView.Adapter<UpcomingHiresListAdapter.ViewHolder> {

    private static ArrayList<UpcomingDrives> mUpcomingDrivesArrayList;
    private static boolean mTwoPaneStatus;
    private static DriveDetailsFragment driveDetailsFragment;
    private Context context;
    /* Store the context for easy access */
    private Context mContext;
    private UpcomingDrives mUpcomingDrives;

    /* Pass in the drives array into the constructor */
    public UpcomingHiresListAdapter(Context context, ArrayList<UpcomingDrives> upcomingDrives, boolean twoPaneStatus) {
        mUpcomingDrivesArrayList = upcomingDrives;
        mContext = context;
        mTwoPaneStatus = twoPaneStatus;
    }

    /* Easy access to the context object in the recyclerview */
    private Context getContext() {
        return mContext;
    }

    @Override
    public UpcomingHiresListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        /* Inflate the custom layout */
        View drivesView = inflater.inflate(R.layout.list_item_upcoming_drives, parent, false);

        /* Return a new holder instance */
        ViewHolder viewHolder = new UpcomingHiresListAdapter.ViewHolder(context, drivesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UpcomingHiresListAdapter.ViewHolder viewHolder, int position) {
        /* Get the data model based on position */
        UpcomingDrives currentDrives = mUpcomingDrivesArrayList.get(position);
        /*
        Set item views based on your views and data model
         */
        viewHolder.hiringDateTextView.setText(currentDrives.getDriveDate());
        viewHolder.hiringSummaryTextView.setText(currentDrives.getDetailedDescription());
        if (mTwoPaneStatus && driveDetailsFragment == null) {
            /* if the recyclerView hasnot been clicked, then inflate the detailsFragment with the
             * first child of it
             */
            UpcomingDrives currentUpcomingDrives = mUpcomingDrivesArrayList.get(0);
            DriveDetailsFragment driveDetailsFragment = new DriveDetailsFragment();
            Bundle driveDetailsBundle = new Bundle();
            driveDetailsBundle.putParcelable(context.getResources()
                    .getString(R.string.current_upcoming_drive_key), currentUpcomingDrives);
            driveDetailsFragment.setArguments(driveDetailsBundle);
            ((HiringListActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_drive_details_container, driveDetailsFragment)
                    .commit();
        }
    }

    @Override
    public int getItemCount() {
        if (mUpcomingDrivesArrayList == null) {
            return 0;
        } else {
            return mUpcomingDrivesArrayList.size();
        }
    }


    public void setDriveData(ArrayList<UpcomingDrives> upcomingDrivesArrayList) {
        mUpcomingDrivesArrayList = upcomingDrivesArrayList;

        /* code below referenced from the link: https://stackoverflow.com/a/5412542/5770629 */
        if (mUpcomingDrivesArrayList != null) {
            Collections.reverse(mUpcomingDrivesArrayList);
        }
        notifyDataSetChanged();
    }

    /*
     Provide a direct reference to each of the views within a data item
     Used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /*
        Your holder should contain a member variable
        for any view that will be set as you render a row
        */
        @BindView(R.id.hiring_date_text_view)
        TextView hiringDateTextView;
        @BindView(R.id.hiring_summary_text_view)
        TextView hiringSummaryTextView;
        Context context;

        /*
        We also create a constructor that accepts the entire item row
        and does the view lookups to find each subview
        */
        public ViewHolder(Context context, View itemView) {
            /*
            Stores the itemView in a public final member variable that can be used
            to access the context from any ViewHolder instance.
            */
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        /*The codes below and at some other places throughout the app related to RecyclerView has been
        * taken from multiple sources on the web including from the following @link:
        * https://guides.codepath.com/android/using-the-recyclerview
        */
        /* Handles the row being being clicked */

        @OnClick(R.id.list_item)
        public void listItemClicked() {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                UpcomingDrives currentUpcomingDrives = mUpcomingDrivesArrayList.get(position);

                if (!mTwoPaneStatus) {
                /* We can access the data within the views */

                    Intent drivesDetailIntent = new Intent(context, DriveDetailsActivity.class);
                    Bundle driveDetailsBundle = new Bundle();
                    driveDetailsBundle.putParcelable(context.getResources()
                            .getString(R.string.current_upcoming_drive_key), currentUpcomingDrives);
                    drivesDetailIntent.putExtra(context.getResources()
                            .getString(R.string.drive_details_bundle_key), driveDetailsBundle);
                    context.startActivity(drivesDetailIntent);
                } else {
                    driveDetailsFragment = new DriveDetailsFragment();
                    Bundle driveDetailsBundle = new Bundle();
                    driveDetailsBundle.putParcelable(context.getResources()
                            .getString(R.string.current_upcoming_drive_key), currentUpcomingDrives);
                    driveDetailsFragment.setArguments(driveDetailsBundle);
                    ((HiringListActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_drive_details_container, driveDetailsFragment)
                            .commit();
                }
            }
        }
    }
}