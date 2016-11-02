package com.gircenko.gabriel.run4est.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.models.JogModelWithId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gabriel Gircenko on 28-Oct-16.
 */
public class JogsWithHeadersListAdapter extends BaseAdapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_HEADER = 1;

    /** key is date */
    private Map<String, List<JogModelWithId>> mData = new TreeMap<>();
    private List<JogModelWithId> mArray = new ArrayList<>();

    private LayoutInflater inflater;

    public JogsWithHeadersListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(JogModelWithId jog) {
        String date = jog.getJog().getDate();
        List<JogModelWithId> jogs = new ArrayList<>();
        if (!mData.containsKey(date)) {
            jogs.add(new JogModelWithId(null, null));

        } else {
            jogs = mData.get(date);
        }

        jogs.add(jog);
        mData.put(date, jogs);
        refreshData();
    }

    public void removeItem(JogModelWithId jog) {
        Iterator<JogModelWithId> iter = mArray.iterator();
        while (iter.hasNext()) {
            JogModelWithId entry = iter.next();
            if (entry.getJogId() != null && entry.getJogId().equals(jog.getJogId())) {
                String date = entry.getJog().getDate();
                List<JogModelWithId> jogs = mData.get(date);
                Iterator<JogModelWithId> iterator = jogs.iterator();
                while (iterator.hasNext()) {
                    JogModelWithId subEntry = iterator.next();
                    if (subEntry.getJogId() != null && subEntry.getJogId().equals(jog.getJogId())) {
                        iterator.remove();
                        String prevDate = subEntry.getJog().getDate();
                        List<JogModelWithId> jogss = mData.get(prevDate);

                        mData.remove(prevDate);

                        if (jogss.size() > 1) {
                            mData.put(prevDate, jogss);
                        }

                        break;
                    }
                }

                break;
            }
        }

        refreshData();
    }

    private void refreshData() {
        mArray = new ArrayList<>();
        for(Map.Entry<String, List<JogModelWithId>> entry : mData.entrySet()) {
            mArray.addAll(entry.getValue());
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public JogModelWithId getItem(int position) {
        return mArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mArray.get(position).getJog() != null) return VIEW_TYPE_ITEM;
        else return VIEW_TYPE_HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
                    holder.tv_time_or_date = (TextView) convertView.findViewById(R.id.tv_total_distance);
                    holder.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
                    holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                    break;

                case VIEW_TYPE_HEADER:
                    convertView = inflater.inflate(R.layout.item_header, null);
                    holder.tv_time_or_date = (TextView) convertView.findViewById(R.id.tv_header);
                    break;
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        if (viewType == VIEW_TYPE_ITEM) {

            JogModelWithId meal = getItem(position);
            holder.tv_time_or_date.setText(meal.getJog().getTime());
            holder.tv_distance.setText(String.valueOf(meal.getJog().getDistance()) + " km");
            holder.tv_speed.setText(Helper.getAverageSpeed(meal.getJog().getTime(), meal.getJog().getDistance()) + " km/h");

        } else if (viewType == VIEW_TYPE_HEADER) {
            JogModelWithId jogModelWithId = getItem(position + 1);   //  to get the date
            holder.tv_time_or_date.setText(jogModelWithId.getJog().getDate());
        }

        return convertView;
    }

    static class ViewHolderItem {
        public TextView tv_time_or_date;
        public TextView tv_speed;
        public TextView tv_distance;
    }
}
