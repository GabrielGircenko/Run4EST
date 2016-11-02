package com.gircenko.gabriel.run4est.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.intefaces.OnJogFragmentCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gabriel Gircenko on 30-Oct-16.
 */

public class JogFragment extends Fragment {

    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_total_distance)
    TextView tv_calories;

    private OnJogFragmentCallback callback;

    private String date = null;
    private String totalTime = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnJogFragmentCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (date != null && tv_date != null) {
            tv_date.setText(date);
        }

        if (totalTime != null && tv_calories != null) {
            tv_calories.setText(String.valueOf(totalTime));
        }
    }

    @OnClick(R.id.rl_total_time)
    public void goToMealListClicked() {
        callback.onTotalCountClicked(tv_date.getText().toString());
    }

    /**
     * Used by MainActivity
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
        if (tv_date != null) {
            tv_date.setText(date);
        }
    }

    /**
     * Used by MainActivity
     * @param totalTime
     */
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
        if (tv_calories != null) {
            tv_calories.setText(totalTime);
        }
    }
}
