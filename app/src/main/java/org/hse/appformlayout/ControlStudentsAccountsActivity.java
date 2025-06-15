package org.hse.appformlayout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ControlStudentsAccountsActivity extends AppCompatActivity {

    Button saveChangesBtn;
    Button addAccountBtn;
    Button deleteAccountBtn;
    EditText loginForm;
    EditText passwordForm;
    EditText studentNameForm;
    EditText studentGroupForm;
    TableLayout accountsTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control_students_accounts);

        accountsTable = findViewById(R.id.table_accounts);
        loginForm = findViewById(R.id.login_edit_form);
        passwordForm = findViewById(R.id.password_edit_form);
        studentNameForm = findViewById(R.id.name_of_student_edit_form);

        saveChangesBtn = findViewById(R.id.change_account_btn);
        addAccountBtn = findViewById(R.id.add_account_btn);
        deleteAccountBtn = findViewById(R.id.delete_account_btn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}