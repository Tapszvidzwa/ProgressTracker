package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class FileHandler {

    Context context;

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
            Toasty.error(context, context.getString(R.string.failed_file_creation), Toast.LENGTH_SHORT);
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
}
