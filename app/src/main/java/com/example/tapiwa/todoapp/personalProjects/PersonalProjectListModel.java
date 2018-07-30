package com.example.tapiwa.todoapp.personalProjects;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonalProjectListModel implements Serializable {

    private ArrayList<PersonalProjectModel> projects;

    public PersonalProjectListModel() {
        //empty constructor
    }

    public ArrayList<PersonalProjectModel> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<PersonalProjectModel> projects) {
        this.projects = projects;
    }

    public PersonalProjectModel getProject(String projectName) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectTitle().equals(projectName)) {
                return projects.get(i);
            }
        }
        return new PersonalProjectModel();
    }

    public void updateProject(PersonalProjectModel project) {

        String projectName = project.getProjectTitle();

        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectTitle().equals(projectName)) {
                projects.set(i, project);
                return;
            }
        }
    }

}
