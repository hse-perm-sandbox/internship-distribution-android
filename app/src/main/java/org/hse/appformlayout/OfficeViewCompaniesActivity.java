package org.hse.appformlayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OfficeViewCompaniesActivity extends AppCompatActivity {

    Spinner companiesSpinner;
    EditText viewCompanyName;
    EditText viewCompanyDescription;
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
        java.util.List<String> companiesList = new java.util.ArrayList<>(java.util.Arrays.asList(getResources().getStringArray(R.array.company_vars_example)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.companies_spinner_item,
                companiesList
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

        addCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Добавляем "Новая компания" в адаптер, если её ещё нет
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) companiesSpinner.getAdapter();
                String newCompany = getString(R.string.new_company);
                boolean hasNewCompany = false;
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(newCompany)) {
                        hasNewCompany = true;
                        break;
                    }
                }
                if (!hasNewCompany) {
                    adapter.add(newCompany);
                }
                companiesSpinner.setSelection(adapter.getPosition(newCompany));
                viewCompanyName.setText("");
                viewCompanyDescription.setText("");
                viewCompanyName.setHint(newCompany);
                viewCompanyDescription.setHint(getString(R.string.office_enter_company_description));
                viewCompanyName.setEnabled(true);
                viewCompanyDescription.setEnabled(true);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}