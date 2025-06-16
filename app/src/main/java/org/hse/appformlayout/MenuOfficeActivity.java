package org.hse.appformlayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MenuOfficeActivity extends AppCompatActivity {


    public final static String TAG="MenuOfficeActivity";

    TextView nameLabel;

    Button leaveBtn;
    Button companyBtn;
    Button studentsListBtn;
    Button menuSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_office);

        //Текст с именем пользователя
        nameLabel=findViewById(R.id.name_label);

        // Кнопка выхода (справа сверху рядом с именем сотрудника)
        leaveBtn = findViewById(R.id.worker_leave_btn);

        // Кнопка просмотра всех компаний
        companyBtn = findViewById(R.id.office_company_btn);

        // Кнопка списка студентов
        studentsListBtn = findViewById(R.id.office_list_of_students);

        // Кнопка настройки УЗ студентов (пока не реализована)
        menuSettingsBtn = findViewById(R.id.office_menu_settings_btn);

        // Переход на главный экран (выход)
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuOfficeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Переход к просмотру компаний
        companyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuOfficeActivity.this, OfficeViewCompaniesActivity.class);
                startActivity(intent);
            }
        });

        // Переход к списку студентов
        studentsListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuOfficeActivity.this, StudentsTableActivity.class);
                startActivity(intent);
            }
        });

        // Переход к управлению аккаунтами студентов
        menuSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuOfficeActivity.this, ControlStudentsAccountsActivity.class);
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