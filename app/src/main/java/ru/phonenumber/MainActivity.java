package ru.phonenumber;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText hostEdt = (EditText) findViewById(R.id.hostNameEdt);
        hostEdt.setText(getApplicationContext().getSharedPreferences("PhoneNumber", MODE_MULTI_PROCESS).getString("host", ""));
        Button saveBtn = (Button) findViewById(R.id.saveHost);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity", "save pref");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("PhoneNumber", MODE_MULTI_PROCESS);
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("host", hostEdt.getText().toString());
                ed.commit();
            }
        });
    }
}
