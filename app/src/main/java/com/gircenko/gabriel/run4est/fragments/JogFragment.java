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
    TextView tv_total_distance;
    @BindView(R.id.tv_average_distance)
    TextView tv_average_distance;
    @BindView(R.id.tv_average_speed)
    TextView tv_average_speed;

    private OnJogFragmentCallback callback;

    private String date = null;
    private String totalDistance = null;
    private String averageDistance = null;
    private String averageSpeed = null;

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

        if (totalDistance != null && tv_total_distance != null) {
            tv_total_distance.setText(String.valueOf(totalDistance));
        }

        if (averageDistance != null && tv_average_distance != null) {
            tv_average_distance.setText(String.valueOf(averageDistance));
        }

        if (averageSpeed != null && tv_average_speed != null) {
            tv_average_speed.setText(String.valueOf(averageSpeed));
        }
    }

    @OnClick(R.id.rl_total_time)
    public void goToJogListClicked() {
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
     * @param totalDistance
     */
    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
        if (tv_total_distance != null) {
            tv_total_distance.setText(totalDistance);
        }
    }

    public void setAverageDistance(String distance) {
        this.averageDistance = distance;
        if (tv_average_distance != null) {
            tv_average_distance.setText(distance);
        }
    }

    public void setAverageSpeed(String speed) {
        this.averageSpeed = speed;
        if (tv_average_speed != null) {
            tv_average_speed.setText(speed);
        }
    }
}
