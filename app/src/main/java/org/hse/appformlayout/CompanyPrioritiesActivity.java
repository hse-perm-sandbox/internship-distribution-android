package org.hse.appformlayout;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CompanyPrioritiesActivity extends AppCompatActivity {


    public static final String TAG="LoadCVActivity";

    public static final String URLGETCURSTUDENT="https://internship-distribution.hse-perm.ru/api/Student/me";

    public static final String URLGETCOMPANIESLIST="https://internship-distribution.hse-perm.ru/api/Companies";

    private final OkHttpClient client =new OkHttpClient();

    private int curStudId=-1;

    int topCompanyAcceptedId=-1;

    int midCompanyAcceptedId=-1;

    int lowCompanyAcceptedId=-1;

    ArrayList<Integer> companiesIds=new ArrayList<>();

    ArrayList<String> companiesNames=new ArrayList<>();

    Hashtable<Integer,Integer> fromCompIdToSpinnerPos=new Hashtable();

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

        //Делаем запрос, получаем id текущего студента
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().url(URLGETCURSTUDENT)
                .addHeader("Authorization", authString);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем id студента
            Gson gson=new Gson();
            ResponseBody body=response.body();
            if(body==null){
                return;
            }
            String string=body.string();
            JsonObject jsonObj= gson.fromJson(string, JsonObject.class);
            curStudId =jsonObj.get("id").getAsInt();

        }
        catch (Exception ex)
        {
            return;
        }

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

        //Делаем запрос на получение списка компаний
        requestBuilder=new Request.Builder().url(URLGETCOMPANIESLIST)
                .addHeader("Authorization", authString);
        request=requestBuilder.build();
        call=client.newCall(request);
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
                fromCompIdToSpinnerPos.put(companiesIds.get(i),i);
            }
        }
        catch (Exception ex)
        {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.companies_spinner_item,
                companiesNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        companiesTop.setAdapter(adapter);
        companiesMid.setAdapter(adapter);
        companiesLow.setAdapter(adapter);

        //Делаем запрос на получение приоритетов для текущего студента
        requestBuilder=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/applications/by-student/"
                        +curStudId)
                .addHeader("Authorization", authString);
        request=requestBuilder.build();
        call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем приоритеты компаний
            Gson gson=new Gson();
            ResponseBody body=response.body();
            if(body==null){
                return;
            }

            JsonObject jsonObj= gson.fromJson(body.string(), JsonObject.class);
            int green = android.graphics.Color.parseColor("#388E3C");
            int white = android.graphics.Color.WHITE;
            String str1=jsonObj.get("priority1Status").getAsString();
            String str2=jsonObj.get("priority2Status").getAsString();
            String str3=jsonObj.get("priority3Status").getAsString();
            if(jsonObj.get("priority1Status").getAsString().equals("Направлено"))
            {
                topCompanyAcceptedId=jsonObj.get("priority1CompanyId").getAsInt();
                companiesTop.setSelection(fromCompIdToSpinnerPos.get(topCompanyAcceptedId));
                statusTop.setText(getString(R.string.status_sent));
                statusTop.setBackgroundResource(R.drawable.oval_status_background);
                statusTop.setTextColor(white);
                statusTop.getBackground().setTint(green);
            }
            if(jsonObj.get("priority2Status").getAsString().equals("Направлено"))
            {
                midCompanyAcceptedId=jsonObj.get("priority2CompanyId").getAsInt();
                companiesMid.setSelection(fromCompIdToSpinnerPos.get(midCompanyAcceptedId));
                statusMid.setText(getString(R.string.status_sent));
                statusMid.setBackgroundResource(R.drawable.oval_status_background);
                statusMid.setTextColor(white);
                statusMid.getBackground().setTint(green);
            }
            if(jsonObj.get("priority3Status").getAsString().equals("Направлено"))
            {
                lowCompanyAcceptedId=jsonObj.get("priority3CompanyId").getAsInt();
                companiesLow.setSelection(fromCompIdToSpinnerPos.get(lowCompanyAcceptedId));
                statusLow.setText(getString(R.string.status_sent));
                statusLow.setBackgroundResource(R.drawable.oval_status_background);
                statusLow.setTextColor(white);
                statusLow.getBackground().setTint(green);
            }
        }
        catch (Exception ex)
        {

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Обновление статуса компании и при выборе из списка
        companiesTop.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                //Меняем цвета и текст статуса
                if(topCompanyAcceptedId!=-1&&fromCompIdToSpinnerPos.get(topCompanyAcceptedId)==position)
                {
                    int green = android.graphics.Color.parseColor("#388E3C");
                    int white = android.graphics.Color.WHITE;
                    statusTop.setText(getString(R.string.status_sent));
                    statusTop.setBackgroundResource(R.drawable.oval_status_background);
                    statusTop.setTextColor(white);
                    statusTop.getBackground().setTint(green);
                }
                else
                {
                    int gray = android.graphics.Color.parseColor("#888888");
                    int white = android.graphics.Color.WHITE;
                    statusTop.setText(R.string.status_not_selected);
                    statusTop.setBackgroundResource(R.drawable.oval_status_background);
                    statusTop.setTextColor(white);
                    statusTop.getBackground().setTint(gray);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ничего не делаем
            }
        });
        companiesMid.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                //Меняем цвета и текст статуса
                if(midCompanyAcceptedId!=-1&&fromCompIdToSpinnerPos.get(midCompanyAcceptedId)==position)
                {
                    int green = android.graphics.Color.parseColor("#388E3C");
                    int white = android.graphics.Color.WHITE;
                    statusMid.setText(getString(R.string.status_sent));
                    statusMid.setBackgroundResource(R.drawable.oval_status_background);
                    statusMid.setTextColor(white);
                    statusMid.getBackground().setTint(green);
                }
                else
                {
                    int gray = android.graphics.Color.parseColor("#888888");
                    int white = android.graphics.Color.WHITE;
                    statusMid.setText(R.string.status_not_selected);
                    statusMid.setBackgroundResource(R.drawable.oval_status_background);
                    statusMid.setTextColor(white);
                    statusMid.getBackground().setTint(gray);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ничего не делаем
            }
        });
        companiesLow.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                //Меняем цвета и текст статуса
                if(lowCompanyAcceptedId!=-1&&fromCompIdToSpinnerPos.get(lowCompanyAcceptedId)==position)
                {
                    int green = android.graphics.Color.parseColor("#388E3C");
                    int white = android.graphics.Color.WHITE;
                    statusLow.setText(getString(R.string.status_sent));
                    statusLow.setBackgroundResource(R.drawable.oval_status_background);
                    statusLow.setTextColor(white);
                    statusLow.getBackground().setTint(green);
                }
                else
                {
                    int gray = android.graphics.Color.parseColor("#888888");
                    int white = android.graphics.Color.WHITE;
                    statusLow.setText(R.string.status_not_selected);
                    statusLow.setBackgroundResource(R.drawable.oval_status_background);
                    statusLow.setTextColor(white);
                    statusLow.getBackground().setTint(gray);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ничего не делаем
            }
        });

        saveBtn.setOnClickListener(v -> {
            //Не выбрана компания для какй-то позиции
            if(companiesTop.getSelectedItemPosition()==-1||
                    companiesMid.getSelectedItemPosition()==-1||companiesLow.getSelectedItemPosition()==-1)
            {
                Toast.makeText(CompanyPrioritiesActivity.this,"Не для всех позиций выбраны компании!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //Одинаковые компании на разных позициях
            if(companiesTop.getSelectedItemPosition()==companiesMid.getSelectedItemPosition()||
                    companiesMid.getSelectedItemPosition()==companiesLow.getSelectedItemPosition()||
                    companiesLow.getSelectedItemPosition()==companiesTop.getSelectedItemPosition())
            {
                Toast.makeText(CompanyPrioritiesActivity.this,"Невозможно отправить!Компания повторяется в нескольких позициях!",
                        Toast.LENGTH_SHORT).show();
                return;
            }





            //Делаем запрос на получение приоритетов для текущего студента
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String stringRB="{\"priority1CompanyId\":"+
                    companiesIds.get(companiesTop.getSelectedItemPosition()).toString()+",\"priority2CompanyId\":"+
                    companiesIds.get(companiesMid.getSelectedItemPosition()).toString()+",\"priority3CompanyId\":"+
                    companiesIds.get(companiesLow.getSelectedItemPosition()).toString()+"}";
            RequestBody body = RequestBody.create(JSON, stringRB);
            Request.Builder requestBuilder1=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/applications/"
                            +curStudId+"/priorities")
                    .addHeader("Authorization", authString);
            Request request1=requestBuilder1.patch(body).build();
            Call call1=client.newCall(request1);
            try (Response response = call1.execute()){
                //Еще не посланы приоритеты
                if(response.code()==404)
                {

                    String stringRB2="{\"studentId\":"+curStudId+",\"priority1CompanyId\":"+
                            companiesIds.get(companiesTop.getSelectedItemPosition()).toString()+",\"priority2CompanyId\":"+
                            companiesIds.get(companiesMid.getSelectedItemPosition()).toString()+",\"priority3CompanyId\":"+
                            companiesIds.get(companiesLow.getSelectedItemPosition()).toString()+"}";
                    RequestBody body2 = RequestBody.create(JSON, stringRB2);
                    Request.Builder requestBuilder2=new Request.Builder().url(
                            "https://internship-distribution.hse-perm.ru/api/applications/")
                            .addHeader("Authorization", authString);
                    Request request2=requestBuilder2.post(body2).build();
                    Call call2=client.newCall(request2);
                    call2.execute();
                }
                //Какая-то ошибка
                else if (!response.isSuccessful()) {
                    throw new IOException("Запрос к серверу не был успешен: " +
                            response.code() + " " + response.message());
                }

                Toast.makeText(CompanyPrioritiesActivity.this,"Приритеты отправлены на сервер!",
                        Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(CompanyPrioritiesActivity.this,"Произошла ошибка при отправке приоритетов на сервер!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            topCompanyAcceptedId=companiesIds.get(companiesTop.getSelectedItemPosition());
            midCompanyAcceptedId=companiesIds.get(companiesMid.getSelectedItemPosition());
            lowCompanyAcceptedId=companiesIds.get(companiesLow.getSelectedItemPosition());


            int green = android.graphics.Color.parseColor("#388E3C");
            int white = android.graphics.Color.WHITE;
            int radius = 50; // радиус для овала
            // Меняем статус, фон и текст
            statusTop.setText(getString(R.string.status_sent));
            statusTop.setBackgroundResource(R.drawable.oval_status_background);
            statusTop.setTextColor(white);
            statusTop.getBackground().setTint(green);

            statusMid.setText(getString(R.string.status_sent));
            statusMid.setBackgroundResource(R.drawable.oval_status_background);
            statusMid.setTextColor(white);
            statusMid.getBackground().setTint(green);

            statusLow.setText(getString(R.string.status_sent));
            statusLow.setBackgroundResource(R.drawable.oval_status_background);
            statusLow.setTextColor(white);
            statusLow.getBackground().setTint(green);
        });
    }
}