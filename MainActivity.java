package com.example.zakatgold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText gram, value;
    Button calcBtn, resetBtn;
    TextView output, outputt, outputtt;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    SharedPreferences sharedPref, sharedPref2;

    float gweight, gvalue;

    MaterialToolbar toolbar;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);


        spinner = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        gram = findViewById(R.id.amount);
        value = findViewById(R.id.currentGold);
        output = findViewById(R.id.totalGold);
        outputt = findViewById(R.id.zakatPay);
        outputtt = findViewById(R.id.totalZakat);

        calcBtn = findViewById(R.id.btncal);
        resetBtn = findViewById(R.id.btnreset);

        calcBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);

        sharedPref = getSharedPreferences("weight", Context.MODE_PRIVATE);
        sharedPref2 = getSharedPreferences("value", Context.MODE_PRIVATE);

        gweight = sharedPref.getFloat("weight", 0F);
        gvalue = sharedPref2.getFloat("value", 0F);


        gram.setText(String.valueOf(gweight));
        value.setText(String.valueOf(gvalue));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_share) {
            shareAppUrl();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void shareAppUrl() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        String appUrl = "Calculate Zakat on Gold easily with this app: [zakatgoldcalc.com]";

        shareIntent.putExtra(Intent.EXTRA_TEXT, appUrl);

        startActivity(Intent.createChooser(shareIntent, "Share Zakat App via..."));
    }


    @Override
    public void onClick(View v) {

        try {
            int id = v.getId();

            if (id == R.id.btncal) {
                calc();

            } else if (id == R.id.btnreset) {

                gram.setText("");
                value.setText("");
                output.setText("Total Gold Value : RM");
                outputt.setText("Zakat Payable : RM");
                outputtt.setText("Total Zakat : RM");
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Input Missing!", Toast.LENGTH_SHORT).show();

        } catch (Exception exp) {
            Toast.makeText(this, "Error: " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public void calc() {

        DecimalFormat df = new DecimalFormat("##0.00");

        float gweight = Float.parseFloat(gram.getText().toString());
        float gvalue = Float.parseFloat(value.getText().toString());
        String stat = spinner.getSelectedItem().toString();


        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("weight", gweight);
        editor.apply();

        SharedPreferences.Editor editor2 = sharedPref2.edit();
        editor2.putFloat("value", gvalue);
        editor2.apply();


        double totalValue = gweight * gvalue;

        double nisabThreshold = stat.equals("Keep") ? 85 : 200;
        double uruf = gweight - nisabThreshold;

        if (uruf < 0) uruf = 0;

        double zakatPayable = uruf * gvalue;
        double totalZakat = zakatPayable * 0.025; // 2.5%

        output.setText("Total Gold Value : RM " + df.format(totalValue));
        outputt.setText("Zakat Payable : RM " + df.format(zakatPayable));
        outputtt.setText("Total Zakat : RM " + df.format(totalZakat));
    }
}