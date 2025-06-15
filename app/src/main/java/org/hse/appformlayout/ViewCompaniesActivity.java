package org.hse.appformlayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewCompaniesActivity extends AppCompatActivity {
    Spinner companiesSpinner;
    TextView viewCompanyName;
    TextView viewCompanyDescription;
    Button prioritiesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_companies);

        // Спиннер с компаниями, по которым студент может посмотреть информацию
        companiesSpinner = findViewById(R.id.spinner_companies);

        // Название и информация о компании
        viewCompanyName = findViewById(R.id.view_company_name);
        viewCompanyDescription = findViewById(R.id.view_company_description);

        // Кнопка перехода к выборам приоритетов компаний,
        // куда студент хочет отправить заявку на практику
        prioritiesBtn = findViewById(R.id.company_priorities_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.companies_spinner_item,
                getResources().getStringArray(R.array.company_vars_example));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        companiesSpinner.setAdapter(adapter);

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

        prioritiesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCompaniesActivity.this, CompanyPrioritiesActivity.class);
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