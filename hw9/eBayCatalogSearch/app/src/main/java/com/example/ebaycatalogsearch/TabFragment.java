package com.example.ebaycatalogsearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class TabFragment extends Fragment {

    int position;
    private TextView textView;
    private TextView textView2;
    public static JSONObject i_res;
    public static JSONObject ship;
    public JSONArray ims;
    public static String it;
    public static String iPrice;
    public static String iSPrice;

    public static Fragment getInstance(int position, JSONObject res, JSONObject SHIP, String title, String price, String item_spp) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        i_res = res;
        ship = SHIP;
        it = title;
        iPrice = price;
        iSPrice = item_spp;
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tab1 = inflater.inflate(R.layout.fragment_tab, container, false);
        View tab2 = inflater.inflate(R.layout.fragment_tab2, container, false);
        View tab3 = inflater.inflate(R.layout.fragment_tab3, container, false);

        if(position == 0){return tab1;}
        else if (position == 1){return tab2;}
        else{return tab3;}
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(position == 0){
            try {
                ims = i_res.getJSONObject("Item").getJSONArray("PictureURL");
                addGroupImage(ims, view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textView = view.findViewById(R.id.title3);
            textView.setText(it);
            String iSP;

            if (iSPrice.equals("0.0")){
                iSP = "FREE Shipping";
            }
            else{
                iSP = "Ships for $"+ iSPrice;
            }

            String htmls = "<font color=#a4c639><strong>" + iPrice + " </font></strong></font><small>" + iSP + "</small>";
            textView2 = view.findViewById(R.id.price3);
            textView2.setText(HtmlCompat.fromHtml(htmls, HtmlCompat.FROM_HTML_MODE_LEGACY));

            String bshtml = "";
            int sflag = 1;
            int bflag = 1;
            String spehtml = "";
            String brand = "";

            try {
                JSONObject ISp = i_res.getJSONObject("Item").getJSONObject("ItemSpecifics");
                if(ISp.isNull("NameValueList")){
                    sflag = 1;
                    bflag = 1;
                }
                else{
                    JSONArray nl = ISp.getJSONArray("NameValueList");
                    bflag = 1;
                    int j = 0;
                    for (int i = 0; i < nl.length(); i++) {
                        String name = nl.getJSONObject(i).get("Name").toString();
                        String value = nl.getJSONObject(i).getJSONArray("Value").get(0).toString();
                        if (name.equals("Brand")){
                            bflag = 0;
                            brand = value;
                        }
                        else if(j < 5){
                            j += 1;
                            spehtml += "<li>&nbsp " + name + " : " + value + "</li>";
                        }
                    }
                    if (!spehtml.equals("")){
                        sflag = 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            try {
                if (i_res.getJSONObject("Item").isNull("Subtitle")){
                    if (bflag == 1){
                        View dd2 = view.findViewById(R.id.divider2);
                        LinearLayout sp2 = view.findViewById(R.id.l1);
                        TextView txv = view.findViewById(R.id.textView6);
                        dd2.setVisibility(View.GONE);
                        sp2.setVisibility(View.GONE);
                        txv.setVisibility(View.GONE);
                    }
                    else{
                        bshtml = "<p><strong><font color=#000000>Brand</font>&nbsp&nbsp&nbsp&nbsp&nbsp</strong><small>" + brand + "</small></p>";
                        TextView textviewsb = view.findViewById(R.id.textView6);
                        textviewsb.setText(HtmlCompat.fromHtml(bshtml, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }
                }
                else {
                    String subtitle = i_res.getJSONObject("Item").get("Subtitle").toString();
                    bshtml = "<p><strong><font color=#000000>Subtitle</font>&nbsp&nbsp&nbsp&nbsp</strong><small>" + subtitle + "</small></p>";
                    if (bflag == 0){
                        bshtml +=  "<p><strong><font color=#000000>Brand</font>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</strong><small>" + brand + "</small></p>";
                    }

                    TextView textviewsb = view.findViewById(R.id.textView6);
                    textviewsb.setText(HtmlCompat.fromHtml(bshtml, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (sflag == 1){
                View dd2 = view.findViewById(R.id.divider3);
                LinearLayout sp2 = view.findViewById(R.id.l2);
                dd2.setVisibility(View.GONE);
                sp2.setVisibility(View.GONE);
            }
            else {
                TextView textviews = view.findViewById(R.id.textView7);
                textviews.setText(HtmlCompat.fromHtml(spehtml, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }


        }
        else if (position == 1){

            JSONObject seller;
            try {
                seller = i_res.getJSONObject("Item").getJSONObject("Seller");
                Iterator<String> iter_2 = seller.keys();
                String htmltext = "";
                textView = view.findViewById(R.id.Seller);
                while (iter_2.hasNext()){
                    String key = iter_2.next();
                    String value = null;
                    try {
                        value = seller.get(key).toString();
                        if (value.equals("false")){ value = "No";}
                        else if (value.equals("true")){ value = "Yes";}
                        htmltext += "<li>&nbsp<strong>" + (key.substring(0,1).toUpperCase()+key.substring(1)).replaceAll("(.)([A-Z])","$1 $2") + " : </strong>" + value + "</li>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                textView.setText(HtmlCompat.fromHtml(htmltext, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (i_res.getJSONObject("Item").isNull("ReturnPolicy")){
                    View dd2r = view.findViewById(R.id.divider);
                    TextView txvr = view.findViewById(R.id.textView3);
                    dd2r.setVisibility(View.GONE);
                    txvr.setVisibility(View.GONE);
                }
                else {
                    JSONObject ret;
                    try {
                        ret = i_res.getJSONObject("Item").getJSONObject("ReturnPolicy");
                        Iterator<String> iter_2 = ret.keys();
                        String htmltext = "";
                        textView2 = view.findViewById(R.id.Return);
                        while (iter_2.hasNext()) {
                            String key = iter_2.next();
                            String value = null;
                            try {
                                value = ret.get(key).toString();
                                if (value.equals("false")) {
                                    value = "No";
                                } else if (value.equals("true")) {
                                    value = "Yes";
                                } else if (value.equals("ReturnsNotAccepted")) {
                                    value = "No";
                                } else if (value.equals("ReturnsAccepted")) {
                                    value = "Yes";
                                }
                                htmltext += "<li>&nbsp<strong>" + (key.substring(0, 1).toUpperCase() + key.substring(1)).replaceAll("(.)([A-Z])", "$1 $2") + " : </strong>" + value + "</li>";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        textView2.setText(HtmlCompat.fromHtml(htmltext, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Iterator<String> iter = ship.keys();
            String htmltext = "";
            textView = view.findViewById(R.id.Shipping);
            while (iter.hasNext()){
                String key = iter.next();
                if(key.equals("shippingServiceCost"))
                {
                    String value = null;
                    try {
                        value = ship.getJSONArray(key).getJSONObject(0).getString("__value__");
                        if (value.equals("false")){ value = "No";}
                        htmltext += "<li>&nbsp<strong>" + (key.substring(0,1).toUpperCase()+key.substring(1)).replaceAll("(.)([A-Z])","$1 $2") + " : </strong>" + value + "</li>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    String value = null;
                    try {
                        value = ship.getJSONArray(key).getString(0);
                        if (value.equals("false")){ value = "No";}
                        htmltext += "<li>&nbsp<strong>" + (key.substring(0,1).toUpperCase()+key.substring(1)).replaceAll("(.)([A-Z])","$1 $2") + " : </strong>" + value + "</li>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            textView.setText(HtmlCompat.fromHtml(htmltext, HtmlCompat.FROM_HTML_MODE_LEGACY));

        }


    }


    // Gallery
    public void addGroupImage(JSONArray ims, View view) throws JSONException {
        LinearLayout llGroup = view.findViewById(R.id.images);
        llGroup.removeAllViews();  //clear linearlayout
        for (int i = 0; i < ims.length(); i++) {
            ImageView imageView = new ImageView(view.getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));  //设置图片宽高
            Picasso.get().load(ims.get(i).toString()).into(imageView);
            imageView.setAdjustViewBounds(true);
            llGroup.addView(imageView); //add images
        }
    }

}
