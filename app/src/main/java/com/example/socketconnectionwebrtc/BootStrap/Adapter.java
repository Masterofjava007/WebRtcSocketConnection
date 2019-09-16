package com.example.socketconnectionwebrtc.BootStrap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.Implementer> {

    private List<BaseMessageHandler> baseMessageList = new ArrayList<>();


    @NonNull
    @Override
    public Implementer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main, parent, false);

        return new Implementer(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Implementer holder, int position) {
        BaseMessageHandler baseMessage = baseMessageList.get(position);
        holder.textViewRecycleerView.setText(baseMessage.getPayload().toString());
    }

    @Override
    public int getItemCount() {
        return baseMessageList.size();
    }

    public void setBaseMessageList(List<BaseMessageHandler> baseMessageList) {
        this.baseMessageList = baseMessageList;
        notifyDataSetChanged();
    }

    class Implementer extends RecyclerView.ViewHolder {

        private TextView textViewRecycleerView;

        public Implementer(@NonNull View itemView) {
            super(itemView);

            textViewRecycleerView = itemView.findViewById(R.id.textViewRecycleerView);
        }
    }
}
