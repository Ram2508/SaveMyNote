package com.example.savemynote;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class TextEditor extends AppCompatActivity {
    String note, path;
    int id;
    EditText et;
    TextView text_save;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_editor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        text_save = (TextView) findViewById(R.id.text_update);
        et = (EditText) findViewById(R.id.updateText);
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        final int position = intent.getIntExtra("POSITION", -1);
        note = intent.getStringExtra("NOTE");
        path = intent.getStringExtra("PATH");
        et.append(note);
        if (path != null) {
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }


        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(TextEditor.this);

                Cursor cd = db.getAllData();
                id = -1;
                while (cd.moveToNext()) {
                    String x = cd.getString(1);
                    if (x.equals(note)) {
                        id = cd.getInt(0);
                        break;
                    }
                }
                cd.close();
                note = et.getText().toString();
                boolean b = db.updateNote(String.valueOf(id), note, path);

                db.close();
                Intent intent1 = new Intent(TextEditor.this, ViewListContents.class);
                startActivity(intent1);

            }
        });

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
}