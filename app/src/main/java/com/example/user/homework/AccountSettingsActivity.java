package com.example.user.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class AccountSettingsActivity extends AppCompatActivity {

    private EditText edtOldPass;
    private EditText edtNewPass;
    private EditText edtNewName;
    private EditText edtNewSurname;
    private EditText edtRepeatNewPass;

    private String uid;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        uid = user != null ? user.getUid() : "";
        edtNewName = findViewById(R.id.edt_change_new_name);
        edtNewSurname = findViewById(R.id.edt_change_new_surname) ;
        edtNewPass = findViewById(R.id.edt_change_new_pass);
        edtOldPass = findViewById(R.id.edt_change_old_pass);
        edtRepeatNewPass = findViewById(R.id.edt_repeat_new_pass);

        findViewById(R.id.btn_change_pass).setOnClickListener(v -> changePassword());
        findViewById(R.id.btn_exit).setOnClickListener(v -> exit());
        findViewById(R.id.btn_change_inf).setOnClickListener(v -> changeUserInformation());
    }

    private void changeUserInformation() {
        reference.child("users").child(uid).child("userInformation").child("name").setValue(edtNewName.getText().toString());
        reference.child("users").child(uid).child("userInformation").child("surname").setValue(edtNewSurname.getText().toString());
        Toast.makeText(getApplicationContext(), "Данные успешно изменены", Toast.LENGTH_SHORT).show();
    }

    private void exit() {
        Toast.makeText(getApplicationContext(), "Вы вышли из аккаунта!", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        intent.putExtra("SIGNOUT", true);
        startActivity(intent);
    }

    private void changePassword() {
        if (user == null) {
            return;
        }
        final String password = edtNewPass.getText().toString();
        final String passwordRepeat = edtRepeatNewPass.getText().toString();
        if (password.equals(passwordRepeat)) {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final String email = user.getEmail();
            final String oldPassword = String.valueOf(edtOldPass.getText());
            auth.signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(password).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Попробуйте другой пароль", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
        }
    }
}
