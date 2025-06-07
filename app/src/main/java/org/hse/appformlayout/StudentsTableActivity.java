package org.hse.appformlayout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentsTableActivity extends AppCompatActivity {

    Button downloadCVBtn;
    Spinner spinnerCVStatus;
    TableLayout tableStudents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_table);

        tableStudents = findViewById(R.id.table_students);
        downloadCVBtn = findViewById(R.id.table_students_download_cv_btn);
        spinnerCVStatus = findViewById(R.id.students_table_spinner_status);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.companies_spinner_item,
                getResources().getStringArray(R.array.company_vars_example));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        spinnerCVStatus.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}