package com.sofialopes.android.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sofia on 4/14/2018.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private List<Step> mStepsList = new ArrayList<>();
    private StepsAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public interface StepsAdapterOnClickHandler {
        void onStepClick(View v, Step step, int position);
    }

    public StepsAdapter(Context context,
                        ArrayList<Step> stepsList,
                        StepsAdapterOnClickHandler clickHandler) {
        this.mStepsList = stepsList;
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steps_recycler_item, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        Step step = mStepsList.get(position);
        int id = step.getId();
        String shortDescription = step.getShortDescription();
        holder.step.setText(
                mContext.getResources().getString(R.string.step_list_item_content, id, shortDescription));
    }

    @Override
    public int getItemCount() {
        if (mStepsList == null || mStepsList.isEmpty()) return 0;

        return mStepsList.size();
    }

    public void updateStepsList(ArrayList<Step> stepsList) {
        mStepsList = stepsList;
        notifyDataSetChanged();
    }


    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView step;

        public StepsViewHolder(View itemView) {
            super(itemView);
            step = itemView.findViewById(R.id.step);
            itemView.setOnClickListener(this);
            itemView.setBackgroundColor(
                    mContext.getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onStepClick(v, mStepsList.get(getAdapterPosition()), getAdapterPosition());
        }
    }
}