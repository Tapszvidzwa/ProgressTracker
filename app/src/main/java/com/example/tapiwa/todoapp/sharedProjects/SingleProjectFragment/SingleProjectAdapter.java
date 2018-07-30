package com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;


public class SingleProjectAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SharedProjectTask> tasks;
    private FirebaseUser user;
    private Util utils;


    public SingleProjectAdapter(Context context, int layout, ArrayList<SharedProjectTask> tasks) {
        this.context = context;
        this.layout = layout;
        this.tasks = tasks;
        user = FirebaseAuth.getInstance().getCurrentUser();
        utils = new Util();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView task, lastModifiedByName, lastModifiedDate;
        ImageView completionStatus;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final SharedProjectTask task = tasks.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.task = row.findViewById(R.id.task_title_txtV);
            holder.lastModifiedByName = row.findViewById(R.id.last_modified_by);
            holder.completionStatus = row.findViewById(R.id.completion_status);
            holder.lastModifiedDate = row.findViewById(R.id.date_last_modified);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.task.setText(task.getTask());


        switch (task.getCompletionStatus()) {
            case "completed":
                holder.completionStatus.setImageResource(R.drawable.ic_chck);
                holder.lastModifiedByName.setText(context.getString(R.string.completed_by) + " " + task.getWhoCompletedTask());
                holder.lastModifiedDate.setText(utils.getDateFromMillis(Long.parseLong(task.getDateLastModified())));
                break;
            case "uncompleted":
                holder.completionStatus.setImageResource(R.drawable.ic_red_boxx);
                holder.lastModifiedByName.setText("");
                break;
            default:
                holder.completionStatus.setImageResource(R.drawable.ic_red_boxx);
        }

        return row;
    }
}