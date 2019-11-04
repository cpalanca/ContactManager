package org.izv.pgc.contactsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.izv.pgc.contactsmanager.R;

import java.sql.Struct;

public class SettingsActivity extends AppCompatActivity {
    private static final String KEY_HDD_TYPE = "hddType";
    private String name, value; //Para guardar el nombre del etArchivo
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        readSettings();
    //    initComponents();
        //initEvents();
    }

    private void initEvents() {


    }

    private void initComponents() {


    }

    private void savePreferrences(String name) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String VALUE_HDD_TYPE = name;
        editor.putString(KEY_HDD_TYPE, VALUE_HDD_TYPE);
        editor.commit();
    }

    private void readSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        name = sharedPreferences.getString("extension", ".csv");
        Log.v("xyz",name);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}