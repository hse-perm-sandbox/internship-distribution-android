package org.hse.appformlayout;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuOfficeActivity extends AppCompatActivity {

    Button leaveBtn;
    Button companyBtn;
    Button studentsListBtn;
    Button menuSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_office);

        // Кнопка выхода (справа сверху рядом с именем сотрудника)
        leaveBtn = findViewById(R.id.worker_leave_btn);

        // Кнопка просмотра всех компаний
        companyBtn = findViewById(R.id.office_company_btn);

        // Кнопка загрузки резюме
        studentsListBtn = findViewById(R.id.office_list_of_students);

        // Кнопка расстановки приоритетов
        menuSettingsBtn = findViewById(R.id.office_menu_settings_btn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}