package org.hse.appformlayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.Permission;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.apache.commons.io.IOUtils;

public class LoadCVActivity extends AppCompatActivity {


    public static final String TAG="LoadCVActivity";

    public static final String URLGETCURSTUDENT="https://internship-distribution.hse-perm.ru/api/Student/me";

    private final OkHttpClient client =new OkHttpClient();

    int curStudId =-1;

    Uri uri=null;

    private static int REQUEST_PERMISSION_READ=9;

    private static int REQUEST_PERMISSION_WRITE=10;


    // Код запроса для выбора PDF-файла
    private static final int PICK_PDF_FILE = 1;

    // Кнопки и текстовое поле для отображения имени файла
    Button choosePdfBtn;
    TextView cvFilename;
    Button uploadBtn;
    Button downloadBtn;
    Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_load_cv_activity);

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

        // Инициализация кнопок и текстового поля по id из layout
        choosePdfBtn = findViewById(R.id.cv_btn_choose_pdf);
        cvFilename = findViewById(R.id.cv_filename);
        uploadBtn = findViewById(R.id.cv_btn_upload);
        downloadBtn = findViewById(R.id.cv_btn_download);
        deleteBtn = findViewById(R.id.cv_btn_delete);

        // Обработчик нажатия на кнопку "Выбрать PDF-файл"
        choosePdfBtn.setOnClickListener(v -> openFilePicker());

        // Обработчик нажатия на кнопку "Загрузить"
        uploadBtn.setOnClickListener(v -> loadCV());

        // Обработчик нажатия на кнопку "Скачать резюме"
        downloadBtn.setOnClickListener(v -> getCV());

        // Обработчик нажатия на кнопку "Удалить резюме"
        deleteBtn.setOnClickListener(v -> deleteCV());

        // Установка отступов для корректного отображения на устройствах с вырезами/панелями
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Открывает системный файловый менеджер для выбора PDF-файла
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Запрос на открытие документа
        intent.setType("application/pdf"); // Только PDF-файлы
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Только открываемые файлы
        startActivityForResult(intent, PICK_PDF_FILE); // Запуск с ожиданием результата
    }

    // Обработка результата выбора файла
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Проверяем, что это наш запрос и что пользователь выбрал файл
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.getData(); // Получаем Uri выбранного файла
                String fileName = getFileName(uri); // Получаем имя файла
                cvFilename.setText(fileName != null ? fileName : "Файл выбран"); // Показываем имя файла

                
                // Здесь можно сохранить uri для дальнейшей отправки на сервер
            }
        }
    }

    // Получает имя файла по его Uri (для отображения пользователю)
    private String getFileName(Uri uri) {
        String result = null;
        // Если файл выбран через content provider
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex); // Имя файла
                    }
                }
            }
        }
        // Если не удалось получить имя, используем последний сегмент пути
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    //Загрузить резюме на сервер
    private void loadCV() {
        if(uri==null)
        {
            Toast.makeText(LoadCVActivity.this,"Не выбран файл для загрузки!", Toast.LENGTH_SHORT).show();
            return;
        }

        String path2= getFileName(uri);


        //Создаем тело запроса
        byte[] bytes=new byte[0];
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            bytes = IOUtils.toByteArray(is);
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage());
        }
        RequestBody requestBodyFromBytes=RequestBody.create(bytes);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(MultipartBody.Part.createFormData("file",URLEncoder.encode(path2),requestBodyFromBytes))
                .build();

        //Делаем запрос на загрузку
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().
                url("https://internship-distribution.hse-perm.ru/api/Student/"+ curStudId +"/resume")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.post(requestBody).build();
        Call call=client.newCall(request);
        Toast.makeText(LoadCVActivity.this,"Идет загрузка резюме на сервер!", Toast.LENGTH_SHORT).show();
        try (Response response = call.execute()){




            //Какая-то ошибка
            if (!response.isSuccessful()) {

                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            Toast.makeText(LoadCVActivity.this,"Резюме загружено на сервер!", Toast.LENGTH_SHORT).show();



        }
        catch (Exception ex)
        {
            Toast.makeText(LoadCVActivity.this,"Ошибка при загрузке резюме!", Toast.LENGTH_SHORT).show();
            Log.wtf(TAG,ex);
            return;
        }
    }

    //Скачать резюме
    private void getCV() {




        //Делаем запрос на скачивание
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().
                url("https://internship-distribution.hse-perm.ru/api/Student/"+ curStudId +"/resume")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){


            //На сервере нет резюме
            if(response.code()==404)
            {
                Toast.makeText(LoadCVActivity.this,"Нельзя скачать: на сервере нет резюме!", Toast.LENGTH_SHORT).show();
                return;
            }


            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            Toast.makeText(LoadCVActivity.this,"Идет скачивание резюме!", Toast.LENGTH_SHORT).show();

            //Сохраняем резюме в файл
            String content_disposition=response.header("content-disposition");
            String fileName=java.net.URLDecoder.decode(content_disposition.substring(content_disposition.indexOf("''")+2), "UTF-8");
            FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Download/"+fileName);
            fos.write(response.body().bytes());
            fos.close();

            Toast.makeText(LoadCVActivity.this,"Резюме сохранено в файл /storage/emulated/0/Download/"+fileName, Toast.LENGTH_SHORT).show();

        }
        catch (Exception ex)
        {
            return;
        }
    }

    //Удалить резюме с сервера
    private void deleteCV() {
        //Делаем запрос на удаление
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().
                url("https://internship-distribution.hse-perm.ru/api/Student/"+ curStudId +"/resume")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.delete().build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){


            //На сервере нет резюме
            if(response.code()==400)
            {
                Toast.makeText(LoadCVActivity.this,"Нельзя удалить: на сервере нет резюме!", Toast.LENGTH_SHORT).show();
                return;
            }


            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            Toast.makeText(LoadCVActivity.this,"Резюме удалено с сервера!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            return;
        }
    }






}
