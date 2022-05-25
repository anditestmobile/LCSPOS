package id.co.lcs.pos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.co.lcs.pos.R;
import id.co.lcs.pos.models.SerialNumber;

public class SerialNumberAdapter extends RecyclerView.Adapter<SerialNumberAdapter.SNViewHolder>  {
    private ArrayList<SerialNumber> mDataset;
    RecyclerViewItemClickListener recyclerViewItemClickListener;

    public SerialNumberAdapter(ArrayList<SerialNumber> myDataset, RecyclerViewItemClickListener listener) {
        mDataset = myDataset;
        this.recyclerViewItemClickListener = listener;
    }

    @NonNull
    @Override
    public SNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_sn, parent, false);

        SNViewHolder vh = new SNViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull SNViewHolder snViewHolder, int i) {
        SerialNumber data = mDataset.get(i);
        snViewHolder.mTextView.setText(data.getSysSerial()+ " - " + data.getSerialNumber());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



    public  class SNViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;

        public SNViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.txtSerialNo);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewItemClickListener.clickOnItem(mDataset.get(this.getAdapterPosition()));

        }
    }

    public interface RecyclerViewItemClickListener {
        void clickOnItem(SerialNumber data);
    }
}
