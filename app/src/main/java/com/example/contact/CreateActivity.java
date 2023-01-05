package com.example.contact;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import db.DbHelper;

public class CreateActivity extends AppCompatActivity {
    DbHelper dbHelper;
    private EditText edtName, edtPhoneNumber, edtEmail, edtStatus, edtAddress, edtBirthDate, edtSocialMedia;
    private ImageView imageView;
    private Button btnSave;
    byte[] bytes = null;
    Uri imageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dbHelper = new DbHelper(this);

        edtName = findViewById(R.id.edt_name);
        edtPhoneNumber = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtAddress = findViewById(R.id.edt_address);
        edtBirthDate = findViewById(R.id.edt_birth_date);
        edtSocialMedia = findViewById(R.id.edt_social_media);

        Spinner spinner = (Spinner)findViewById(R.id.edt_status);

        btnSave = findViewById(R.id.btn_submit);

        imageView = findViewById(R.id.imageView);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String phone_number = edtPhoneNumber.getText().toString();
                String email = edtEmail.getText().toString();
                String address = edtAddress.getText().toString();
                String birth_date = edtBirthDate.getText().toString();
                String social_media = edtSocialMedia.getText().toString();
                String status = spinner.toString();


                if(name.isEmpty()){
                    Snackbar.make(view, "Name must be filled.", Snackbar.LENGTH_SHORT).show();
                }else if(phone_number.isEmpty()){
                    Snackbar.make(view, "Phone Number must be filled.", Snackbar.LENGTH_SHORT).show();
                }else if(phone_number.isEmpty()){
                    Snackbar.make(view, "Phone Number must be filled.", Snackbar.LENGTH_SHORT).show();
                }else{
                    try{
                        if(imageUri != null){
                            bytes = getBytes(CreateActivity.this, imageUri);
                        }

                        dbHelper.store(name, phone_number, email, bytes, status, address, birth_date, social_media);

                        Toast.makeText(getApplicationContext(), "Contact created successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                        startActivity(intent);

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    public void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(intent);
    }

    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent  = result.getData();
                            if(intent != null && intent.getData() != null){
                                imageUri = intent.getData();
                                imageView.setImageURI(imageUri);
                            }
                        }
                    }
            );
}