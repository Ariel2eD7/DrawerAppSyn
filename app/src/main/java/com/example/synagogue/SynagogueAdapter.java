package com.example.synagogue;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawerappsyn.R;

import java.util.ArrayList;

public class SynagogueAdapter
        extends RecyclerView.Adapter<SynagogueAdapter.SynagogueViewHolder> {

    private final ArrayList<Synagogue> synagogueList;
    private final OnSynagogueClickListener listener;

    public interface OnSynagogueClickListener {
        void onSynagogueClick(Synagogue synagogue);
    }

    public SynagogueAdapter(
            ArrayList<Synagogue> list,
            OnSynagogueClickListener listener) {

        synagogueList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SynagogueViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_user,
                        parent,
                        false
                );

        return new SynagogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SynagogueViewHolder holder,
            int position) {

        Synagogue synagogue =
                synagogueList.get(position);

        // שם
        String name = synagogue.getName();

        if (TextUtils.isEmpty(name)) {
            name = "בית כנסת";
        }

        holder.textName.setText(name);

        // כתובת
        String address = synagogue.getAddress();

        if (TextUtils.isEmpty(address)) {

            holder.textAddress.setVisibility(View.GONE);

        } else {

            holder.textAddress.setVisibility(View.VISIBLE);

            holder.textAddress.setText(
                    "📍 " + address
            );
        }

        // תיאור
        String description =
                synagogue.getDescription();

        if (TextUtils.isEmpty(description)) {

            holder.textDescription.setVisibility(
                    View.GONE
            );

        } else {

            holder.textDescription.setVisibility(
                    View.VISIBLE
            );

            holder.textDescription.setText(
                    description
            );
        }

        // כרגע מציגים Meta בסיסי.
        // בהמשך נחבר אותו ישירות לנתוני
        // prayers ו-features החדשים.
        holder.textMeta.setText(
                "בית כנסת פעיל"
        );

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onSynagogueClick(
                        synagogue
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return synagogueList.size();
    }

    static class SynagogueViewHolder
            extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textAddress;
        TextView textDescription;
        TextView textMeta;

        SynagogueViewHolder(
                @NonNull View itemView) {

            super(itemView);

            textName =
                    itemView.findViewById(
                            R.id.textName
                    );

            textAddress =
                    itemView.findViewById(
                            R.id.textAddress
                    );

            textDescription =
                    itemView.findViewById(
                            R.id.textDescription
                    );

            textMeta =
                    itemView.findViewById(
                            R.id.textMeta
                    );
        }
    }
}
