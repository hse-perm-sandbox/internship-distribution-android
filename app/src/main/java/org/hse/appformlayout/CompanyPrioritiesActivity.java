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

public class CompanyPrioritiesActivity extends AppCompatActivity {

    Button saveBtn;
    Spinner companiesTop;
    Spinner companiesMid;
    Spinner companiesLow;
    TextView statusTop;
    TextView statusMid;
    TextView statusLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_priorities);

        // Кнопка сохранения компаний, которые выбрал студент
        saveBtn = findViewById(R.id.priorities_btn_save);

        // Спиннеры с выбором компаний, куда нужно отправить заявку
        statusTop = findViewById(R.id.priorities_status_top);
        statusMid = findViewById(R.id.prioties_status_mid);
        statusLow = findViewById(R.id.priorities_status_low);

        // Статус заявки в компанию
        companiesTop = findViewById(R.id.priorities_spinner_top);
        companiesMid = findViewById(R.id.priorities_spinner_mid);
        companiesLow = findViewById(R.id.priorities_spinner_low);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.companies_spinner_item,
                getResources().getStringArray(R.array.company_vars_example));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        companiesTop.setAdapter(adapter);
        companiesMid.setAdapter(adapter);
        companiesLow.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}