package com.cs.android.bluetooth.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs.android.R;
import com.cs.android.bluetooth.BlueToothActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2022/1/11 8:28
 **/
public class BlueToothListAdapter extends RecyclerView.Adapter<BlueToothListAdapter.BlueToothHolder> {
    private BlueToothActivity mContext;
    private List<BluetoothDevice> mDevices;

    public BlueToothListAdapter(@NonNull BlueToothActivity mContext) {
        this.mContext = mContext;
    }

    public void add(BluetoothDevice device) {
        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }
        mDevices.add(device);
        notifyItemInserted(mDevices.size() - 1);
    }

    public void addAll(List<BluetoothDevice> devices) {
        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }
        mDevices.addAll(devices);
        notifyItemRangeChanged(mDevices.size() - devices.size(), devices.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if (mDevices != null) {
            mDevices.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public BlueToothHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth, parent, false);
        return new BlueToothHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BlueToothHolder holder, int position) {
        BluetoothDevice device = mDevices.get(position);
        String name = device.getName();
        holder.tvName.setText(name == null ? "未知" : name);
        holder.tvAddress.setText(device.getAddress());

        holder.itemView.setOnClickListener(v -> {
            if (mContext.isDiscovering()) {
                mContext.cancelDiscovering();
            }
            mContext.setConnectServer(device);
        });
    }

    @Override
    public int getItemCount() {
        return mDevices == null ? 0 : mDevices.size();
    }


    static class BlueToothHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;

        public BlueToothHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}

