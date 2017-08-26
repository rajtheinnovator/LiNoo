package com.enpassio1.linoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.models.UpcomingDrives;

import java.util.ArrayList;

/**
 * Created by ABHISHEK RAJ on 8/26/2017.
 */

public class UpcomingHiresListAdapter extends RecyclerView.Adapter<UpcomingHiresListAdapter.ViewHolder> {

    private static ArrayList<UpcomingDrives> mUpcomingDrivesArrayList;
    /* Store the context for easy access */
    private Context mContext;
    private UpcomingDrives mUpcomingDrives;

    /* Pass in the drives array into the constructor */
    public UpcomingHiresListAdapter(Context context, ArrayList<UpcomingDrives> upcomingDrives) {
        mUpcomingDrivesArrayList = upcomingDrives;
        mContext = context;
    }

    /* Easy access to the context object in the recyclerview */
    private Context getContext() {
        return mContext;
    }

    @Override
    public UpcomingHiresListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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
        viewHolder.hiringSummaryTextView.setText(currentDrives.getDriveSummary());
    }

    @Override
    public int getItemCount() {
        return mUpcomingDrivesArrayList.size();
    }

    public void setDriveData(ArrayList<UpcomingDrives> upcomingDrivesArrayList) {
        mUpcomingDrivesArrayList = upcomingDrivesArrayList;
        notifyDataSetChanged();
    }

    /*
     Provide a direct reference to each of the views within a data item
     Used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /*
        Your holder should contain a member variable
        for any view that will be set as you render a row
        */
        public final TextView hiringDateTextView;
        public final TextView hiringSummaryTextView;
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
            hiringDateTextView = (TextView) itemView.findViewById(R.id.hiring_date_text_view);
            hiringSummaryTextView = (TextView) itemView.findViewById(R.id.hiring_summary_text_view);
            this.context = context;
            /* Attach a click listener to the entire row view */
            itemView.setOnClickListener(this);
        }

        /*The codes below and at some other places throughout the app related to RecyclerView has been
        * taken from multiple sources on the web including from the following @link:
        * "https://guides.codepath.com/android/using-the-recyclerview
        */
        /* Handles the row being being clicked */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                UpcomingDrives currentUpcomingDrives = mUpcomingDrivesArrayList.get(position);
                /* We can access the data within the views */

                // Intent drivesDetailIntent = new Intent(context, DriveDetailsActivity.class);


            }
        }
    }
}
