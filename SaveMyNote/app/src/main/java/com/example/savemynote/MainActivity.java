package com.example.savemynote;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.TextKeyListener;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editNote;
    TextView btn_save;
    ImageView btn_upload;
    ImageView image ;
    public static String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myDb = new DatabaseHelper(this);
        editNote = (EditText)findViewById(R.id.editText);
        btn_save = (TextView) findViewById(R.id.text_save);
        btn_upload = (ImageView) findViewById(R.id.add_gallery);
        image = (ImageView)findViewById(R.id.idImage);
        AddData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
    public void AddData()
    {
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });

        btn_save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newEntry = editNote.getText().toString();
                        if(editNote.length() > 0) {
                            boolean isInserted = myDb.insertData(editNote.getText().toString(), MainActivity.name);
                            if(isInserted == true)
                            {
                                Toast.makeText(MainActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(MainActivity.this, "Note not saved", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(MainActivity.this, ViewListContents.class);
                            startActivity(intent);
                        }else
                         Toast.makeText(MainActivity.this, "You must put something in the text field", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                Uri uri = data.getData();
                InputStream input = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                image.setImageBitmap(bitmap);

                saveImage(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveImage(Bitmap bitmap)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/SaveMyNote/saved_images");
        myDir.mkdirs();


        File file = new File (myDir, getFileName());
        MainActivity.name = file.getAbsolutePath();
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileName()
    {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return new String("Image-"+ n +".jpg");
    }

}
