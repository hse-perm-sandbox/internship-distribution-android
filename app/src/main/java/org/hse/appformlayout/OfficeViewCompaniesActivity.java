package org.hse.appformlayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OfficeViewCompaniesActivity extends AppCompatActivity {

    Spinner companiesSpinner;
    EditText viewCompanyName;
    EditText viewCompanyDescription;
    Button addCompanyBtn;
    Button deleteCompanyBtn;
    Button saveChangesBtn;


    public static final String URLGETCOMPANIESLIST="https://internship-distribution.hse-perm.ru/api/Companies";

    public static final String URLPOSTCOMPANY="https://internship-distribution.hse-perm.ru/api/Companies";

    private final OkHttpClient client =new OkHttpClient();

    private int curStudId=-1;

    ArrayList<Integer> companiesIds=new ArrayList<>();

    ArrayList<String> companiesNames=new ArrayList<>();

    ArrayList<String> companiesDescs=new ArrayList<>();

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

        //Делаем запрос на получение списка компаний
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().url(URLGETCOMPANIESLIST)
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
                companiesIds.add(jsonObj.get("id").getAsInt());
                companiesDescs.add(jsonObj.get("description").getAsString());
            }
        }
        catch (Exception ex)
        {
            return;
        }



        // --- Добавлено: настройка спиннера компаний ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.companies_spinner_item,
                companiesNames
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
                viewCompanyDescription.setText(companiesDescs.get(position));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ничего не делаем
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Делаем запрос на изменение текущей компании
                int pos=companiesSpinner.getSelectedItemPosition();
                int id=companiesIds.get(pos);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String stringRB="{\"name\": \""+viewCompanyName.getText()+"\",\"description\": \""+
                        viewCompanyDescription.getText()+"\"}";
                RequestBody requestBody = RequestBody.create(JSON, stringRB);
                Request.Builder requestBuilder=new Request.Builder().url(
                        "https://internship-distribution.hse-perm.ru/api/Companies/"+id
                        )
                        .addHeader("Authorization", authString);
                Request request=requestBuilder.put(requestBody).build();
                Call call=client.newCall(request);
                try (Response response = call.execute()){
                    //Какая-то ошибка
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }

                    companiesNames.set(pos,viewCompanyName.getText().toString());
                    companiesDescs.set(pos,viewCompanyDescription.getText().toString());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            OfficeViewCompaniesActivity.this,
                            R.layout.companies_spinner_item,
                            companiesNames
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                    companiesSpinner.setAdapter(adapter);
                    companiesSpinner.setSelection(pos);
                }
                catch (Exception ex)
                {
                    Toast.makeText(OfficeViewCompaniesActivity.this,"Произошла ошибка при изменении компании!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(OfficeViewCompaniesActivity.this,"Компания изменена!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        addCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Делаем запрос на создание компании "Новая компания"
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String stringRB="{\"name\": \"Новая компания\",\"description\": \"Новое описание\"}";
                RequestBody requestBody = RequestBody.create(JSON, stringRB);
                Request.Builder requestBuilder=new Request.Builder().url(URLPOSTCOMPANY)
                        .addHeader("Authorization", authString);
                Request request=requestBuilder.post(requestBody).build();
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
                    JsonObject jsonObj=gson.fromJson(body.string(),JsonObject.class);
                    companiesNames.add(jsonObj.get("name").getAsString());
                    companiesIds.add(jsonObj.get("id").getAsInt());
                    companiesDescs.add(jsonObj.get("description").getAsString());

                }
                catch (Exception ex)
                {
                    Toast.makeText(OfficeViewCompaniesActivity.this,"Произошла ошибка при добавлении компании!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(OfficeViewCompaniesActivity.this,"Добавлена новая компания!",
                        Toast.LENGTH_SHORT).show();
            }

        });

        deleteCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=companiesSpinner.getSelectedItemPosition();
                int id=companiesIds.get(pos);
                //Делаем запрос на удаление выбранной сейчас компании
                Request.Builder requestBuilder=new Request.Builder().url(
                        "https://internship-distribution.hse-perm.ru/api/Companies/"+id)
                        .addHeader("Authorization", authString);
                Request request=requestBuilder.delete().build();
                Call call=client.newCall(request);
                try (Response response = call.execute()){
                    //Какая-то ошибка
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }


                    companiesNames.remove(pos);
                    companiesIds.remove(pos);
                    companiesDescs.remove(pos);
                    viewCompanyName.setText("");
                    viewCompanyDescription.setText("");
                    if(companiesDescs.size()>0)
                    {
                        companiesSpinner.setSelection(0);
                    }

                }
                catch (Exception ex)
                {
                    Toast.makeText(OfficeViewCompaniesActivity.this,"Произошла ошибка при удалении компании!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(OfficeViewCompaniesActivity.this,"Компания удалена!",
                        Toast.LENGTH_SHORT).show();

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}