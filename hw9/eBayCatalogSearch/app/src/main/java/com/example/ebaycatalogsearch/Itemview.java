package com.example.ebaycatalogsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class Itemview extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;
    public ProgressBar load ;
    public TextView ltext ;
    String ID;
    String Title;
    String Link;
    String Price;
    String Shipp;
    JSONObject SHIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemview);

        // Get the Intent that started this activity
        final Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Title = intent.getStringExtra("Title");
        Link = intent.getStringExtra("Link");
        Price = intent.getStringExtra("Price");
        Shipp = intent.getStringExtra("Shipp");
        try {
            SHIP = new JSONObject(intent.getStringExtra("Ship"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Loading
        load = findViewById(R.id.loading);
        ltext = findViewById(R.id.l_text);
        load.setVisibility(View.VISIBLE);
        ltext.setVisibility(View.VISIBLE);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.actions);
        toolbar.setTitle(Title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.out:
                        Intent intent2 = new Intent();
                        intent2.setData(Uri.parse(Link));
                        intent2.setAction(Intent.ACTION_VIEW);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        });

        // Send request
        link();

    }

    //设置ToolBar的选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void link(){
        int DEFAULT_TIMEOUT_MS = 10000;
        int DEFAULT_MAX_RETRIES = 3;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String formurl = ID;
        String url ="https://homework8-yijing.wl.r.appspot.com/item?" + formurl;

        System.out.println(url);

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        load.setVisibility(View.GONE);
                        ltext.setVisibility(View.GONE);
                        show(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void show(JSONObject response){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), response, SHIP, Title, Price, Shipp);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //指定Tab的位置
        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);
        three = tabLayout.getTabAt(2);

        //设置Tab的图标
        one.setIcon(R.drawable.information_variant_selected);
        two.setIcon(R.drawable.ic_seller);
        two.getIcon().setColorFilter(getResources().getColor(R.color.tab_selected_color), PorterDuff.Mode.SRC_IN);
        three.setIcon(R.drawable.truck_delivery_selected);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}