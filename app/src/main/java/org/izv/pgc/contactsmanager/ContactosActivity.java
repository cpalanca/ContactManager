package org.izv.pgc.contactsmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.izv.pgc.contactsmanager.operaciones.AfterPermissionsCheck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class ContactosActivity extends AppCompatActivity {
    private List<Contacto> contactos;
    private static final int NONE = -1;
    private static final int INTERN = 0;
    private static final int PUBLIC = 1;
    private static final int PRIVATE = 2;
    private static final int ID_PERMISO_LEER_ESCRIBIR = 4;
    private static final String TAG = "xyz " + MainActivity.class.getName();
    private static final String KEY_ARCHIVO = "archivo";
    private Button btEscribir; //Boton escribir
    private String extensionArchivos;
    private CheckBox ckbTipoHdd; //Checkbox tipo
    private TextView tvLista; //TextView resultado

    private String name, value; //Para guardar el nombre del etArchivo
    private int type = INTERN; //Para guardar el tipo de ckbTipoHdd

    private static int getCheckedType(String item) { //Le pasamos el checkbox activado desde sharedPreference
        int tipo = NONE;
        Log.v("ITEM", item);
        switch (item) {
            case "INTERN":
                tipo = INTERN;
                break;
            case "PUBLIC":
                tipo = PUBLIC;
                break;
            case "PRIVATE":
                tipo = PRIVATE;
                break;
        }
        return tipo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contactos);
        obtenerListaContactos();
        readSettings();
        assingEvents();
    }

    private void assingEvents() {
        btEscribir = findViewById(R.id.btEscribir);
        btEscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFile();
            }
        });
    }

    private boolean isValues() { //obtiene los dos valores: nombre y tipo

        Calendar calendarNow = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
        int monthDay =calendarNow.get(Calendar.DAY_OF_MONTH);
        int month = calendarNow.get(Calendar.MONTH);
        int year = calendarNow.get(Calendar.YEAR);
        int hour = calendarNow.get(Calendar.HOUR);
        int minute = calendarNow.get(Calendar.MINUTE);
        int second = calendarNow.get(Calendar.SECOND);
        name = "backup_"+year+month+monthDay+hour+minute+second; //.trim te quita los espacios de delante y detras
        Log.v(TAG,"preferences "+readPreferences());
        type = ContactosActivity.getCheckedType(readPreferences()); //Coge el valor del radio button pulsado
        Log.v(TAG,"isValues"+type);
        //Log.v(TAG,"isValues"+name);
        return !(name.isEmpty() || type == NONE); //return true;
    }

    private void writeFile() {
        value = "" + tvLista.getText().toString();
        //Log.v(TAG,value);
        if (isValues() && !value.isEmpty()) {
            if (type == PUBLIC) {
                //comprobar permisos
                checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.tituloExplicacion, R.string.mensajeExplicacion, new AfterPermissionsCheck() {
                    @Override
                    public void doTheJob() {

                    }
                });
            } else {
                Log.v(TAG,"writeFile");
                writeNotes();
            }
        }
    }

    private void checkPermissions(String permiso, int titulo, int mensaje, AfterPermissionsCheck apc) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permiso no garantizado
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //2ª y sucesivas veces //mostrar explicacion
                //Explicar y pedir permiso: requestPermissions(ID)
                explain(R.string.tituloExplicacion, R.string.mensajeExplicacion, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ID_PERMISO_LEER_ESCRIBIR);
            }
        } else {
            //Realizar accion
            apc.doTheJob();
            //writeNotes();
            //readNotes();
        }
    }

    private void explain(int title, int message, final String permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(ContactosActivity.this, new String[]{permissions}, ID_PERMISO_LEER_ESCRIBIR);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void writeNotes() {
        File f = new File(getFile(type), name + readPreferences2());
        Log.v(TAG, f.getAbsolutePath());
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(value);
            fw.flush();
            fw.close();
            Log.v(TAG,"writeNotes");
            //tvLista.setText(R.string.escribir);
            savePreferrences();
            Log.v(TAG,""+value);
        } catch (IOException e) {
            tvLista.setText(e.getMessage());
        }
    }

    private File getFile(int type) {
        return ContactosActivity.getFile(this, type);
    }

    private static File getFile(Context context, int type) { //Tambien en los parentesis podemos pornerlo MainActivity context, int type
        File file = null;
        switch (type) {
            case INTERN:
                file = context.getFilesDir();
                break;
            case PUBLIC:
                file = Environment.getExternalStorageDirectory(); //devuelve acceso a la carpeta
                break;
            case PRIVATE:
                file = context.getExternalFilesDir(null);
                break;
        }
        return file;
    }

    private void readSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        extensionArchivos = sharedPreferences.getString("extension", ".csv");
    }

    private String readPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ContactosActivity.this);
        return sharedPref.getString("list_preference_1", "Intern");
    }

    private String readPreferences2() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ContactosActivity.this);
        return sharedPref.getString("extension", ".csv");
    }

    private void savePreferrences(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ARCHIVO, name);
        editor.commit();
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


    private void obtenerListaContactos() {
        contactos = getListaContactos();
        tvLista = findViewById(R.id.tvLista);
        tvLista.setText(contactos.toString());
    }


    public List<Contacto> getListaContactos(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId))
                    .setNombre(cursor.getString(indiceNombre))
                    .setTelefonos(getListaTelefonos(contacto.getId()));
            //contacto.setId(1).setNombre("2");
            lista.add(contacto);
        }
        return lista;
    }

    public List<String> getListaTelefonos(long id){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id+""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while(cursor.moveToNext()){
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }

    private class Contactillo{
        private long id;
        private String nombre;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    private static class Contacto{
        private long id;
        private String nombre;
        private List<String> telefonos = new ArrayList<>();

        public long getId() {
            return id;
        }

        public Contacto setId(long id) {
            this.id = id;
            return this;
        }

        public String getNombre() {
            return nombre;
        }

        public Contacto setNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public void setTelefonos(List<String> telefonos) {
            this.telefonos = telefonos;
        }

        @Override
        public String toString() {
            return "Contacto{" +
                    "id=" + id +
                    ", nombre='" + nombre + '\'' +
                    ", telefonos='" + telefonos + '\'' +
                    "\n'" +
                    '}';
        }
    }
}
