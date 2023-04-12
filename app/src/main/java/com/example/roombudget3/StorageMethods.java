package com.example.roombudget3;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class StorageMethods {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";

    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";


    public boolean checkDirectories() {

        File file = new File(default_path);

        if (file.exists()) {

            return true;

        } else {
            return false;
        }
    }

    public void createDirectories() {
        File file = new File(default_path);
        file.mkdirs();
    }

    public boolean checkFile(String filename) {
        File file = new File(default_path, filename);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void createFile(String filename, String[] content) {

        File file = new File(default_path, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < content.length; i++) {
                pw.println(content[i]);
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readFile(String filename) {

        File file = new File(default_path, filename);
        String result;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = stringBuilder.toString();
        return result;


    }

    public void deleteFile(String filename) {

        File file = new File(default_path, filename);

        file.delete();

    }

    public String[] readFileOfPersons() {

        String[] persons = {};

        File file = new File(default_path, PERSONS_FILE_NAME);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                persons = Arrays.copyOf(persons, persons.length + 1);
                persons[persons.length - 1] = line.trim();

            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return persons;
        }
    }

    public String[] readFileOfMonths() {

        String[] months = {};

        File file = new File(default_path, MONTHS_FILE_NAME);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                months = Arrays.copyOf(months, months.length + 1);
                months[months.length - 1] = line.trim();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return months;
        }
    }




}
