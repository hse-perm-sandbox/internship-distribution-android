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

        // Добавляем обработку клика по строкам таблицы
        for (int i = 1; i < tableStudents.getChildCount(); i++) { // пропускаем заголовок
            if (tableStudents.getChildAt(i) instanceof android.widget.TableRow) {
                android.widget.TableRow row = (android.widget.TableRow) tableStudents.getChildAt(i);
                row.setOnClickListener(v -> {
                    // Сбросить выделение у всех строк
                    for (int j = 1; j < tableStudents.getChildCount(); j++) {
                        if (tableStudents.getChildAt(j) instanceof android.widget.TableRow) {
                            tableStudents.getChildAt(j).setBackgroundResource(0);
                        }
                    }
                    // Выделить выбранную строку
                    row.setBackgroundResource(R.color.lemon_yellow);
                    downloadCVBtn.setEnabled(true);
                    downloadCVBtn.setBackgroundResource(R.drawable.button_background);
                    downloadCVBtn.setTextColor(getResources().getColor(android.R.color.white));
                });
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}