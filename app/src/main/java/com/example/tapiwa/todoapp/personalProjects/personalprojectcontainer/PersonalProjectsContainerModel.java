package com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer;

import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectModel;

import java.util.LinkedList;

public class PersonalProjectsContainerModel {

    private LinkedList<PersonalProjectModel> projects;

    public PersonalProjectsContainerModel() {
        //empty constructor
    }

    public LinkedList<PersonalProjectModel> getProjects() {
        return projects;
    }

    public void setProjects(LinkedList<PersonalProjectModel> projects) {
        this.projects = projects;
    }

    public PersonalProjectModel getProject(String projectKey) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectKey().equals(projectKey)) {
                return projects.get(i);
            }
        }
        return null;
    }

    public void updateProject(PersonalProjectModel project) {
        String projectKey = project.getProjectKey();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectKey().equals(projectKey)) {
                projects.set(i, project);
                return;
            }
        }
    }

    public int getNumProjects() {
        return projects.size();
    }

    public String getProjectKey(int pos) {
        return projects.get(pos).getProjectKey();
    }

    public String projectName(int pos) {
        return projects.get(pos).getProjectTitle();
    }

    public String getProjectName(String projectKey) {
        for (int i = 0; i < projects.size(); i++) {
            if (projectKey.equals(projects.get(i).getProjectKey())) {
                return projects.get(i).getProjectTitle();
            }
        }
        return null;
    }

    public PersonalProjectModel getPersonalProject(String projectKey) {
        for (int i = 0; i < projects.size(); i++) {
            if (projectKey.equals(projects.get(i).getProjectKey())) {
                return projects.get(i);
            }
        }
        return null;
    }
}
