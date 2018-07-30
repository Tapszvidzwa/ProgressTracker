package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class FileHandler {

    private Context context;

    public FileHandler() {
    }

    public FileHandler(Context context) {
        this.context = context;
    }

    public void saveFile(String filename, String json) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir(), filename);
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] personalProjectsFileBytes = json.getBytes();
            fos.write(personalProjectsFileBytes);
            fos.flush();
        } catch (IOException e) {
            Toasty.error(context, context.getString(R.string.failed_file_creation), Toast.LENGTH_SHORT).show();
            return;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject readFile(final String filename) {
        BufferedReader br = null;
        try {
            File tasksFile = new File(context.getFilesDir(), filename);
            tasksFile.createNewFile();

            br = new BufferedReader(new FileReader(tasksFile));
            if (br != null) {
                if (br.read() != -1) {
                    String tasksString = '{' + br.readLine();
                    JSONObject jsonObject = new JSONObject(tasksString);
                    return jsonObject;
                } else {
                    return null;
                }
            }
        } catch (IOException | JSONException e) {
            Toasty.error(context, context.getString(R.string.failed_file_loading), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
