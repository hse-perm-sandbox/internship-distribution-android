package org.hse.appformlayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.os.StrictMode;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";

    public static final String URL="https://internship-distribution.hse-perm.ru/api/auth/login";

    private final OkHttpClient client =new OkHttpClient();

    EditText login;
    EditText password;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 2 поля с логином и паролем
        login = findViewById(R.id.main_loginText);
        password = findViewById(R.id.main_passwordText);

        // кнопка для ввода логина и пароля
        loginButton = findViewById(R.id.main_loginButton);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TryAuthorize();
           }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    protected void TryAuthorize()
    {
        String loginText = login.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        //Делаем запрос
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("email", loginText);
            jsonObject.put("password", passwordText);
        }
        catch (Exception ex)
        {

        }
        RequestBody rb=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());
        Request.Builder requestBuilder=new Request.Builder().url(URL).post(rb);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){

            //Неверный логин или пароль
            if(response.code()==401 ||response.code()==415){
                Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                Log.wtf(TAG,response.body().string());
                return;
            }

            //Какая-то другая ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем токен от сервера
            Gson gson=new Gson();
            ResponseBody body=response.body();
            if(body==null){
                return;
            }
            String string=body.string();
            JsonObject jsonObj= gson.fromJson(string, JsonObject.class);
            String token=jsonObj.get("token").getAsString();

            //Добавляем токен в SharedPreferences
            SharedPreferences.Editor editor = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE).edit();
            editor.putString("token", token);
            editor.apply();
        }
        catch (Exception ex)
        {
            return;
        }

        //Вытаскиваем инфу о роли пользователя из токена
        String token=this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        String stringWithUserRole= new String(Base64.getDecoder().decode(token.split("\\.")[1]));
        JsonObject jsonObj= new Gson().fromJson(stringWithUserRole, JsonObject.class);
        String role=jsonObj.get("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").getAsString();


        // Идем в окно для студента
        if (role.equals("Student")) {
            Intent intent = new Intent(MainActivity.this, MenuStudentActivity.class);
            startActivity(intent);
        }
        // Идем в окно для менеджера
        else if (role.equals("Manager")) {
            Intent intent = new Intent(MainActivity.this, MenuOfficeActivity.class);
            startActivity(intent);
        }
    }
}