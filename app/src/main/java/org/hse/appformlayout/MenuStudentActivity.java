package org.hse.appformlayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuStudentActivity extends AppCompatActivity {

    Button leaveBtn;
    Button companyBtn;
    Button uploadCvBtn;
    Button prioritesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_student);


        // Кнопка выхода (справа сверху рядом с именем студента)
        leaveBtn = findViewById(R.id.student_leave_btn);

        // Кнопка просмотра всех компаний
        companyBtn = findViewById(R.id.student_company_btn);

        // Кнопка загрузки резюме
        uploadCvBtn = findViewById(R.id.student_cv_btn);

        // Кнопка расстановки приоритетов
        prioritesBtn = findViewById(R.id.student_priorities_btn);

        companyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuStudentActivity.this, ViewCompaniesActivity.class);
                startActivity(intent);
            }
        });

        uploadCvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuStudentActivity.this, LoadCVActivity.class);
                startActivity(intent);
            }
        });

        prioritesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuStudentActivity.this, CompanyPrioritiesActivity.class);
                startActivity(intent);
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuStudentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}