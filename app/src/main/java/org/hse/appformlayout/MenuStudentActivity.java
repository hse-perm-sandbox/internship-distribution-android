package org.hse.appformlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MenuStudentActivity extends AppCompatActivity {


    public final static String TAG="MenuStudentActivity";
    String URL="https://internship-distribution.hse-perm.ru/api/Student/me";

    private final OkHttpClient client =new OkHttpClient();

    TextView nameLabel;

    Button leaveBtn;
    Button companyBtn;
    Button uploadCvBtn;
    Button prioritesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_student);

        //Текст с именем пользователя
        nameLabel=findViewById(R.id.name_label);

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

        //Делаем запрос
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().url(URL)
                .addHeader("Authorization", authString);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем имя пользователя
            Gson gson=new Gson();
            ResponseBody body=response.body();
            if(body==null){
                return;
            }
            String string=body.string();
            JsonObject jsonObj= gson.fromJson(string, JsonObject.class);
            String name=jsonObj.get("lastname").getAsString()+" "+jsonObj.get("name").getAsString()+" "+jsonObj.get("fathername").getAsString();
            nameLabel.setText(name);
        }
        catch (Exception ex)
        {
            return;
        }
    }
}