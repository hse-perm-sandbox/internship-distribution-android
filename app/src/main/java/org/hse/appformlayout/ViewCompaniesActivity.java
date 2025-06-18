package org.hse.appformlayout;

import android.content.Context;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ViewCompaniesActivity extends AppCompatActivity {

    /*
    //Класс с информацией о компании
    private static class CompanyInfo
    {
        String name;
        String descr;
        int id;
        public CompanyInfo(int id,String name, String descr)
        {
            this.id=id;
            this.name=name;
            this.descr=descr;
        }
    }
    */

    public final static String TAG="ViewCompaniesActivity";
    String URL="https://internship-distribution.hse-perm.ru/api/Companies";

    private final OkHttpClient client =new OkHttpClient();

    Spinner companiesSpinner;
    TextView viewCompanyName;
    TextView viewCompanyDescription;
    Button prioritiesBtn;

    ArrayList<String> companiesNames=new ArrayList<>();

    ArrayList<String> companiesDescr=new ArrayList<>();

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

            //Получаем список компаний
            Gson gson=new Gson();
            ResponseBody body=response.body();
            if(body==null){
                return;
            }

            JSONArray jsonArray=new JSONArray(body.string());
            for(int i=0;i<jsonArray.length();i++)
            {
                String jsonStr=jsonArray.optJSONObject(i).toString();
                JsonObject jsonObj=gson.fromJson(jsonStr,JsonObject.class);
                companiesNames.add(jsonObj.get("name").getAsString());
                companiesDescr.add(jsonObj.get("description").getAsString());
            }



            //JsonObject jsonObj= gson.fromJson(string, JsonObject.class);
            //String name=jsonObj.get("lastname").getAsString()+" "+jsonObj.get("name").getAsString()+" "+jsonObj.get("fathername").getAsString();

        }
        catch (Exception ex)
        {
            return;
        }





        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.companies_spinner_item,companiesNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        companiesSpinner.setAdapter(adapter);

        //Если есть компании, изменяем описание
        if(!companiesDescr.isEmpty())
        {
            viewCompanyDescription.setText(companiesDescr.get(0));
        }

        // Обновление названия компании и её описания при выборе из списка
        companiesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCompany = (String) parent.getItemAtPosition(position);
                viewCompanyName.setText(selectedCompany);
                viewCompanyDescription.setText(companiesDescr.get(position));
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