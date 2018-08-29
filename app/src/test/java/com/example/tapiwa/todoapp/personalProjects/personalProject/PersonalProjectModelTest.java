package com.example.tapiwa.todoapp.personalProjects.personalProject;

import com.example.tapiwa.todoapp.Utils.Constants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersonalProjectModelTest {

    private PersonalProjectModel model = new PersonalProjectModel();

    @Test
    public void generateRandomKey() {
        String randomKey = model.generateRandomKey();
        int numLetters = 0;
        int numDigits = 0;

        for (int i = 0; i < randomKey.length(); i++) {
            if (i < 5) {
                if (Character.isAlphabetic(randomKey.charAt(i))) {
                    numLetters++;
                }
            } else if (Character.isDigit(randomKey.charAt(i))) {
                numDigits++;
            }
        }

        assertEquals(5, numDigits);
        assertEquals(5, numLetters);
        assertEquals(Constants.PERSONAL_PROJECT_KEY_SIZE, randomKey.length());
    }
}