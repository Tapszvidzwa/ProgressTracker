package com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.projectmembers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SharedProjectTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProjectMembersAdapter  extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<String> names;
    private ArrayList<String> emails;


    public ProjectMembersAdapter(Context context, int layout, ArrayList<String> names, ArrayList<String> emails) {
        this.context = context;
        this.layout = layout;
        this.names = names;
        this.emails = emails;
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public Object getItem(int position) {
        return emails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView name, email;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {
        String name, email;

        try {
             name = names.get(position);
             email = emails.get(position);
        } catch (NullPointerException e) {
            name = "Group member";
            email = emails.get(position);
        }

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.name = row.findViewById(R.id.member_name);
            holder.email = row.findViewById(R.id.member_email);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.name.setText(name);
        holder.email.setText(email);

        return row;
    }
}
