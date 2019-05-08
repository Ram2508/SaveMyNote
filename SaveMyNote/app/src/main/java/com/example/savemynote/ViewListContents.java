package com.example.savemynote;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class ViewListContents extends AppCompatActivity {

    DatabaseHelper db;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter listAdapter;
    ListView listView;
    ImageView addButton;
    EditText searchText;
    Cursor data;
    String[] strArray,pathArray;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contents_layout);
    }


    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView)findViewById(R.id.listview);
        addButton = (ImageView)findViewById(R.id.add_image);
        searchText = (EditText)findViewById(R.id.search_text);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewListContents.this, TextEditor.class);
                intent.putExtra("POSITION", position);
                intent.putExtra("NOTE", list.get(position));
                intent.putExtra("PATH", pathArray[position]);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewListContents.this, MainActivity.class);
                startActivity(intent);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                populateListView();

                if(!s.toString().equals(""))
                {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerForContextMenu(listView);
        openDB();
        populateListView();
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
        if(!data.isClosed())
            data.close();
        finish();
    }

    public void openDB(){
        db = new DatabaseHelper(this);
    }
    public void populateListView(){

        list.clear();
        data = db.getAllData();
        if( data.getCount() != 0)
            {
            strArray = new String[data.getCount()];
            pathArray = new String[data.getCount()];
            int i=0;
            while(data.moveToNext()) {
                list.add(data.getString(1));
                strArray[i] = data.getString(1);
                pathArray[i] = data.getString(2);
                i++;
            }
                listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
                listView.setAdapter(listAdapter);

        }
        if(!data.isClosed())
            data.close();
    }

    public void searchItem(String textToSearch)
    {

        if(strArray!=null) {
            for (String str : strArray) {
                if (!str.contains(textToSearch))
                    list.remove(str);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.floating_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

       AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String note = (String) listAdapter.getItem(info.position);
        Cursor cd = db.getAllData();
        int id = -1;
        while(cd.moveToNext())
        {
           String x = cd.getString(1);
           if(x.equals(note)){
               id = cd.getInt(0);
               break;
           }
        }

        cd.close();
        switch (item.getItemId()) {
            case R.id.id_delete: {
                deleteCheckedItems(id, info.position);
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViewListContents.this);
        builder.setCancelable(true);
        builder.setMessage("Are you sure want to exit ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteCheckedItems(int id, int position)
    {
        String str;
        if((str = (String)listAdapter.getItem(position)) != null) {
            listAdapter.remove(str);
            listAdapter.notifyDataSetChanged();

            File file = new File(pathArray[position]);
            if (file.exists()) {
                file.delete();
            }
            db.deleteData(String.valueOf(id));
            refreshData();
        }
    }
    public void refreshData()
    {
            list.clear();
            data = db.getAllData();
            if(data.getCount() != 0){
                strArray = new String[data.getCount()];
                pathArray = new String[data.getCount()];
                int i=0;
                while(data.moveToNext()) {
                    list.add(data.getString(1));
                    strArray[i] = data.getString(1);
                    pathArray[i] = data.getString(2);
                    i++;
                }

            }
            if(!data.isClosed())
                data.close();
    }


}
