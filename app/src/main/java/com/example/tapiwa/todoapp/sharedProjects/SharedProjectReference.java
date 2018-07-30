package com.example.tapiwa.todoapp.sharedProjects;

import java.io.Serializable;

public class SharedProjectReference implements Serializable {

    String projectKey;

    public SharedProjectReference() {
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    @Override
    public int hashCode() {
        //Generate a hashCode for this object
        int hash = 101;
        hash = 603 * hash + this.projectKey.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            if (this.getProjectKey().equals(((SharedProjectReference) obj).projectKey)) {
                return true;
            }
        }
        return false;
    }
}
