package com.example.roombudget3;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;


public class ReportBugsFragment extends Fragment {

    ImageButton BSelectImage;
    Button send;
    EditText email_body;
    Uri uri;
    ImageView IVPreviewImage;
    int SELECT_PICTURE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report_bugs, container, false);

        // register the UI widgets with their appropriate IDs
        BSelectImage = v.findViewById(R.id.BSelectImage);
        IVPreviewImage = v.findViewById(R.id.IVPreviewImage);
        send = v.findViewById(R.id.Bsend);
        email_body = v.findViewById(R.id.emailbodyid);
        TextInputLayout description_til = v.findViewById(R.id.txtInputLayout);
        description_til.setStartIconTintList(null);


        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email_body.getText().toString().trim().length() == 0 ){
                    email_body.setError("Email body should not be empty");
                }else if(IVPreviewImage.getDrawable() == null){
                    email_body.setError("Image not chosen");
                } else {
                    mailto4(email_body.getText().toString(), uri);
                }
            }
        });

        return v;
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                    BSelectImage.setVisibility(View.GONE);
                    IVPreviewImage.setVisibility(View.VISIBLE);
                    uri = selectedImageUri;
                }
            }
        }
    }

    public void mailto4(String emailbody, Uri bm) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"gajulanithish000@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting Bugs");
        intent.putExtra(Intent.EXTRA_TEXT, emailbody);
        intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com");
        intent.putExtra(Intent.EXTRA_STREAM, bm);
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

}