package com.example.ebaycatalogsearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private OnitemClick onitemClick;   //定义点击事件接口

    //数据源
    public JSONArray items;

    public MyAdapter(JSONArray response) {
        items = response;
    }

    //返回item个数
    @Override
    public int getItemCount() {
        return items.length();
    }

    //定义设置点击事件监听的方法
    public void setOnitemClickListener (OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }


    //定义一个点击事件的接口
    public interface OnitemClick {
        void onItemClick(int position) throws JSONException;
    }



    //创建ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    //填充视图
    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder holder, final int position) {
        try {
            JSONObject search_item = (JSONObject) items.get(position);
            String id = search_item.get("ID").toString();
            String title = search_item.get("Title").toString().toUpperCase();
            String ship = search_item.get("ShippingCost").toString();
            String image = search_item.get("Image").toString();
            String top = search_item.get("Top").toString();
            String price = "$" + search_item.get("Price").toString();
            String condition = search_item.get("Condition").toString();

            if (ship.equals("0.0")){
                ship = "<br /><strong>FREE</strong> Shipping";
            }
            else{
                ship = "<br />Ships for <strong>$"+ ship+"</strong>";
            }

            if (top.equals("true")){
                ship += "<br /><strong>Top Rated Listing</strong>";
            }


            TextView title_text = holder.item_card.findViewById(R.id.title);
            TextView ship_text = holder.item_card.findViewById(R.id.ship_top);
            TextView cond_text = holder.item_card.findViewById(R.id.condition);
            TextView price_text = holder.item_card.findViewById(R.id.price);
            ImageView image_v = holder.item_card.findViewById(R.id.imageView);

            title_text.setText(title);
            ship_text.setText(HtmlCompat.fromHtml(ship,HtmlCompat.FROM_HTML_MODE_LEGACY));
            cond_text.setText(condition);
            price_text.setText(price);

            if (!image.equals("default")){
                Picasso.get().load(image).into(image_v);
            }

            if (onitemClick != null) {
                holder.item_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //在TextView的地方进行监听点击事件，并且实现接口
                        try {
                            onitemClick.onItemClick(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView item_card;

        public ViewHolder(View itemView) {
            super(itemView);
            item_card = itemView.findViewById(R.id.card);
        }
    }



}

