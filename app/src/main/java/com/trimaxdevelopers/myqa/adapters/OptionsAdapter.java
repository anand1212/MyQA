package com.trimaxdevelopers.myqa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trimaxdevelopers.myqa.R;
import com.trimaxdevelopers.myqa.models.QARow;

import java.util.List;

public abstract class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> itemList;
    private QARow qaRow;


    public OptionsAdapter(Context context, QARow qaRow) {
        this.context = context;
        this.qaRow = qaRow;
        this.itemList = qaRow.getOptions();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cell_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int finalPosition = position;
        final String option = itemList.get(position);
        holder.textTitle.setText(option);

        holder.textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qaRow.setSelectedAnswer(option);
                notifyDataSetChanged();
                onItemClick(finalPosition, option);
            }
        });

        if (qaRow.getSelectedAnswer() != null && qaRow.getSelectedAnswer().equals(option)) {
            holder.textTitle.setBackgroundResource(R.drawable.selected);
        } else {
            holder.textTitle.setBackgroundResource(R.drawable.unselected);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public abstract void onItemClick(int position, String option);

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;

        ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
        }
    }

}