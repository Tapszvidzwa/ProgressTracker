package com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class PersonalProjectsContainerAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<PersonalProjectModel> personalProjectsList;

    public PersonalProjectsContainerAdapter(Context context, int layout, LinkedList<PersonalProjectModel> personalProjectsList) {
        this.context = context;
        this.layout = layout;
        this.personalProjectsList = personalProjectsList;
    }

    @Override
    public int getCount() {
        if(personalProjectsList != null) {
            return personalProjectsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return personalProjectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {
        final PersonalProjectModel project = personalProjectsList.get(position);
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

        holder.projectTitle.setText(project.getProjectTitle());
        return row;
    }

    private class ViewHolder {
        TextView projectTitle;
    }
}