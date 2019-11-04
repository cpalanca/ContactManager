package org.izv.pgc.contactsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class HistorialBackupActivity extends AppCompatActivity {
    private String historial;
    private TextView tvHistorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_backup);
        initComponent();
        initEvents();
    }

    private void initComponent() {
        tvHistorial = findViewById(R.id.tvHistorial);
    }


    private void initEvents() {
        readHistorial();
        tvHistorial.setText(historial);
    }

    private void readHistorial() {
        SharedPreferences sharedPreferences = getSharedPreferences("ContactosActivity",Context.MODE_PRIVATE);
        historial = sharedPreferences.getString("historial", "no exiten todavia backups");
    }
}
