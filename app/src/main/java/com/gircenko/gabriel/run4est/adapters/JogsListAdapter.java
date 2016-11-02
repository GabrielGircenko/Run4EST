package com.gircenko.gabriel.run4est.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.models.JogModel;
import com.gircenko.gabriel.run4est.models.JogModelWithId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel Gircenko on 30-Oct-16.
 */

public class JogsListAdapter extends BaseAdapter {

    private final int VIEW_TYPE_ITEM = 102;

    private List<JogModelWithId> items = new ArrayList<>();

    private LayoutInflater inflater;

    public JogsListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemList(List<JogModelWithId> jogs) {
        items = jogs;
        notifyDataSetChanged();
    }

    public void addItem(JogModelWithId jogModel) {
        items.add(jogModel);
    }

    public void removeItem(String jogId) {
        for (JogModelWithId jog : items) {
            if (jogId.equals(jog.getJogId())) {
                items.remove(jog);
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public JogModelWithId getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem holder;
        int viewType = this.getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolderItem();
            switch (viewType) {
                case VIEW_TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.item_item, null);
                    holder.tv_time = (TextView) convertView.findViewById(R.id.tv_total_distance);
                    holder.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
                    holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                    break;
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        holder.tv_time.setText(items.get(position).getJog().getTime());
        holder.tv_speed.setText(
                Helper.getAverageSpeed(items.get(position).getJog().getTime(),
                        items.get(position).getJog().getDistance()) + " km/h");
        holder.tv_distance.setText(String.valueOf(items.get(position).getJog().getDistance()));

        return convertView;
    }

    static class ViewHolderItem {
        public TextView tv_time;
        public TextView tv_speed;
        public TextView tv_distance;
    }
}
