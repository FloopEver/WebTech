package com.example.ebaycatalogsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private EditText Keyword = null;
    private EditText MinPrice = null;
    private EditText MaxPrice = null;
    private CheckBox New = null;
    private CheckBox Used = null;
    private CheckBox Unspecified = null;
    private Spinner Spinner = null;
    private ArrayAdapter<String> adapter = null;
    private static final String [] SortBy ={"Best Match","Price: Highest first","Price + Shipping: Highest first","Price + Shipping: Lowest first"};
    private String[] form = {"????", "", "", "false", "false", "false", "0"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner = (Spinner)findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,SortBy);
        //设置下拉列表风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器添加到spinner中去
        Spinner.setAdapter(adapter);
        Spinner.setVisibility(View.VISIBLE);//设置默认显示
        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                form[6] = SortBy[arg2];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

    }

    /** Called when the user taps the Search button */
    public void sendMessage(View view) {
        Keyword = findViewById(R.id.Keyword);
        MinPrice = findViewById(R.id.MinimumPrice);
        MaxPrice = findViewById(R.id.MaximumPrice);
        New = findViewById(R.id.NewCheck);
        Used = findViewById(R.id.UsedCheck);
        Unspecified = findViewById(R.id.UnspecifiedCheck);
        int flag = 1;
        if (Keyword.getText().toString().equals("")){
            TextInputLayout KeywordLayout = findViewById(R.id.KeywordLayout);
            KeywordLayout.setErrorEnabled(true);
            KeywordLayout.setError(HtmlCompat.fromHtml("<font color=#E53935>Please enter mandatory field</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            flag = 0;
        }
        String min = MinPrice.getText().toString();
        String max = MaxPrice.getText().toString();
        TextInputLayout MaxLayout = findViewById(R.id.MaximumPriceLayout);
        if ((!min.equals("")) && (!max.equals(""))){
            Float minf = Float.valueOf(min);
            Float maxf = Float.valueOf(max);
            if(minf>maxf){
                MaxLayout.setError(HtmlCompat.fromHtml("<font color=#E53935>Please enter valid price values</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                flag = 0;
            }
        }
        // Submit input and send input content(form) by GET to backend.
        if(flag == 1) {
            form[0] = Keyword.getText().toString();
            form[1] = min;
            form[2] = max;
            form[3] = String.valueOf(New.isChecked());
            form[4] = String.valueOf(Used.isChecked());
            form[5] = String.valueOf(Unspecified.isChecked());
            jump();
        }
        else {
            Toast toast = Toast.makeText(MainActivity.this, "Please fix all fields with errors", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /** Called when the user taps the Clear button */
    public void clear(View view) {
        Keyword = findViewById(R.id.Keyword);
        MinPrice = findViewById(R.id.MinimumPrice);
        MaxPrice = findViewById(R.id.MaximumPrice);
        New = findViewById(R.id.NewCheck);
        Used = findViewById(R.id.UsedCheck);
        Unspecified = findViewById(R.id.UnspecifiedCheck);
        Spinner = findViewById(R.id.spinner);
        TextInputLayout KeywordLayout = findViewById(R.id.KeywordLayout);
        TextInputLayout MaxLayout = findViewById(R.id.MaximumPriceLayout);

        Keyword.setText(null);
        MinPrice.setText(null);
        MaxPrice.setText(null);
        New.setChecked(false);
        Used.setChecked(false);
        Unspecified.setChecked(false);
        Spinner.setSelection(0,true);
        KeywordLayout.setErrorEnabled(false);
        MaxLayout.setErrorEnabled(false);

    }


    public  void jump(){
        Intent intent = new Intent(this, DisplayItems.class);
        // Deliver form input to Displayitems activity
        Bundle b=new Bundle();
        b.putStringArray("form_input", form);
        intent.putExtras(b);

        startActivityForResult(intent, 1);
    }

}