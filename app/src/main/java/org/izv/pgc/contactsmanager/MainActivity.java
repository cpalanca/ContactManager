package org.izv.pgc.contactsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int ID_PERMISO_LEER_CONTACTOS = 1;

    private Button btContactos, btHistorial, btAjustes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initEvents();
    }

    private void initEvents() {
        readSettings();
    }

    private void readSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        String name = sharedPreferences.getString("extension", ".csv");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mnSettings:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initComponents() {
        btContactos = findViewById(R.id.btContactos);
        btAjustes = findViewById(R.id.btAjustesContactos);
        btHistorial = findViewById(R.id.btHistorial);

        btContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermisosContactos();
            }
        });
        btAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustes();
            }
        });
        btHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irHistorial();
            }
        });
    }

    private void irHistorial() {
        Intent intent = new Intent(this,HistorialBackupActivity.class);
        startActivity(intent);
    }

    public void pedirPermisosContactos(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //realizar accion
            irActividad();
        }else{
            // AQUI SE COMPRUEBA SI LA APP TIENE PERMISOS PARA LO QUE SOLICITAMOS
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // SI NO TUVIERA PERMISO LA APP VOLVERA A PEDIRLA
                // DEBERIA VOLVER A PREGUNTAR POR EL PERMISO
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {

                    Toast.makeText(this, R.string.razon, Toast.LENGTH_LONG).show();
                    // 2º VEZ QUE LE PIDO PERMISO AL USUARIO
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            ID_PERMISO_LEER_CONTACTOS);

                } else {
                    // ES LA 1º VEZ QUE PIDO PERMISO
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            ID_PERMISO_LEER_CONTACTOS);
                }
            } else {
                // Tengo permiso por lo que realizo la acción
                irActividad();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ID_PERMISO_LEER_CONTACTOS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    irActividad();

                } else {
                    // permiso denegado, boo! funcionalidad
                    // desabilitada por que depende del permiso.
                }
                return;
            }
            // Crear otros casos para otros permisos
        }
    }

    private void irActividad() {
        Intent intent = new Intent(this,ContactosActivity.class);
        startActivity(intent);
    }

    private void irAjustes() {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }


}
