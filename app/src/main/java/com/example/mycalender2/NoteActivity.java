package com.example.mycalender2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private ListView noteListView;
    private Button addBtn;

    private List<NoteInfo> noteList = new ArrayList<>();
    private ListAdapter mListAdapter;

    private static NoteDataBaseHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_note);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();

        dbHelper = new NoteDataBaseHelper(this,"MyNote.db",null,1);

        initView();
        setListener();

        if (intent != null){
            getNoteList();
            mListAdapter.refreshDataSet();
        }

    }
    private void initView(){
        noteListView = findViewById(R.id.note_list);
        addBtn = findViewById(R.id.add);
        //获取noteList
        getNoteList();
        mListAdapter = new ListAdapter(NoteActivity.this,noteList);
        noteListView.setAdapter(mListAdapter);
    }
    //设置监听器
    private void setListener(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteInfo noteInfo = noteList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("noteInfo",(Serializable)noteInfo);
                intent.putExtras(bundle);
                intent.setClass(NoteActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final NoteInfo noteInfo = noteList.get(position);
                String title = "警告";
                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle(title)
                        .setMessage("确定要删除吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note.deleteNote(dbHelper,Integer.parseInt(noteInfo.getId()));
                                noteList.remove(position);
                                mListAdapter.refreshDataSet();
                                Toast.makeText(NoteActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return true;
            }
        });
    }
    private void getNoteList(){
        noteList.clear();
        Cursor allNotes = Note.getAllNotes(dbHelper);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteList.add(noteInfo);
        }
    }

    public void back_main(View view) {
        Intent intent = new Intent(NoteActivity.this,MainActivity.class);
        startActivity(intent);
    }
    //给其他类提供dbHelper
    public static NoteDataBaseHelper getDbHelper() {
        return dbHelper;
    }


}