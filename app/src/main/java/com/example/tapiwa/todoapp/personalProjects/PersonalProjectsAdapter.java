package com.example.tapiwa.todoapp.personalProjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;

import java.util.ArrayList;
import java.util.LinkedList;


public class PersonalProjectsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<PersonalProjectModel> personalProjectsList;


    public PersonalProjectsAdapter(Context context, int layout, ArrayList<PersonalProjectModel> personalProjectsList) {
        this.context = context;
        this.layout = layout;
        this.personalProjectsList = personalProjectsList;
    }

    @Override
    public int getCount() {
        return personalProjectsList.size();
    }

    @Override
    public Object getItem(int position) {
        return personalProjectsList.get(position);
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

        final PersonalProjectModel project = personalProjectsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.projectTitle = row.findViewById(R.id.project_name);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.projectTitle.setText(project.getProjectTitle());

        return row;
    }
}