package id.co.lcs.pos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.starmicronics.stario.PortInfo;

import java.util.List;

import id.co.lcs.pos.R;
import id.co.lcs.pos.activity.PaymentActivity;
import id.co.lcs.pos.printer.PrinterSettingConstant;

public class DialogPaymentAdapter extends RecyclerView.Adapter<DialogPaymentAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<PortInfo> listInfo;
    private PaymentActivity mContext;


    public DialogPaymentAdapter(PaymentActivity ctx, List<PortInfo> listInfo){

        inflater = LayoutInflater.from(ctx);
        this.listInfo = listInfo;
        mContext = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.macAddr.setText(listInfo.get(position).getMacAddress());
        holder.name.setText(listInfo.get(position).getPortName().substring(PrinterSettingConstant.IF_TYPE_BLUETOOTH.length()));
    }

    @Override
    public int getItemCount() {
        return listInfo.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView macAddr, name;


        public MyViewHolder(View itemView) {
            super(itemView);

            macAddr = itemView.findViewById(R.id.txtMacAddress);
            name = itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.setDataFromDialog(listInfo.get(getAdapterPosition()));
//                    Crea.textView.setText("You have selected : "+materialList[getAdapterPosition()]);
                    PaymentActivity.dialog.dismiss();
                }
            });

        }

    }
}
