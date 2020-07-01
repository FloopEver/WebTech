package com.example.ebaycatalogsearch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayItems extends AppCompatActivity {
    public String[] formInput = {"", "", "", "false", "false", "false", "0"};

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private GridLayoutManager mLayoutManager;
    public ProgressBar load ;
    public TextView ltext ;
    public TextView record_text;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_items);

        // Get the Intent that started this activity
        Intent intent = getIntent();
        // Get form input.
        Bundle b = this.getIntent().getExtras();
        formInput = b.getStringArray("form_input");

        load = findViewById(R.id.loading);
        ltext = findViewById(R.id.l_text);
        record_text = findViewById(R.id.record_text);
        mSwipeRefreshLayout = findViewById(R.id.refresh);

        load.setVisibility(View.VISIBLE);
        ltext.setVisibility(View.VISIBLE);

        link();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                link();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

    }

    public String buildurl(){
        String url="find?";
        url += "&keywords=" + formInput[0];
        int i = -1;
        if(!formInput[1].equals("")){
            i += 1;
            url += "&itemFilter(" + i + ").name=MinPrice&itemFilter(" + i
                    + ").value=" + formInput[1] + "&itemFilter(" + i + ").paramName=Currency&itemFilter(" + i + ").paramValue=USD";
        }
        if(!formInput[2].equals("")){
            i += 1;
            url += "&itemFilter(" + i + ").name=MaxPrice&itemFilter(" +
                    i + ").value=" + formInput[2] + "&itemFilter(" + i + ").paramName=Currency&itemFilter(" + i + ").paramValue=USD";
        }
        if(formInput[3].equals("true") || formInput[4].equals("true") || formInput[5].equals("true")){
            i += 1;
            url += "&itemFilter(" + i + ").name=Condition";
            int k = -1;
            if(formInput[3].equals("true")){
                k += 1;
                url += "&itemFilter(" + i + ").value(" + k + ")=New";
            }

            if(formInput[4].equals("true")){
                k += 1;
                url += "&itemFilter(" + i + ").value(" + k + ")=Used";
            }

            if(formInput[5].equals("true")){
                k += 1;
                url += "&itemFilter(" + i + ").value(" + k + ")=Unspecified";
            }
        }
        String sortOrder = "BestMatch";

        if (formInput[6].equals("Price: highest first")){sortOrder = "CurrentPriceHighest";}
        else if (formInput[6].equals("Price + Shipping: Highest first")){sortOrder = "PricePlusShippingHighest";}
        else if (formInput[6].equals("Price + Shipping: Lowest first")){sortOrder = "PricePlusShippingLowest";}

        url += "&sortOrder=" + sortOrder;
        return url;
    }

    public void link(){
        int DEFAULT_TIMEOUT_MS = 10000;
        int DEFAULT_MAX_RETRIES = 3;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String formurl = buildurl();
        String url ="https://homework8-yijing.wl.r.appspot.com/" + formurl;

        System.out.println(url);

        // Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            load.setVisibility(View.GONE);
                            ltext.setVisibility(View.GONE);
                            JSONObject res = (JSONObject) response.get(0);
                            if (res.get("ID").equals("true")) {
                                record_text.setVisibility((View.VISIBLE));
                                Toast toast = Toast.makeText(DisplayItems.this, "No Records", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else{
                                showItems(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void showItems(final JSONArray response) throws JSONException {

        mRecyclerView = findViewById(R.id.recycler_view);
        mMyAdapter = new MyAdapter(response);
        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMyAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));

        TextView result = findViewById(R.id.result_text);
        int num = response.length();
        String keyword = formInput[0];
        result.setText(HtmlCompat.fromHtml("Showing <font color=#3472F8>" + num + "</font> results for <font color=#3472F8>" + keyword + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));

        mMyAdapter.setOnitemClickListener(new MyAdapter.OnitemClick() {
            @Override
            public void onItemClick(int position) throws JSONException {
                JSONObject search_item = (JSONObject) response.get(position);
                String id = search_item.get("ID").toString();
                String title = search_item.get("Title").toString();
                String url_link = search_item.get("Link").toString();
                String ship = search_item.get("Ship").toString();
                String price = "$" + search_item.get("Price").toString();
                String shipp = search_item.get("ShippingCost").toString();

                jump(id, title, ship, url_link, price, shipp);
            }
        });
    }

    public  void jump(String id, String title, String ship, String url_link, String price, String spp){
        Intent intent_item = new Intent(this, Itemview.class);
        // Deliver form input to ItemsView activity
        intent_item.putExtra("ID",id);
        intent_item.putExtra("Title",title);
        intent_item.putExtra("Ship",ship);
        intent_item.putExtra("Link",url_link);
        intent_item.putExtra("Price",price);
        intent_item.putExtra("Shipp",spp);

        startActivityForResult(intent_item, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}