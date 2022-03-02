package com.cs.android.usb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs.android.R;
import com.cs.android.usb.bean.CommonFile;

import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/20 15:39
 **/
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListHolder> {
    public static final String TAG = FileListAdapter.class.getSimpleName();
    private UDiskActivity mContext;
    private List<CommonFile> mData;

    public FileListAdapter(UDiskActivity context) {
        this.mContext = context;
    }

    public void setData(List<CommonFile> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, parent, false);
        return new FileListHolder(view);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FileListHolder holder, int position) {
        CommonFile file = mData.get(position);

        holder.tvFileName.setText(file.getDisplayName());
        if (file.isDirectory()) {
            holder.ivFileLogo.setImageResource(R.drawable.ic_dir);
        } else {
            holder.ivFileLogo.setImageResource(R.drawable.ic_file);
        }

        holder.cbChecked.setOnCheckedChangeListener(null);

        if (file.isChecked()) {
            holder.cbChecked.setChecked(true);
            holder.itemView.setSelected(true);
        } else {
            holder.cbChecked.setChecked(false);
            holder.itemView.setSelected(false);
        }

        holder.itemView.setOnClickListener(v -> {
            mContext.enterToChild(file);
        });

        holder.cbChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "setOnCheckedChangeListener " + isChecked);
            if (isChecked) {
                file.setChecked(true);
                holder.itemView.setSelected(true);
            } else {
                file.setChecked(false);
                holder.itemView.setSelected(false);
            }
        });
    }

    static class FileListHolder extends RecyclerView.ViewHolder {
        private final ImageView ivFileLogo;
        private final TextView tvFileName;
        private final CheckBox cbChecked;

        public FileListHolder(@NonNull View itemView) {
            super(itemView);
            ivFileLogo = itemView.findViewById(R.id.ivFileLogo);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            cbChecked = itemView.findViewById(R.id.cbChecked);
        }
    }
}
