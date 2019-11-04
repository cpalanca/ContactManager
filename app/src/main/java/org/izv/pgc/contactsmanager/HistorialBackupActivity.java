package org.izv.pgc.contactsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class HistorialBackupActivity extends AppCompatActivity {
    private String guardaHistorial;
    private TextView tvHistorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_backup);
        readSettings();
        initComponent();
    }

    private void initComponent() {
        tvHistorial = findViewById(R.id.tvHistorial);
    }

    private void readSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        guardaHistorial = sharedPreferences.getString("sync", "summaryOn");
        initEvents();
    }

    private void initEvents() {
    }
}
