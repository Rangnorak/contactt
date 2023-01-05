package com.example.contact;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import db.DbHelper;
import model.Contact;

public class EditActivity extends AppCompatActivity
{
    private DbHelper dbHelper;
    private EditText etName, etPhone, etEmail, etStatus, etAddress, etBirthDate, etSocialMedia;
    private Button btnUpdate, btnDelete;
    private ImageView imageView;
    private Contact contact;
    Uri imageUri = null;
    byte[] imageBytes = null;
    byte[] bytes = null;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new DbHelper(this);

        etName = findViewById(R.id.edt_name);
        etPhone = findViewById(R.id.edt_phone);
        etEmail = findViewById(R.id.edt_email);
        etStatus = findViewById(R.id.edt_status);
        etAddress = findViewById(R.id.edt_address);
        etBirthDate = findViewById(R.id.edt_birth_date);
        etSocialMedia = findViewById(R.id.edt_social_media);

        btnUpdate = findViewById(R.id.btn_submit);
        btnDelete = findViewById(R.id.btn_delete);

        imageView = findViewById(R.id.edt_image);

        Intent intent = getIntent();
        contact = (Contact) intent.getSerializableExtra("contact");

        bytes = contact.getImage();

        etName.setText(contact.getName());
        etPhone.setText(contact.getPhone_number());
        etEmail.setText(contact.getEmail());
        etStatus.setText(contact.getStatus());
        etAddress.setText(contact.getAddress());
        etBirthDate.setText(contact.getBirth_date());
        etSocialMedia.setText(contact.getSocial_media());

        id = contact.getId();

        if(bytes != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phone_number = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                String status = etStatus.getText().toString();
                String address = etAddress.getText().toString();
                String birth_date = etBirthDate.getText().toString();
                String social_media = etSocialMedia.getText().toString();

                if (name.isEmpty()) {
                    Snackbar.make(view, "Name must be filled.", Snackbar.LENGTH_SHORT).show();
                } else if (phone_number.isEmpty()) {
                    Snackbar.make(view, "Phone Number must be filled.", Snackbar.LENGTH_SHORT).show();
                }else{
                    try{

                        if(imageUri != null){
                            imageBytes = getBytes(EditActivity.this, imageUri);
                        }else{
                            imageBytes = bytes;
                        }

                        dbHelper.update(id, name, phone_number, email, imageBytes, status, address, birth_date, social_media);
                        Toast.makeText(getApplicationContext(), "Contact updated successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(), "error: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
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

    public void showConfirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("Delete confirmation");
        builder.setMessage("This contact will be deleted form your device");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.delete(id, imageUri);
                Toast.makeText(EditActivity.this, "Contact successfully deleted.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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