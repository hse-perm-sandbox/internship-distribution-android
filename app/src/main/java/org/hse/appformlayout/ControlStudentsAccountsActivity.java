package org.hse.appformlayout;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ControlStudentsAccountsActivity extends AppCompatActivity {


    Button deleteAccountBtn;
    Button addAccountBtn;

    EditText loginForm;

    EditText nameForm;

    EditText lastnameForm;

    EditText fathernameForm;
    ArrayList<Integer>UserIds=new ArrayList<>();

    ArrayList<Integer>StudentIds=new ArrayList<>();

    ArrayList<String>StudentLogins=new ArrayList<>();

    ArrayList<String>StudentFIOS=new ArrayList<>();
    Integer selectedUserId =-1;
    private final OkHttpClient client =new OkHttpClient();
    TableLayout accountsTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control_students_accounts);

        //Делаем запрос на получение списка студентов
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/Student")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем список студентов
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
                StudentIds.add(jsonObj.get("id").getAsInt());
                StudentFIOS.add(jsonObj.get("lastname").getAsString()+" "+jsonObj.get("name").getAsString()
                        +" "+jsonObj.get("fathername").getAsString());
                UserIds.add(jsonObj.get("userId").getAsInt());
                JsonElement usr=jsonObj.get("user");
                if(usr!= JsonNull.INSTANCE) {
                    StudentLogins.add(usr.getAsJsonObject().get("email").getAsString());
                }
                else
                {
                    StudentLogins.add("null");
                }
            }
        }
        catch (Exception ex)
        {
            return;
        }

        addAccountBtn=findViewById(R.id.add_account_btn);
        loginForm=findViewById(R.id.login_edit_form);
        nameForm=findViewById(R.id.name_edit_form);
        lastnameForm=findViewById(R.id.lastname_edit_form);
        fathernameForm=findViewById(R.id.fathername_edit_form);
        accountsTable = findViewById(R.id.table_accounts);
        deleteAccountBtn = findViewById(R.id.delete_account_btn);

        deleteAccountBtn.setEnabled(false);
        deleteAccountBtn.setBackgroundResource(R.drawable.rounded_white_spinner);
        deleteAccountBtn.setTextColor(Color.parseColor("#888888"));
        //Добавляем обработчик нажатия на кнопку создания аккаунта
        addAccountBtn.setOnClickListener(v->addAccount());
        //Добавляем обработчик нажатия на кнопку загрузки
        deleteAccountBtn.setOnClickListener(v->deleteAccount());

        //Заполняем таблицу
        accountsTable.removeViews(1, Math.max(0, accountsTable.getChildCount() - 1)); //Удаляем все кроме заголовка
        for(int i=0;i<StudentIds.size();i++)
        {
            TableRow tr=new TableRow(ControlStudentsAccountsActivity.this);
            TextView fioView=new TextView(ControlStudentsAccountsActivity.this, null, 0, R.style.PlainStudentsTableText);
            fioView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            fioView.setText(StudentFIOS.get(i));
            tr.addView(fioView);
            accountsTable.addView(tr);
        }

        // Добавляем обработку клика по строкам таблицы
        for (int i = 1; i < accountsTable.getChildCount(); i++) { // пропускаем заголовок
            if (accountsTable.getChildAt(i) instanceof android.widget.TableRow) {
                android.widget.TableRow row = (android.widget.TableRow) accountsTable.getChildAt(i);
                row.setOnClickListener(v -> {
                    // Сбросить выделение у всех строк
                    for (int j = 1; j < accountsTable.getChildCount(); j++) {
                        if (accountsTable.getChildAt(j) instanceof android.widget.TableRow) {
                            accountsTable.getChildAt(j).setBackgroundResource(0);
                            if(accountsTable.getChildAt(j)==row)
                            {
                                selectedUserId = UserIds.get(j-1);
                            }
                        }

                    }
                    // Выделить выбранную строку
                    row.setBackgroundResource(R.color.lemon_yellow);
                    deleteAccountBtn.setEnabled(true);
                    deleteAccountBtn.setBackgroundResource(R.drawable.button_background);
                    deleteAccountBtn.setTextColor(getResources().getColor(android.R.color.white));
                });
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void deleteAccount()
    {
        if(selectedUserId<=-1)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Не выбран студент для удаления!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Делаем запрос на удаление студента
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        Request.Builder requestBuilder=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/User/"+selectedUserId)
                .addHeader("Authorization", authString);
        Request request=requestBuilder.delete().build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            Toast.makeText(ControlStudentsAccountsActivity.this,"Студент удален!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Произошла ошибка при удалении студента!", Toast.LENGTH_SHORT).show();
            return;
        }
        int pos=UserIds.indexOf(selectedUserId);
        StudentIds.remove(pos);
        UserIds.remove(pos);
        StudentFIOS.remove(pos);
        StudentLogins.remove(pos);


        deleteAccountBtn.setEnabled(false);
        deleteAccountBtn.setBackgroundResource(R.drawable.rounded_white_spinner);
        deleteAccountBtn.setTextColor(Color.parseColor("#888888"));
        //Заполняем таблицу
        accountsTable.removeViews(1, Math.max(0, accountsTable.getChildCount() - 1)); //Удаляем все кроме заголовка
        for(int i=0;i<StudentIds.size();i++)
        {
            TableRow tr=new TableRow(ControlStudentsAccountsActivity.this);
            TextView fioView=new TextView(ControlStudentsAccountsActivity.this, null, 0, R.style.PlainStudentsTableText);
            fioView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            fioView.setText(StudentFIOS.get(i));
            tr.addView(fioView);
            accountsTable.addView(tr);
        }

        // Добавляем обработку клика по строкам таблицы
        for (int i = 1; i < accountsTable.getChildCount(); i++) { // пропускаем заголовок
            if (accountsTable.getChildAt(i) instanceof android.widget.TableRow) {
                android.widget.TableRow row = (android.widget.TableRow) accountsTable.getChildAt(i);
                row.setOnClickListener(v -> {
                    // Сбросить выделение у всех строк
                    for (int j = 1; j < accountsTable.getChildCount(); j++) {
                        if (accountsTable.getChildAt(j) instanceof android.widget.TableRow) {
                            accountsTable.getChildAt(j).setBackgroundResource(0);
                            if(accountsTable.getChildAt(j)==row)
                            {
                                selectedUserId = UserIds.get(j-1);
                            }
                        }

                    }
                    // Выделить выбранную строку
                    row.setBackgroundResource(R.color.lemon_yellow);
                    deleteAccountBtn.setEnabled(true);
                    deleteAccountBtn.setBackgroundResource(R.drawable.button_background);
                    deleteAccountBtn.setTextColor(getResources().getColor(android.R.color.white));
                });
            }
        }
    }

    private void addAccount()
    {
        if(loginForm.getText().toString().indexOf('@')==-1||loginForm.getText().toString().indexOf('@')==loginForm.getText().toString().length()-1)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Некорректный адрес эл. почты в поле ввода логина!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(nameForm.getText().toString().length()==0
                ||lastnameForm.getText().toString().length()==0||
                fathernameForm.getText().toString().length()==0)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Поля для имен не могут быть пустыми!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Делаем запрос на регистрацию студента
        String authString="Bearer "+this.getSharedPreferences(getString
                (R.string.preferences_file), Context.MODE_PRIVATE).getString("token","!");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String stringRB="{\"students\":[{\"email\":\""+loginForm.getText()+"\",\"name\":\""+
                nameForm.getText()+"\",\"lastname\":\""+lastnameForm.getText()+"\",\"fathername\":\""+
                fathernameForm.getText()+"\"}]}";
        RequestBody requestBody = RequestBody.create(JSON, stringRB);
        Request.Builder requestBuilder=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/Student/bulk-create")
                .addHeader("Authorization", authString);
        Request request=requestBuilder.post(requestBody).build();
        Call call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }
            String pwd="";
            ResponseBody body=response.body();
            if(body==null)
            {
               throw new NullPointerException();
            }
            Gson gson=new Gson();
            JsonObject jsonObject=gson.fromJson(body.string(),JsonObject.class);
            JsonArray jsonArray=jsonObject.get("createdStudents").getAsJsonArray();
            if(jsonArray.size()==0)
            {
                Toast.makeText(ControlStudentsAccountsActivity.this,
                        "Для данного логина уже есть пользователь!", Toast.LENGTH_SHORT).show();
                return;
            }
            for(JsonElement jsObj:jsonArray)
            {
                if(jsObj==JsonNull.INSTANCE)
                {
                    throw new NullPointerException();
                }
                pwd=jsObj.getAsJsonObject().get("generatedPassword").getAsString();
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Новый студент был создан!");
            alert.setMessage("Его пароль: "+pwd);

            alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //ничего
                }
            });
            alert.show();
        }
        catch (Exception ex)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Произошла ошибка при добавлении студента!", Toast.LENGTH_SHORT).show();
            return;
        }
        StudentFIOS=new ArrayList<>();
        StudentLogins=new ArrayList<>();
        StudentIds=new ArrayList<>();
        UserIds=new ArrayList<>();

        //Делаем запрос на получение списка студентов
        requestBuilder=new Request.Builder().url("https://internship-distribution.hse-perm.ru/api/Student")
                .addHeader("Authorization", authString);
        request=requestBuilder.build();
        call=client.newCall(request);
        try (Response response = call.execute()){
            //Какая-то ошибка
            if (!response.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        response.code() + " " + response.message());
            }

            //Получаем список студентов
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
                StudentIds.add(jsonObj.get("id").getAsInt());
                StudentFIOS.add(jsonObj.get("lastname").getAsString()+" "+jsonObj.get("name").getAsString()
                        +" "+jsonObj.get("fathername").getAsString());
                UserIds.add(jsonObj.get("userId").getAsInt());
                JsonElement usr=jsonObj.get("user");
                if(usr!= JsonNull.INSTANCE) {
                    StudentLogins.add(usr.getAsJsonObject().get("email").getAsString());
                }
                else
                {
                    StudentLogins.add("null");
                }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(ControlStudentsAccountsActivity.this,"Произошла ошибка при загрузке списков студентов!", Toast.LENGTH_SHORT).show();
            return;
        }


        deleteAccountBtn.setEnabled(false);
        deleteAccountBtn.setBackgroundResource(R.drawable.rounded_white_spinner);
        deleteAccountBtn.setTextColor(Color.parseColor("#888888"));
        //Заполняем таблицу
        accountsTable.removeViews(1, Math.max(0, accountsTable.getChildCount() - 1)); //Удаляем все кроме заголовка
        for(int i=0;i<StudentIds.size();i++)
        {
            TableRow tr=new TableRow(ControlStudentsAccountsActivity.this);
            TextView fioView=new TextView(ControlStudentsAccountsActivity.this, null, 0, R.style.PlainStudentsTableText);
            fioView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            fioView.setText(StudentFIOS.get(i));
            tr.addView(fioView);
            accountsTable.addView(tr);
        }

        // Добавляем обработку клика по строкам таблицы
        for (int i = 1; i < accountsTable.getChildCount(); i++) { // пропускаем заголовок
            if (accountsTable.getChildAt(i) instanceof android.widget.TableRow) {
                android.widget.TableRow row = (android.widget.TableRow) accountsTable.getChildAt(i);
                row.setOnClickListener(v -> {
                    // Сбросить выделение у всех строк
                    for (int j = 1; j < accountsTable.getChildCount(); j++) {
                        if (accountsTable.getChildAt(j) instanceof android.widget.TableRow) {
                            accountsTable.getChildAt(j).setBackgroundResource(0);
                            if(accountsTable.getChildAt(j)==row)
                            {
                                selectedUserId = UserIds.get(j-1);
                            }
                        }

                    }
                    // Выделить выбранную строку
                    row.setBackgroundResource(R.color.lemon_yellow);
                    deleteAccountBtn.setEnabled(true);
                    deleteAccountBtn.setBackgroundResource(R.drawable.button_background);
                    deleteAccountBtn.setTextColor(getResources().getColor(android.R.color.white));
                });
            }
        }
    }

}