package com.example.roombudget3;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddDataFragment extends Fragment {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";
    String[] persons = {};
    String[] months = {};
    String selected_person;
    String selected_month;
    String simplified_description;
    AutoCompleteTextView month_spinner;
    AutoCompleteTextView person_spinner;
    EditText Amount;
    EditText Description;
    EditText Date;
    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_data, container, false);

        month_spinner = v.findViewById(R.id.monthid);
        person_spinner = v.findViewById(R.id.personnameid);
        Button UploadData = v.findViewById(R.id.uploadid);
        Button clearData = v.findViewById(R.id.clear_id);
        Amount = v.findViewById(R.id.amountid);
        Description = v.findViewById(R.id.descriptionid);
        Date = v.findViewById(R.id.dateid);
        TextInputLayout person_til = v.findViewById(R.id.person_til);
        TextInputLayout date_til = v.findViewById(R.id.date_til);
        TextInputLayout month_til = v.findViewById(R.id.month_til);
        TextInputLayout amount_til = v.findViewById(R.id.amount_til);
        TextInputLayout description_til = v.findViewById(R.id.description_til);

        person_til.setStartIconTintList(null);
        date_til.setStartIconTintList(null);
        month_til.setStartIconTintList(null);
        amount_til.setStartIconTintList(null);
        description_til.setStartIconTintList(null);

        Calendar calendar = Calendar.getInstance();
        final int year1 = calendar.get(Calendar.YEAR);
        final int month1 = calendar.get(Calendar.MONTH);
        final int date1 = calendar.get(Calendar.DAY_OF_MONTH);

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        //String date2 = day + "/" + month + "/" + year;
                        String date2 = String.format("%02d/%02d/%04d",day,month,year);
                        Date.setError(null);
                        Date.setText(date2);
                    }
                }, year1, month1, date1);
                datePickerDialog.show();
            }
        });

        UploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                simplified_description = Description.getText().toString().trim();


                if (selected_person == null) {
                    person_til.setError("Person not selected");
                } else if (Date.getText().toString().trim().length() < 10 ||  Date.getText().toString().trim().length() > 10) {
                    Date.setError("Date wrongly formatted");
                }else if(!Date.getText().toString().trim().contains("/")){
                    Date.setError("Date format DD/MM/YYYY");
                } else if (selected_month == null) {
                    month_til.setError("month should not be empty");
                } else if (Amount.getText().toString().trim().length() == 0) {
                    Amount.setError("Amount should not be empty");
                } else if (simplified_description.length() == 0) {
                    Description.setError("Description should not be empty");
                } else {
                    upload();
                }

            }
        });

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_month = null;
                selected_person = null;
                Date.setText("");
                Amount.setText("");
                Description.setText("");
                month_spinner.setText("");
                person_spinner.setText("");

            }
        });

        person_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String person123 = parent.getItemAtPosition(position).toString();
                person_til.setError("");
                selected_person = person123;

            }
        });

        month_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String month123 = parent.getItemAtPosition(position).toString();
                month_til.setError("");
                selected_month = month123;

            }

        });

        StorageMethods storageMethods = new StorageMethods();
        persons = storageMethods.readFileOfPersons();
        months = storageMethods.readFileOfMonths();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                person();
                month();
            }
        }, 500);

        return v;
    }

    private void person() {

        //person dropdown
        ArrayAdapter<String> person_adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item2, persons);
        person_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        person_spinner.setAdapter(person_adapter);
    }

    private void month() {

        //month dropdown
        ArrayList<String> n = new ArrayList<>();
        n.add(Integer.toString(months.length));

        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, n);
        month_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        month_spinner.setAdapter(month_adapter);
    }

    private void upload() {


        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        TextView nameD = view1.findViewById(R.id.name_confirm_id);
        TextView dateD = view1.findViewById(R.id.date_confirm_id);
        TextView monthD = view1.findViewById(R.id.month_confirm_id);
        TextView amountD = view1.findViewById(R.id.amount_confirm_id);
        TextView descriptionD = view1.findViewById(R.id.description_confirm_id);
        Button cancel = view1.findViewById(R.id.cancel_confirm_id);
        Button upload = view1.findViewById(R.id.confirm_confirm_id);
        LinearLayout ll1 = view1.findViewById(R.id.ll1);
        LottieAnimationView anm1 = view1.findViewById(R.id.lottie_animation_1);
        mBuilder.setView(view1);
        final AlertDialog dialog1 = mBuilder.create();
        final String person = selected_person;
        final String month = selected_month;
        final String date = Date.getText().toString().trim();
        final String amount = Amount.getText().toString().trim();
        final String description = Description.getText().toString();

        Log.i("hello",""+ date);

        nameD.setText(" " + person);
        dateD.setText(" " + date);
        monthD.setText(" " + month);
        amountD.setText(" ₹ " + amount);
        descriptionD.setText(" " + description);

        dialog1.setCanceledOnTouchOutside(false);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = getResources().getString(R.string.spreadsheet_url);

                ll1.setVisibility(View.GONE);
                anm1.setVisibility(View.VISIBLE);
                anm1.setAnimation(R.raw.meditation_wait_please);
                anm1.playAnimation();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                anm1.setAnimation(R.raw.done);
                                anm1.playAnimation();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog1.dismiss();
                                    }
                                }, 5000);

                                Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                anm1.setAnimation(R.raw.error);
                                anm1.playAnimation();

                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> para = new HashMap<>();

                        //here we pass params
                        para.put("action", "addItem");
                        para.put("person", person);
                        para.put("date", date);
                        para.put("month", month);
                        para.put("amount", amount);
                        para.put("description", description);

                        return para;
                    }
                };

                int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(retryPolicy);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(stringRequest);

                selected_month = null;
                selected_person = null;
                Date.setText("");
                Amount.setText("");
                Description.setText("");
                month_spinner.setText("");
                person_spinner.setText("");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.show();
    }
}