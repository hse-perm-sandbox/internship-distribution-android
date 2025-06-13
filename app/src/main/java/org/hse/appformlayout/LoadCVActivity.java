package org.hse.appformlayout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoadCVActivity extends AppCompatActivity {

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

        choosePdfBtn = findViewById(R.id.cv_btn_choose_pdf);
        cvFilename = findViewById(R.id.cv_filename);
        uploadBtn = findViewById(R.id.cv_btn_upload);
        downloadBtn = findViewById(R.id.cv_btn_download);
        deleteBtn = findViewById(R.id.cv_btn_delete);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}