package com.kirussell.tastytrucks.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by russellkim on 07/04/16.
 * Base data adapter
 */
public abstract class DataAdapter<T, VH> extends BaseAdapter {

    ArrayList<T> data = new ArrayList<>();

    /**
     * Clears previous data and adds given
     * @param data new items
     */
    public void setData(List<T> data) {
        replaceData(data);
        notifyDataSetChanged();
    }

    protected void replaceData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    /**
     * Adds to end
     * @param data new items
     */
    public void addData(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), getItemLayoutResource(), null);
            holder = createViewHolder(convertView);
        } else {
            holder = (VH) convertView.getTag();
        }
        T item = getItem(position);
        onBindViewHolder(holder, item);
        convertView.setTag(holder);
        return convertView;
    }

    protected abstract int getItemLayoutResource();

    protected abstract VH createViewHolder(View convertView);

    protected abstract void onBindViewHolder(VH holder, T item);
}
