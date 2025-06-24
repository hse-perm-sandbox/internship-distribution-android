package org.hse.appformlayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StudentsTableActivity extends AppCompatActivity {

    private class rowData
    {
        String fio="";
        String company="";
        Integer priority=0;
        Integer companyId=0;
        Integer studentId=-1;

        public rowData(String fio,String company, int priority, int companyId,int studentId)
        {
            this.fio=fio;
            this.company=company;
            this.priority=priority;
            this.companyId=companyId;
            this.studentId=studentId;
        }
    }

    Button downloadCVBtn;
    Spinner spinnerCVStatus;
    TableLayout tableStudents;

    Integer selectedStudentId=-1;

    public static final String URLGETCOMPANIESLIST="https://internship-distribution.hse-perm.ru/api/Companies";

    private final OkHttpClient client =new OkHttpClient();

    ArrayList<Integer> companiesIds=new ArrayList<>();

    ArrayList<String> companiesNames=new ArrayList<>();

    ArrayList<rowData> filteredEntries=new ArrayList<>();

    ArrayList<rowData> allEntries=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_table);

        tableStudents = findViewById(R.id.table_students);
        downloadCVBtn = findViewById(R.id.table_students_download_cv_btn);
        spinnerCVStatus = findViewById(R.id.students_table_spinner_status);

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
        spinnerCVStatus.setAdapter(adapter);

        //Делаем запрос на получение списка резюме
        requestBuilder=new Request.Builder().url(
                "https://internship-distribution.hse-perm.ru/api/applications")
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
                Integer studentId=jsonObj.get("studentId").getAsInt();
                String fio="";
                Request.Builder rb2=new Request.Builder().url(
                        "https://internship-distribution.hse-perm.ru/api/Student/"+studentId
                        )
                        .addHeader("Authorization", authString);
                Request request2=rb2.build();
                Call call2=client.newCall(request2);
                Response responce2=call2.execute();
                String str=responce2.body().string();
                JsonObject fioGson=gson.fromJson(str,JsonObject.class);
                fio=fioGson.get("lastname").getAsString()+" "+fioGson.get("name").getAsString()+" "+
                        fioGson.get("fathername").getAsString();
                if(jsonObj.get("priority1Status").getAsString().equals("Направлено"))
                {
                    Integer companyId=jsonObj.get("priority1CompanyId").getAsInt();
                    allEntries.add(new rowData(fio,companiesNames.get(companiesIds.indexOf(companyId)),
                            1,companyId,studentId
                            ));
                }
                if(jsonObj.get("priority2Status").getAsString().equals("Направлено"))
                {
                    Integer companyId=jsonObj.get("priority2CompanyId").getAsInt();
                    allEntries.add(new rowData(fio,companiesNames.get(companiesIds.indexOf(companyId)),
                            2,companyId,studentId
                    ));
                }
                if(jsonObj.get("priority3Status").getAsString().equals("Направлено"))
                {
                    Integer companyId=jsonObj.get("priority3CompanyId").getAsInt();
                    allEntries.add(new rowData(fio,companiesNames.get(companiesIds.indexOf(companyId)),
                            3,companyId,studentId
                    ));
                }
            }
        }
        catch (Exception ex)
        {
            return;
        }

        //Инициализируем filteredEntries
        filteredEntries=new ArrayList<>();
        if(spinnerCVStatus.getSelectedItemPosition()!=-1)
        {
            Integer compId=companiesIds.get(spinnerCVStatus.getSelectedItemPosition());
            for(rowData rd:allEntries)
            {
                if(rd.companyId==compId)
                    filteredEntries.add(rd);
            }
        }

        //Изменяем таблицу
        tableStudents.removeViews(1, Math.max(0, tableStudents.getChildCount() - 1)); //Удаляем все кроме заголовка
        for(rowData rd:filteredEntries)
        {
            TableRow tr=new TableRow(StudentsTableActivity.this);
            TextView fioView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
            fioView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            TextView companyView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
            companyView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            TextView priorityView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
            priorityView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            fioView.setText(rd.fio);
            priorityView.setText(rd.priority.toString());
            companyView.setText(rd.company);
            tr.addView(fioView);
            tr.addView(companyView);
            tr.addView(priorityView);
            tableStudents.addView(tr);
        }

        // Добавляем обработку клика по строкам таблицы
        for (int i = 1; i < tableStudents.getChildCount(); i++) { // пропускаем заголовок
            if (tableStudents.getChildAt(i) instanceof android.widget.TableRow) {
                android.widget.TableRow row = (android.widget.TableRow) tableStudents.getChildAt(i);
                row.setOnClickListener(v -> {
                    // Сбросить выделение у всех строк
                    for (int j = 1; j < tableStudents.getChildCount(); j++) {
                        if (tableStudents.getChildAt(j) instanceof android.widget.TableRow) {
                            tableStudents.getChildAt(j).setBackgroundResource(0);
                            if(tableStudents.getChildAt(j)==row)
                            {
                                selectedStudentId=filteredEntries.get(j-1).studentId;
                            }
                        }

                    }
                    // Выделить выбранную строку
                    row.setBackgroundResource(R.color.lemon_yellow);
                    downloadCVBtn.setEnabled(true);
                    downloadCVBtn.setBackgroundResource(R.drawable.button_background);
                    downloadCVBtn.setTextColor(getResources().getColor(android.R.color.white));
                });
            }
        }


        downloadCVBtn.setBackgroundResource(R.drawable.rounded_white_spinner);
        downloadCVBtn.setTextColor(Color.parseColor("#888888"));
        //Добавляем обработчик нажатия на кнопку загрузки
        downloadCVBtn.setOnClickListener(v->download_CV());


        //Добавляем обработку изменения спиннера
        spinnerCVStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                downloadCVBtn.setEnabled(false);
                downloadCVBtn.setBackgroundResource(R.drawable.rounded_white_spinner);
                downloadCVBtn.setTextColor(Color.parseColor("#888888"));
                Integer compId =companiesIds.get(position);
                //Инициализируем filteredEntries
                filteredEntries=new ArrayList<>();
                if(spinnerCVStatus.getSelectedItemPosition()!=-1)
                {
                    for(rowData rd:allEntries)
                    {
                        if(rd.companyId==compId)
                            filteredEntries.add(rd);
                    }
                }

                //Изменяем таблицу
                tableStudents.removeViews(1, Math.max(0, tableStudents.getChildCount() - 1)); //Удаляем все кроме заголовка
                for(rowData rd:filteredEntries)
                {
                    TableRow tr=new TableRow(StudentsTableActivity.this);
                    TextView fioView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
                    fioView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                    TextView companyView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
                    companyView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                    TextView priorityView=new TextView(StudentsTableActivity.this, null, 0, R.style.PlainStudentsTableText);
                    priorityView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                    fioView.setText(rd.fio);
                    priorityView.setText(rd.priority.toString());
                    companyView.setText(rd.company);
                    tr.addView(fioView);
                    tr.addView(companyView);
                    tr.addView(priorityView);
                    tableStudents.addView(tr);
                }

                // Добавляем обработку клика по строкам таблицы
                for (int i = 1; i < tableStudents.getChildCount(); i++) { // пропускаем заголовок
                    if (tableStudents.getChildAt(i) instanceof android.widget.TableRow) {
                        android.widget.TableRow row = (android.widget.TableRow) tableStudents.getChildAt(i);
                        row.setOnClickListener(v -> {
                            // Сбросить выделение у всех строк
                            for (int j = 1; j < tableStudents.getChildCount(); j++) {
                                if (tableStudents.getChildAt(j) instanceof android.widget.TableRow) {
                                    tableStudents.getChildAt(j).setBackgroundResource(0);
                                    if(tableStudents.getChildAt(j)==row)
                                    {
                                        selectedStudentId=filteredEntries.get(j-1).studentId;
                                    }
                                }

                            }
                            // Выделить выбранную строку
                            row.setBackgroundResource(R.color.lemon_yellow);
                            downloadCVBtn.setEnabled(true);
                            downloadCVBtn.setBackgroundResource(R.drawable.button_background);
                            downloadCVBtn.setTextColor(getResources().getColor(android.R.color.white));
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                downloadCVBtn.setEnabled(false);
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void download_CV()
    {
        if(selectedStudentId<=-1)
        {
            Toast.makeText(StudentsTableActivity.this,"Нельзя скачать: не выбрана строчка таблицы!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Скачиваем резюме

        //Делаем запрос на скачивание
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().
                url("https://internship-distribution.hse-perm.ru/api/Student/"+ selectedStudentId +"/resume")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){


            //На сервере нет резюме
            if(response.code()==404)
            {
                Toast.makeText(StudentsTableActivity.this,"Нельзя скачать: на сервере нет резюме!", Toast.LENGTH_SHORT).show();
                return;
            }


            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            Toast.makeText(StudentsTableActivity.this,"Идет скачивание резюме!", Toast.LENGTH_SHORT).show();

            //Сохраняем резюме в файл
            String content_disposition=response.header("content-disposition");
            String fileName=java.net.URLDecoder.decode(content_disposition.substring(content_disposition.indexOf("''")+2), "UTF-8");
            FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Download/"+fileName);
            fos.write(response.body().bytes());
            fos.close();

            Toast.makeText(StudentsTableActivity.this,"Резюме сохранено в файл /storage/emulated/0/Download/"+fileName, Toast.LENGTH_SHORT).show();

        }
        catch (Exception ex)
        {
            return;
        }
    }
}