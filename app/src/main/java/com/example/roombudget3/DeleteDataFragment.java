package com.example.roombudget3;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeleteDataFragment extends Fragment {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";
    String[] persons = {};
    String[] months = {};
    String selected_person;
    String selected_month;

    AutoCompleteTextView person1_act, month_act;
    Button get_data_btn;
    Button delete_btn;
    EditText get_id;
    ListView listView;
    ListAdapter adapter;

    ArrayList<HashMap<String, String>> list23;

    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";

    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_data, container, false);

        mBuilder1 = new AlertDialog.Builder(getActivity());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();


        person1_act = v.findViewById(R.id.person_delete1_id);
        month_act = v.findViewById(R.id.month_delete_id);
        get_data_btn = v.findViewById(R.id.get_data_delete_id);
        delete_btn = v.findViewById(R.id.delete_item_btn);
        get_id = v.findViewById(R.id.get_ID_id);
        listView = v.findViewById(R.id.lv_items_delete);

        TextInputLayout person_til = v.findViewById(R.id.person_til);
        TextInputLayout month_til = v.findViewById(R.id.month_til);

        person_til.setStartIconTintList(null);
        month_til.setStartIconTintList(null);

        person1_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String person123 = parent.getItemAtPosition(position).toString();
                selected_person = person123;

            }
        });

        month_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String month123 = parent.getItemAtPosition(position).toString();
                selected_month = month123;

            }

        });

        get_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getItems(selected_person, selected_month);

            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(get_id.getText().toString().trim().length() == 0){
                    get_id.setError("ID should not be empty");
                } else {

                    for (int i = 0; i < list23.size(); i++) {

                        String id1 = list23.get(i).get("position5").trim();
                        String entered_id = get_id.getText().toString().trim();

                        if (id1.equals(entered_id)) {
                            String person = list23.get(i).get("position1");
                            String month = list23.get(i).get("position2");
                            String amount = list23.get(i).get("position3");
                            String description = list23.get(i).get("position4");
                            String id = list23.get(i).get("position5");

                            delete(person, month, amount, description, id);
                            break;
                        }
                    }

                }


            }
        });

        StorageMethods storageMethods = new StorageMethods();
        persons =  storageMethods.readFileOfPersons();
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

    private void delete(String person, String month, String amount, String description, String id) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.confirmation_dialog2, null);
        TextView nameD = view1.findViewById(R.id.name_confirm_id);
        TextView idD = view1.findViewById(R.id.id_confirm_id);
        TextView monthD = view1.findViewById(R.id.month_confirm_id);
        TextView amountD = view1.findViewById(R.id.amount_confirm_id);
        TextView descriptionD = view1.findViewById(R.id.description_confirm_id);
        Button cancel = view1.findViewById(R.id.cancel_confirm_id);
        Button upload = view1.findViewById(R.id.confirm_confirm_id);
        LinearLayout ll1 = view1.findViewById(R.id.ll1);
        LottieAnimationView anm1 = view1.findViewById(R.id.lottie_animation_1);
        mBuilder.setView(view1);
        final AlertDialog dialog1 = mBuilder.create();

        nameD.setText(" " + person);
        idD.setText(" " + id);
        monthD.setText(" " + month);
        amountD.setText(" ₹ " + amount);
        descriptionD.setText(" " + description);

        Log.i("entered ", "triggered " + person + " " + month + " " + amount + " " + description + " " + id + " ");
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.GONE);
                anm1.setVisibility(View.VISIBLE);
                anm1.setAnimation(R.raw.meditation_wait_please);
                anm1.playAnimation();

                final String person = selected_person;
                final String rowID = get_id.getText().toString().trim();
                String url = getResources().getString(R.string.spreadsheet_url);

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
                                        get_data_btn.performClick();
                                    }
                                }, 4000);

                                get_id.setText("");
                                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

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
                        Map<String, String> parmas = new HashMap<>();

                        //here we pass params
                        parmas.put("action", "deleteItem");
                        parmas.put("person", person);
                        parmas.put("rowID", rowID);

                        return parmas;
                    }
                };


                int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(retryPolicy);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(stringRequest);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

    }

    private void person() {

        //person dropdown
        ArrayAdapter<String> person_adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item2, persons);
        person_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        person1_act.setAdapter(person_adapter);
    }

    private void month() {

        //month dropdown
        ArrayList<String> m = new ArrayList<>();
        m.add(Integer.toString(months.length));

        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, m);
        month_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        month_act.setAdapter(month_adapter);
    }

    private void getItems(String kani, String kini) {

        final String name = kani;
        final String month = kini;
        String param = "?action=getItems&person=" + name + "&month=" + month;
        String url = getResources().getString(R.string.spreadsheet_url);

        dialogg1.show();
        dialogg1.setCanceledOnTouchOutside(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {

        list23 = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String name = jo.getString("Name");
                String month = jo.getString("Month");
                String amount = jo.getString("Amount");
                String description = jo.getString("Description");
                String id = jo.getString("Id");
                String date = jo.getString("Date").substring(0,10);

                date = date.replace("-","/");
                date = date + "ABCDE";

                Boolean b = null;
                if(Character.toString(date.charAt(2)).equals("/")){
                    b = true;
                }else if(Character.toString(date.charAt(4)).equals("/")){
                    b = false;
                }

                String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                String[] temp;
                date = date.substring(0,10);

                if(b){

                    temp = date.split("/");
                    date = String.format("%s %s %s", temp[0], months[Integer.parseInt(temp[1]) - 1], temp[2]);
                }else if(!b){

                    temp = date.split("/");
                    date = String.format("%s %s %s", temp[1], months[Integer.parseInt(temp[2])], temp[0]);
                }

                HashMap<String, String> item = new HashMap<>();
                item.put("position1", name);
                item.put("position2", month);
                item.put("position3", amount);
                item.put("position4", description);
                item.put("position5", id);
                item.put("position6", date);


                list23.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(getActivity(), list23, R.layout.list_item_row2,
                new String[]{"position1", "position2", "position3", "position4", "position5","position6"}, new int[]{R.id.lv_2_person_id, R.id.lv_2_month_id, R.id.lv_2_amount_id, R.id.lv_2_description_id, R.id.lv_2_row_id_id,R.id.lv_2_date_id});


        listView.setAdapter(adapter);
        dialogg1.dismiss();

    }

}