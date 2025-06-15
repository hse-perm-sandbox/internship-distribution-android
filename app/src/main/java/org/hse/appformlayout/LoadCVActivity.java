package org.hse.appformlayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoadCVActivity extends AppCompatActivity {

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

        // Инициализация кнопок и текстового поля по id из layout
        choosePdfBtn = findViewById(R.id.cv_btn_choose_pdf);
        cvFilename = findViewById(R.id.cv_filename);
        uploadBtn = findViewById(R.id.cv_btn_upload);
        downloadBtn = findViewById(R.id.cv_btn_download);
        deleteBtn = findViewById(R.id.cv_btn_delete);

        // Обработчик нажатия на кнопку "Выбрать PDF-файл"
        choosePdfBtn.setOnClickListener(v -> openFilePicker());

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
                Uri uri = data.getData(); // Получаем Uri выбранного файла
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
}