package com.example.tapiwa.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;


public class ProjectsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private LinkedList<TaskList> taskList;

    public ProjectsAdapter(Context context, int layout, LinkedList<TaskList> projectsList) {
        this.context = context;
        this.layout = layout;
        this.taskList = projectsList;
    }

    @Override
    public int getCount() {
        if(taskList != null) {
            return taskList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView folder_name;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final TaskList project = taskList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.folder_name = row.findViewById(R.id.project_name);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.folder_name.setText(project.getName());

        return row;
    }
}