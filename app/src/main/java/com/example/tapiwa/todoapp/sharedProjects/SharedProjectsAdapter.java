package com.example.tapiwa.todoapp.sharedProjects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static com.example.tapiwa.todoapp.utils.Constants.SHARED_PROJECTS_DB_PATH;


public class SharedProjectsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SharedProjectReference> sharedProjectsList;
    private FirebaseFirestore db;


    public SharedProjectsAdapter(Context context, int layout, ArrayList<SharedProjectReference> sharedProjectsList) {
        this.context = context;
        this.layout = layout;
        this.sharedProjectsList = sharedProjectsList;
        db = FirebaseFirestore.getInstance();
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
        TextView projectTitle, completionPercentage;
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
            holder.completionPercentage = row.findViewById(R.id.completion_percentage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        setProjectName(holder, project);
        return row;
    }

    public void setProjectName(final ViewHolder viewHolder, SharedProjectReference projectReference) {
        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                SharedProjectModel projectModel = documentSnapshot.toObject(SharedProjectModel.class);
                if (projectModel != null) {
                    viewHolder.projectTitle.setText(projectModel.getProjectTitle());
                    viewHolder.completionPercentage.setText(projectModel.getPercentageCompleted());
                    viewHolder.completionPercentage.setTextColor(Color.rgb(208, 35, 35));
                } else {
                    viewHolder.projectTitle.setText("Could not load project name");
                }
            }
        });
    }
}