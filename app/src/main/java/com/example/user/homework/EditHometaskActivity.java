package com.example.user.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.user.homework.utils.UiUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditHometaskActivity extends AppCompatActivity {

    private String task;
    private EditText edtHometask;
    private int number;
    private String groupId;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hometask);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        String lesson = bundle.getString("LessonModel");
        groupId = bundle.getString("GROUPID");
        assert groupId != null;
        String day = bundle.getString("Day");
        number = bundle.getInt("LessonModel number");
        if (day == null) {
            return;
        }
        day = day.substring(0, 2) + day.substring(3, 5) + day.substring(6);

        edtHometask = findViewById(R.id.add_edt_task);
        TextView txtLesson = findViewById(R.id.chosen_lesson_name);
        ImageButton btnBack = findViewById(R.id.add_bar_home);
        Button btnAdd = findViewById(R.id.add_btn_add);
        TextView txtDate = findViewById(R.id.chosen_date);
        ImageButton btnAddAttachments = findViewById(R.id.add_attachments);
        txtDate.setText(day);
        txtLesson.setText(lesson);

        reference = reference.child(groupId);
        reference = reference.child("task").child(day).child(String.valueOf(number));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(String.class);
                edtHometask.setText(task);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GroupViewActivity.class);
            intent.putExtra("GROUPID", groupId);
            startActivityForResult(intent, 0);
        });
        btnAdd.setOnClickListener(v -> {
            String task = edtHometask.getText().toString();
            if (number == -1) {
                UiUtils.say(this, R.string.not_chosen_lesson);
                return;
            }
            reference.setValue(task);
            UiUtils.say(this, R.string.task_added);
            Intent intent = new Intent(this, GroupViewActivity.class);
            intent.putExtra("GROUPID", groupId);
            startActivityForResult(intent, 0);

        });
        btnAddAttachments.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });
    }
}
