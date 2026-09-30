package com.example.synagogue;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawerappsyn.R;


import java.util.ArrayList;

public class SynagogueAdapter extends RecyclerView.Adapter<SynagogueAdapter.SynagogueViewHolder> {
    private final ArrayList<Synagogue> synagogueList;
    private final OnSynagogueClickListener listener;

    public interface OnSynagogueClickListener {
        void onSynagogueClick(Synagogue synagogue);
    }

    public SynagogueAdapter(ArrayList<Synagogue> list, OnSynagogueClickListener listener) {
        synagogueList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SynagogueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new SynagogueViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SynagogueViewHolder holder, int position) {
        Synagogue s = synagogueList.get(position);
        holder.textName.setText(s.getName());
        holder.textAddress.setText("📍 " + s.getAddress());
        holder.textPhone.setText("📞 " + s.getPhone());
        holder.itemView.setOnClickListener(v -> listener.onSynagogueClick(s));
    }

    @Override
    public int getItemCount() {
        return synagogueList.size();
    }

    static class SynagogueViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textAddress, textPhone;

        SynagogueViewHolder(@NonNull View v) {
            super(v);
            textName = v.findViewById(R.id.textName);
            textAddress = v.findViewById(R.id.textAddress);
            textPhone = v.findViewById(R.id.textPhone);
        }
    }
}
