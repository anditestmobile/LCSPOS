package id.co.lcs.pos.adapter;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.pos.R;
import id.co.lcs.pos.activity.MainActivity;
import id.co.lcs.pos.databinding.RowViewMainBinding;
import id.co.lcs.pos.models.QuotationItemResponse;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.DataObjectHolder> {

    private List<QuotationItemResponse> dataList;
    private MainActivity mContext;
    private String[] method = {"%", "$"};

    public MainAdapter(MainActivity context, List<QuotationItemResponse> dataList) {
        this.dataList = dataList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewMainBinding binding;
        final Handler handler = new Handler();
        DataObjectHolder(RowViewMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.txtDisc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().isEmpty() || editable.toString().trim().equals("-")){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.txtDisc.setText(String.valueOf(dataList.get((Integer)binding.txtDisc.getTag()).getDisc()));
                            }
                        }, 3000);
                    }else{
                        if (binding.spinner.getSelectedItemPosition() == 0) {
                            double disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice()
                                    * (Double.parseDouble(editable.toString()) / 100)));
                            double nettPrice = dataList.get((Integer) binding.txtDisc.getTag()).getNettPrice();
                            if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
                                dataList.get((Integer) binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
                                disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice()
                                        * dataList.get((Integer) binding.txtDisc.getTag()).getDisc() / 100));
                                nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc));
                                dataList.get((Integer) binding.txtDisc.getTag()).setNettPrice(nettPrice);

                                double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
                                        * Double.parseDouble(dataList.get((Integer) binding.txtDisc.getTag()).getQty())));
                                dataList.get((Integer) binding.txtDisc.getTag()).setTotPrice(totPrice);
                                binding.txtTotPrice.setText("S$ " + totPrice);
                                binding.txtNettPrice.setText(String.valueOf(nettPrice));
                                mContext.changeQty();
                            }
                        }else{
                            double disc = Double.parseDouble(String.format("%.2f", (Double.parseDouble(editable.toString()))));
                            double nettPrice = dataList.get((Integer) binding.txtDisc.getTag()).getNettPrice();
                            if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
                                dataList.get((Integer) binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
                                disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getDisc()));
                                nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc));
                                dataList.get((Integer) binding.txtDisc.getTag()).setNettPrice(nettPrice);

                                double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
                                        * Double.parseDouble(dataList.get((Integer) binding.txtDisc.getTag()).getQty())));
                                dataList.get((Integer) binding.txtDisc.getTag()).setTotPrice(totPrice);
                                binding.txtTotPrice.setText("S$ " + totPrice);
                                binding.txtNettPrice.setText(String.valueOf(nettPrice));
                                mContext.changeQty();
                            }
                        }
                    }
                }
            });

            binding.txtDisc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().isEmpty() || editable.toString().trim().equals("-")){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.txtDisc.setText(String.valueOf(dataList.get((Integer)binding.txtDisc.getTag()).getDisc()));
                            }
                        }, 3000);
                    }else{
                        if (binding.spinner.getSelectedItemPosition() == 0) {
                            double disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice()
                                    * (Double.parseDouble(editable.toString()) / 100)));
                            double nettPrice = dataList.get((Integer) binding.txtDisc.getTag()).getNettPrice();
                            if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
                                dataList.get((Integer) binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
                                disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice()
                                        * dataList.get((Integer) binding.txtDisc.getTag()).getDisc() / 100));
                                nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc));
                                dataList.get((Integer) binding.txtDisc.getTag()).setNettPrice(nettPrice);

                                double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
                                        * Double.parseDouble(dataList.get((Integer) binding.txtDisc.getTag()).getQty())));
                                dataList.get((Integer) binding.txtDisc.getTag()).setTotPrice(totPrice);
                                binding.txtTotPrice.setText("S$ " + totPrice);
                                binding.txtNettPrice.setText(String.valueOf(nettPrice));
                                mContext.changeQty();
                            }
                        }else{
                            double disc = Double.parseDouble(String.format("%.2f", (Double.parseDouble(editable.toString()))));
                            double nettPrice = dataList.get((Integer) binding.txtDisc.getTag()).getNettPrice();
                            if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
                                dataList.get((Integer) binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
                                disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getDisc()));
                                nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) binding.txtDisc.getTag()).getPrice() - disc));
                                dataList.get((Integer) binding.txtDisc.getTag()).setNettPrice(nettPrice);

                                double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
                                        * Double.parseDouble(dataList.get((Integer) binding.txtDisc.getTag()).getQty())));
                                dataList.get((Integer) binding.txtDisc.getTag()).setTotPrice(totPrice);
                                binding.txtTotPrice.setText("S$ " + totPrice);
                                binding.txtNettPrice.setText(String.valueOf(nettPrice));
                                mContext.changeQty();
                            }
                        }
                    }
                }
            });

            binding.txtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().isEmpty() || editable.toString().equals("0")){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.txtQty.setText(dataList.get((Integer)binding.txtQty.getTag()).getQty());
                            }
                        }, 3000);
                    }else{
                        dataList.get((Integer)binding.txtQty.getTag()).setQty(editable.toString());
                        double disc = dataList.get((Integer)binding.txtQty.getTag()).getPrice()
                                * dataList.get((Integer)binding.txtQty.getTag()).getDisc()/100;
//                        double nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer)binding.txtQty.getTag()).getPrice() - disc));
                        double nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer)binding.txtNettPrice.getTag()).getNettPrice()));
//                        dataList.get((Integer)binding.txtDisc.getTag()).setNettPrice(nettPrice);
                        double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
                                * Double.parseDouble(dataList.get((Integer)binding.txtQty.getTag()).getQty())));
                        dataList.get((Integer)binding.txtQty.getTag()).setTotPrice(totPrice);
                        binding.txtTotPrice.setText("S$ " + totPrice);
//                    binding.txtNettPrice.setText(String.valueOf(nettPrice));
                        mContext.changeQty();
                    }
                }
            });
            binding.txtNettPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().isEmpty()){
                        dataList.get((Integer)binding.txtNettPrice.getTag())
                                .setTotPrice(Double.parseDouble(String.format("%.2f", (Double.parseDouble(s.toString())))));
                        dataList.get((Integer)binding.txtNettPrice.getTag())
                                .setNettPrice(Double.parseDouble(String.format("%.2f", (Double.parseDouble(s.toString())))));
                        mContext.changeQty();
                    }
                }
            });
        }


    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {
        final QuotationItemResponse data = dataList.get(position);
        holder.binding.txtQty.setTag(position);
        holder.binding.txtDisc.setTag(position);
        holder.binding.txtNettPrice.setTag(position);
        holder.binding.txtNo.setText(String.valueOf(position+1));
        holder.binding.txtUOM.setText(data.getUom());
        if(data.getIsSerialNumber().equals("N")) {
            holder.binding.txtItem.setText(data.getItemCode() + "\n" + data.getDescription());
        }else{
            holder.binding.txtItem.setText(data.getItemCode()+"\n"+data.getDescription()
                    +"\n"+ "S/N : " + data.getSerialNumber().get(0).getSerialNumber());
        }
        holder.binding.txtPrice.setText("S$ " + data.getPrice());
        holder.binding.txtTotPrice.setText("S$ " + data.getTotPrice());
        holder.binding.txtNettPrice.setText(String.valueOf(data.getNettPrice()));
        if(data.getIsPriceChange() == 1){
            holder.binding.txtNettPrice.setEnabled(true);
        }else{
            holder.binding.txtNettPrice.setEnabled(false);
        }

        holder.binding.txtQty.setText(data.getQty());

        holder.binding.spinner.setOnItemSelectedListener(mContext);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,
                R.layout.spinner_item_2, method);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.binding.spinner.setAdapter(arrayAdapter);
        holder.binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataList.get((Integer) holder.binding.txtDisc.getTag()).setDiscMethod(i);
                holder.binding.txtDisc.setText(holder.binding.txtDisc.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.deleteItem(position);
            }
        });

//        if(data.getDisc() != 0) {
//            holder.binding.spinner.setSelection(0);
            holder.binding.txtDisc.setText(String.valueOf(data.getDisc()));
//        }else{
//            holder.binding.spinner.setSelection(1);
//            holder.binding.txtDisc.setText(String.valueOf(data.getDiscPrice()));
//        }

//        holder.binding.txtQty.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(editable.toString().isEmpty() || editable.toString().equals("0")){
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.binding.txtQty.setText(dataList.get((Integer)holder.binding.txtQty.getTag()).getQty());
//                        }
//                    }, 3000);
//                }else{
//                    dataList.get((Integer)holder.binding.txtQty.getTag()).setQty(editable.toString());
//                    double disc = dataList.get((Integer)holder.binding.txtQty.getTag()).getPrice()
//                            * dataList.get((Integer)holder.binding.txtQty.getTag()).getDisc()/100;
//                    double nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer)holder.binding.txtQty.getTag()).getPrice() - disc));
//                    dataList.get((Integer)holder.binding.txtDisc.getTag()).setNettPrice(nettPrice);
//                    double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
//                            * Double.parseDouble(dataList.get((Integer)holder.binding.txtQty.getTag()).getQty())));
//                    dataList.get((Integer)holder.binding.txtQty.getTag()).setTotPrice(totPrice);
//                    holder.binding.txtTotPrice.setText("S$ " + totPrice);
////                    holder.binding.txtNettPrice.setText(String.valueOf(nettPrice));
//                    mContext.changeQty();
//                }
//            }
//        });

//        holder.binding.txtDisc.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(editable.toString().isEmpty() || editable.toString().trim().equals("-")){
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.binding.txtDisc.setText(String.valueOf(dataList.get((Integer)holder.binding.txtDisc.getTag()).getDisc()));
//                        }
//                    }, 3000);
//                }else{
//                    if (holder.binding.spinner.getSelectedItemPosition() == 0) {
//                        double disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice()
//                                * (Double.parseDouble(editable.toString()) / 100)));
//                        double nettPrice = dataList.get((Integer) holder.binding.txtDisc.getTag()).getNettPrice();
//                        if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
//                            disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice()
//                                    * dataList.get((Integer) holder.binding.txtDisc.getTag()).getDisc() / 100));
//                            nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice() - disc));
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setNettPrice(nettPrice);
//
//                            double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
//                                    * Double.parseDouble(dataList.get((Integer) holder.binding.txtDisc.getTag()).getQty())));
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setTotPrice(totPrice);
//                            holder.binding.txtTotPrice.setText("S$ " + totPrice);
//                            holder.binding.txtNettPrice.setText(String.valueOf(nettPrice));
//                            mContext.changeQty();
//                        }
//                    }else{
//                        double disc = Double.parseDouble(String.format("%.2f", (Double.parseDouble(editable.toString()))));
//                        double nettPrice = dataList.get((Integer) holder.binding.txtDisc.getTag()).getNettPrice();
//                        if (Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setDisc(Double.parseDouble(editable.toString()));
//                            disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getDisc()));
//                            nettPrice = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtDisc.getTag()).getPrice() - disc));
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setNettPrice(nettPrice);
//
//                            double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
//                                    * Double.parseDouble(dataList.get((Integer) holder.binding.txtDisc.getTag()).getQty())));
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setTotPrice(totPrice);
//                            holder.binding.txtTotPrice.setText("S$ " + totPrice);
//                            holder.binding.txtNettPrice.setText(String.valueOf(nettPrice));
//                            mContext.changeQty();
//                        }
//                    }
//                }
//            }
//        });

//        holder.binding.txtNettPrice.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(editable.toString().isEmpty()){
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.binding.txtDisc.setText(String.valueOf(dataList.get((Integer)holder.binding.txtDisc.getTag()).getDisc()));
//                        }
//                    }, 3000);
//                }else{
//                    double nettPrice =  Double.parseDouble(String.format("%.2f", Double.parseDouble(editable.toString())));
//                    double disc = Double.parseDouble(String.format("%.2f", dataList.get((Integer) holder.binding.txtNettPrice.getTag()).getPrice()
//                            * (dataList.get((Integer)holder.binding.txtNettPrice.getTag()).getDisc()/100)));
//                    if(Double.parseDouble(String.format("%.2f", dataList.get((Integer)holder.binding.txtDisc.getTag()).getPrice() - disc)) != nettPrice) {
//                        if (dataList.get((Integer) holder.binding.txtNettPrice.getTag()).getPrice() < nettPrice) {
//                            Toast.makeText(mContext, "Nett Price cannot bigger than price", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dataList.get((Integer) holder.binding.txtNettPrice.getTag()).setNettPrice(nettPrice);
//                            disc = Double.parseDouble(String.format("%.2f", ((dataList.get((Integer) holder.binding.txtNettPrice.getTag()).getPrice()
//                                    - nettPrice)/dataList.get((Integer) holder.binding.txtNettPrice.getTag()).getPrice()) * 100));
//                            dataList.get((Integer) holder.binding.txtDisc.getTag()).setDisc(disc);
//
//                            double totPrice = Double.parseDouble(String.format("%.2f", nettPrice
//                                    * Double.parseDouble(dataList.get((Integer) holder.binding.txtNettPrice.getTag()).getQty())));
//                            dataList.get((Integer) holder.binding.txtNettPrice.getTag()).setTotPrice(totPrice);
//                            holder.binding.txtTotPrice.setText("S$ " + totPrice);
//                            holder.binding.txtDisc.setText(String.valueOf(disc));
//                            mContext.changeQty();
//                        }
//                    }
//                }
//            }
//        });

        holder.binding.txtDisc.setText(String.valueOf(data.getDisc()));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(List<QuotationItemResponse> filteredList) {
        dataList = filteredList;
        this.notifyDataSetChanged();
    }


}
