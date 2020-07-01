package com.example.ebaycatalogsearch;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.json.JSONObject;

class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"PRODUCT", "SELLER INFO", "SHIPPING"};
    public JSONObject item_res;
    public JSONObject item_ship;
    public String item_title;
    public String Price;
    public String item_spp;

    public ViewPagerAdapter(FragmentManager manager, JSONObject response, JSONObject SHIP, String item_titled, String price, String Shipp) {
        super(manager);
        item_res = response;
        item_ship = SHIP;
        item_title = item_titled;
        Price = price;
        item_spp = Shipp;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.getInstance(position, item_res, item_ship, item_title, Price, item_spp);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
