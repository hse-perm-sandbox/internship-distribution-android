package org.hse.appformlayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginText = login.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                // Заглушка для студента
                if (loginText.equals("student") && passwordText.equals("1111")) {
                    Intent intent = new Intent(MainActivity.this, MenuStudentActivity.class);
                    startActivity(intent);
                }
                // Заглушка для сотрудника офиса
                else if (loginText.equals("office") && passwordText.equals("1111")) {
                    Intent intent = new Intent(MainActivity.this, MenuOfficeActivity.class);
                    startActivity(intent);
                }
                // Если данные не совпадают ни с одним вариантом
                else {
                    Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}