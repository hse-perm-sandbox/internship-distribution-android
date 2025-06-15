package org.hse.appformlayout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OfficeViewCompaniesActivity extends AppCompatActivity {

    Spinner companiesSpinner;
    TextView viewCompanyName;
    TextView viewCompanyDescription;
    Button addCompanyBtn;
    Button deleteCompanyBtn;
    Button saveChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_office_view_companies);

        // Спиннер с компаниями
        companiesSpinner = findViewById(R.id.office_spinner_companies);

        // Название и информация о компании
        viewCompanyName = findViewById(R.id.office_view_company_name);
        viewCompanyDescription = findViewById(R.id.office_view_company_description);

        // Кнопки управления компаниями
        addCompanyBtn = findViewById(R.id.company_add_btn);
        deleteCompanyBtn = findViewById(R.id.company_delete_btn);
        saveChangesBtn = findViewById(R.id.company_save_changes_btn);

        // --- Добавлено: настройка спиннера компаний ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.companies_spinner_item,
                getResources().getStringArray(R.array.company_vars_example)
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        companiesSpinner.setAdapter(adapter);
        // --- Конец добавленного ---

        // Обновление названия компании при выборе из списка
        companiesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCompany = (String) parent.getItemAtPosition(position);
                viewCompanyName.setText(selectedCompany);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ничего не делаем
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}