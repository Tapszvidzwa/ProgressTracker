package com.example.tapiwa.todoapp.sharedProjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectModel;
import com.example.tapiwa.todoapp.sharedProjects.sharedProject.SharedProjectReference;

import java.util.ArrayList;


public class SharedProjectsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SharedProjectReference> sharedProjectsList;


    public SharedProjectsAdapter(Context context, int layout, ArrayList<SharedProjectReference> sharedProjectsList) {
        this.context = context;
        this.layout = layout;
        this.sharedProjectsList = sharedProjectsList;
    }

    @Override
    public int getCount() {
        return sharedProjectsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sharedProjectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView projectTitle;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final SharedProjectReference project = sharedProjectsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.projectTitle = row.findViewById(R.id.project_name);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.projectTitle.setText(project.getProjectName());
        return row;
    }
}