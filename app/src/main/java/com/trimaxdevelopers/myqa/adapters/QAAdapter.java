package com.trimaxdevelopers.myqa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trimaxdevelopers.myqa.R;
import com.trimaxdevelopers.myqa.customviews.RecyclerViewNonScrollable;
import com.trimaxdevelopers.myqa.models.QARow;

import java.util.List;

public abstract class QAAdapter extends RecyclerView.Adapter<QAAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<QARow> itemList;
    private DisplayMetrics displayMetrics;


    public QAAdapter(Context context, List<QARow> itemList) {
        this.context = context;
        this.itemList = itemList;
        layoutInflater = LayoutInflater.from(context);
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    public void setItemList(List<QARow> itemList) {
        this.itemList = itemList;
    }

    public List<QARow> getItemList() {
        return itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cell_qa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int finalPosition = position;
        final QARow qARow = itemList.get(position);

        holder.textQuestion.setText(qARow.getQuestion());

        holder.rvOptions.setAdapter(new OptionsAdapter(context, qARow) {
            @Override
            public void onItemClick(int position, String option) {
                onOptionSelected(finalPosition, qARow);
                qARow.setSelectedAnswer(option);
                itemList.set(finalPosition, qARow);
                notifyItemChanged(finalPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public abstract void onOptionSelected(int position, QARow qARow);

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textQuestion;
        private RecyclerViewNonScrollable rvOptions;

        ViewHolder(View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.textQuestion);
            rvOptions = itemView.findViewById(R.id.rvOptions);

            rvOptions.setLayoutManager(new LinearLayoutManager(rvOptions.getContext()));
        }
    }

}