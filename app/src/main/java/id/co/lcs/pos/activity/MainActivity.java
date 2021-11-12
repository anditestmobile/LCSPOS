package id.co.lcs.pos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import id.co.lcs.pos.R;
import id.co.lcs.pos.adapter.MainAdapter;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityMainBinding;
import id.co.lcs.pos.models.Customer;
import id.co.lcs.pos.models.Payment;
import id.co.lcs.pos.models.PaymentItem;
import id.co.lcs.pos.models.QuotationItemResponse;
import id.co.lcs.pos.models.QuotationResponse;
import id.co.lcs.pos.service.SessionManager;
import id.co.lcs.pos.utils.Helper;
import id.co.lcs.pos.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private MainAdapter adapter;
    private ActivityMainBinding binding;
    public int PARAM = 0;
    String[] method = {"%", "$"};
    private Button btnNext, btnScan;
    private Dialog dialog;
    private QuotationItemResponse[] qIR;
    private Customer[] listCust;
    private String scan;
    private List<QuotationItemResponse> listQIR;
    private List<PaymentItem> listPaymentItem;
    private Payment payment = new Payment();
    private double subTotal, disc, taxTotal, grandTotal = 0;
    private String textCust;
    private int FLAG_CUST = 0;
    private EditText edtScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();

        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtCustName.getText().toString().isEmpty()) {
                    setToast("Customer Name cannot empty");
                } else if (binding.txtCustCode.getText().toString().isEmpty()) {
                    setToast("Customer Code cannot empty");
                } else if (listQIR.size() == 0) {
                    setToast("Item cannot empty");
                } else {
                    if(binding.txtQuoNo.getText().toString().isEmpty() || binding.txtSONo.getText().toString().isEmpty()){
                        if(binding.txtQuoNo.getText().toString().isEmpty()){
                            Helper.setItemParam(Constants.SO_FLAG, false);
                        }else{
                            Helper.setItemParam(Constants.SO_FLAG, true);
                        }
                    }else{
                        Helper.setItemParam(Constants.SO_FLAG, false);
                    }
                    payment = new Payment();
                    payment.setCardName(binding.txtCustName.getText().toString());
                    payment.setCardCode(binding.txtCustCode.getText().toString());
                    if (binding.txtDisc.getText().toString().isEmpty()) {
                        payment.setDiscPer(0);
                        payment.setDiscSum(0);
                    } else {
                        if (binding.spinner.getSelectedItemPosition() == 0) {
                            payment.setDiscPer(Double.parseDouble(String.format("%.2f", disc)));
                            payment.setDiscSum(0);
                        } else if (binding.spinner.getSelectedItemPosition() == 1) {
                            payment.setDiscSum(Double.parseDouble(String.format("%.2f", disc)));
                            payment.setDiscPer(0);
                        } else {
                            payment.setDiscPer(0);
                            payment.setDiscSum(0);
                        }
                    }
                    payment.setDocTotal(Double.parseDouble(String.format("%.2f", grandTotal)));
                    if(!binding.txtQuoNo.getText().toString().isEmpty()) {
                        payment.setQuoNo(binding.txtQuoNo.getText().toString());
                    }else{
                        payment.setQuoNo(binding.txtSONo.getText().toString());
                    }
                    payment.setDocDate("2021-05-26");
                    listPaymentItem = new ArrayList<>();
                    for (QuotationItemResponse temp : listQIR) {
                        PaymentItem paymentItem = new PaymentItem();
                        paymentItem.setItemName(temp.getDescription());
                        paymentItem.setItemCode(temp.getItemCode());
                        paymentItem.setUnitPrice(temp.getPrice());
                        paymentItem.setQty(Double.parseDouble(temp.getQty()));
                        if(temp.getDiscMethod() == 0) {
                            paymentItem.setDiscPer(temp.getDisc());
                        }else{
                            paymentItem.setDiscSum(temp.getDisc());
                        }
                        paymentItem.setLineTotal(Double.parseDouble(String.format("%.2f", temp.getPrice() * Double.parseDouble(temp.getQty()))));
                        listPaymentItem.add(paymentItem);
                    }
                    payment.setProductInfo(listPaymentItem);
                    Helper.setItemParam(Constants.PAYMENT, payment);
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    startActivity(intent);
                }
            }
        });

        binding.btnDownPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtCustName.getText().toString().isEmpty()) {
                    setToast("Customer Name cannot empty");
                } else if (binding.txtCustCode.getText().toString().isEmpty()) {
                    setToast("Customer Code cannot empty");
                } else if (listQIR.size() == 0) {
                    setToast("Item cannot empty");
                } else {
                    if(binding.txtQuoNo.getText().toString().isEmpty() || binding.txtQuoNo.getText().toString().isEmpty()){
                        if(binding.txtQuoNo.getText().toString().isEmpty()){
                            Helper.setItemParam(Constants.SO_FLAG, true);
                        }else{
                            Helper.setItemParam(Constants.SO_FLAG, false);
                        }
                    }else{
                        Helper.setItemParam(Constants.SO_FLAG, false);
                    }
                    payment = new Payment();
                    payment.setCardName(binding.txtCustName.getText().toString());
                    payment.setCardCode(binding.txtCustCode.getText().toString());
                    if (binding.txtDisc.getText().toString().isEmpty()) {
                        payment.setDiscPer(0);
                        payment.setDiscSum(0);
                    } else {
                        if (binding.spinner.getSelectedItemPosition() == 0) {
                            payment.setDiscPer(Double.parseDouble(String.format("%.2f", disc)));
                            payment.setDiscSum(0);
                        } else if (binding.spinner.getSelectedItemPosition() == 1) {
                            payment.setDiscSum(Double.parseDouble(String.format("%.2f", disc)));
                            payment.setDiscPer(0);
                        } else {
                            payment.setDiscPer(0);
                            payment.setDiscSum(0);
                        }
                    }
                    payment.setDocTotal(Double.parseDouble(String.format("%.2f", grandTotal)));
                    if(!binding.txtQuoNo.getText().toString().isEmpty()) {
                        payment.setQuoNo(binding.txtQuoNo.getText().toString());
                    }else{
                        payment.setQuoNo(binding.txtSONo.getText().toString());
                    }
                    payment.setDocDate("2021-05-26");
                    listPaymentItem = new ArrayList<>();
                    for (QuotationItemResponse temp : listQIR) {
                        PaymentItem paymentItem = new PaymentItem();
                        paymentItem.setItemName(temp.getDescription());
                        paymentItem.setItemCode(temp.getItemCode());
                        paymentItem.setUnitPrice(temp.getPrice());
                        paymentItem.setQty(Double.parseDouble(temp.getQty()));
                        paymentItem.setDiscPer(temp.getDisc());
//                        paymentItem.setDiscSum(temp.getNettPrice());
                        paymentItem.setLineTotal(Double.parseDouble(String.format("%.2f", temp.getPrice() * Double.parseDouble(temp.getQty()))));
                        listPaymentItem.add(paymentItem);
                    }
                    payment.setProductInfo(listPaymentItem);
                    Helper.setItemParam(Constants.PAYMENT, payment);
                    Intent intent = new Intent(getApplicationContext(), DownPaymentActivity.class);
                    startActivity(intent);
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure to cancel this transaction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listQIR.clear();
                                adapter.filterList(listQIR);
                                binding.txtQuoNo.setText("");
                                binding.txtCustCode.setText("");
                                binding.txtCustName.setText("");
                                binding.txtGrandTotal.setText("");
                                binding.txtTaxTotal.setText("");
                                binding.txtSubtot.setText("");
                                binding.txtDisc.setText("");
                                binding.spinner.setSelection(0);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        binding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SessionManager(getApplicationContext()).clearData();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        binding.spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_2, method);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_2);
        binding.spinner.setAdapter(arrayAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (listQIR != null && listQIR.size() != 0)
                    countData(listQIR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtQuoNo.getText().toString().trim().isEmpty()) {
                    setToast("Quotation cannot empty");
                } else {
                    binding.txtSONo.setText("");
                    if (listQIR != null && listQIR.size() != 0) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Your data won't be save. Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listQIR.clear();
                                        adapter.filterList(listQIR);
                                        binding.txtCustCode.setText("");
                                        binding.txtCustName.setText("");
                                        binding.txtGrandTotal.setText("");
                                        binding.txtTaxTotal.setText("");
                                        binding.txtSubtot.setText("");
                                        binding.txtDisc.setText("");
                                        binding.spinner.setSelection(0);
                                        PARAM = 0;
                                        new RequestUrl().execute();
                                        getProgressDialog().show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    } else {
                        PARAM = 0;
                        new RequestUrl().execute();
                        getProgressDialog().show();
                    }
                }
            }
        });


        binding.btnGet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtSONo.getText().toString().trim().isEmpty()) {
                    setToast("Sales order no. cannot empty");
                } else {
                    binding.txtQuoNo.setText("");
                    if (listQIR != null && listQIR.size() != 0) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Your data won't be save. Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listQIR.clear();
                                        adapter.filterList(listQIR);
                                        binding.txtCustCode.setText("");
                                        binding.txtCustName.setText("");
                                        binding.txtGrandTotal.setText("");
                                        binding.txtTaxTotal.setText("");
                                        binding.txtSubtot.setText("");
                                        binding.txtDisc.setText("");
                                        binding.spinner.setSelection(0);
                                        PARAM = 3;
                                        new RequestUrl().execute();
                                        getProgressDialog().show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    } else {
                        PARAM = 3;
                        new RequestUrl().execute();
                        getProgressDialog().show();
                    }
                }
            }
        });

        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
//                startActivity(intent);
                openDialogScan();
//                if (!binding.txtQuoNo.getText().toString().trim().isEmpty()) {
//
//                } else {
//                    setToast("Quotation No. Cannot Empty");
//                }
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
                if (listQIR != null && listQIR.size() != 0) {
                    countData(listQIR);
                } else {
                    if (!editable.toString().isEmpty()) {
                        setToast("please add item first");
                        binding.txtDisc.setText("");
                    }
                }
            }
        });

        binding.txtCustName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listCust != null && listCust.length != 0) {
                    for (Customer temp : listCust) {
                        if (temp.getCustName().equals(editable.toString())) {
                            binding.txtCustCode.setText(temp.getCustCode());
                            FLAG_CUST = 1;
                        }
                    }
                }
                if (FLAG_CUST == 0 && editable.toString().length() >= 3) {
                    binding.txtCustCode.setText("");
                    textCust = editable.toString();
                    PARAM = 2;
                    new RequestUrl().execute();
                } else {
                    FLAG_CUST = 0;
                }
            }
        });

        binding.btnGet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.txtItem.getText().toString().trim().isEmpty()) {
                    scan = binding.txtItem.getText().toString();
                    if(!binding.txtSONo.getText().toString().isEmpty() || !binding.txtQuoNo.getText().toString().isEmpty()) {
                        PARAM = 1;
                        new RequestUrl().execute();
                        getProgressDialog().show();
                    }else{
                        if(!binding.txtCustCode.getText().toString().isEmpty()){
                            PARAM = 1;
                            new RequestUrl().execute();
                            getProgressDialog().show();
                        }else{
                            setToast("Customer cannot empty");
                        }
                    }
                } else {
                    setToast("Item/Barcode cannot empty");
                }
            }
        });

//        binding.txtItem.addTextChangedListener(new TextWatcher() {
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
//                if (!editable.toString().isEmpty() || editable.length() > 5) {
//                    scan = binding.txtItem.getText().toString();
//                    binding.txtItem.setText("");
//                    PARAM = 1;
//                    new RequestUrl().execute();
//                    getProgressDialog().show();
//                }
//            }
//        });

    }

    private void openDialogScan() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_scan1);
        edtScan = dialog.findViewById(R.id.edtScan);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtScan.getText().toString().trim().isEmpty()) {
                    scan = edtScan.getText().toString();
                    PARAM = 1;
                    new RequestUrl().execute();
                    getProgressDialog().show();
                } else {
                    setToast("Item/Barcode cannot empty");
                }
            }
        });
        dialog.show();
    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_scan, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog a = dialogBuilder.create();
        edtScan = dialogView.findViewById(R.id.edtScan);
        final ImageButton imgBtnClose = dialogView.findViewById(R.id.imgDelete);
        final ImageButton imgBtnReset = dialogView.findViewById(R.id.imgReset);
        btnNext = dialogView.findViewById(R.id.btnNext);
        btnScan = dialogView.findViewById(R.id.btnScan);


        if (Helper.getItemParam(Constants.BARCODERESULT) != null) {
            edtScan.setText(Helper.getItemParam(Constants.BARCODERESULT).toString());
            btnNext.setEnabled(true);
            setButton(btnNext, "NEXT", R.drawable.btn_blue);
        } else {
            btnNext.setEnabled(false);
            setButton(btnNext, "NEXT", R.drawable.btn_grey);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edtScan.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Input/Scan Item first", Toast.LENGTH_LONG).show();
                } else {
                    a.dismiss();
                    Helper.removeItemParam(Constants.POS_MENU);
                    Helper.removeItemParam(Constants.BARCODERESULT);
                    PARAM = 0;
                    new RequestUrl().execute();
                    getProgressDialog().show();
//                    insertDummyData();
                }
            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                a.dismiss();
            }
        });
        imgBtnReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                edtScan.setText("");
            }
        });
        edtScan.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (!s.toString().trim().equals("")) {
                    btnNext.setEnabled(true);
                    setButton(btnNext, "NEXT", R.drawable.btn_blue);
                } else {
                    btnNext.setEnabled(false);
                    setButton(btnNext, "NEXT", R.drawable.btn_grey);
                }
            }
        });
        a.show();
    }

    public void deleteItem(int position) {
        listQIR.remove(position);
        adapter.filterList(listQIR);
        countData(listQIR);
    }

    public void changeQty() {
        countData(listQIR);
    }

    private class RequestUrl extends AsyncTask<Void, Void, QuotationResponse[]> {

        @Override
        protected QuotationResponse[] doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_GET_QUOTATION = Constants.API_PREFIX + Constants.API_GET_QUOTATION;

                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_QUOTATION).concat(binding.txtQuoNo.getText().toString());

                    return (QuotationResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationResponse[].class);
                } else if (PARAM == 1) {
                    String url = "";
                    if (!binding.txtQuoNo.getText().toString().trim().isEmpty()) {
                        String URL_GET_ITEM = Constants.API_PREFIX + Constants.API_GET_QUOTATION_ITEM;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_ITEM)
                                .concat(binding.txtQuoNo.getText().toString().concat("\\").concat(scan));
                    }else if(!binding.txtSONo.getText().toString().trim().isEmpty()) {
                        String URL_GET_ITEM = Constants.API_PREFIX + Constants.API_GET_ORDER_ITEM;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_ITEM)
                                .concat(binding.txtSONo.getText().toString().concat("\\").concat(scan));
                    }else {
                        String URL_GET_QUO_ITEM = Constants.API_PREFIX + Constants.API_GET_ITEM;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_QUO_ITEM)
                                .concat("\\").concat(scan).concat("\\").concat(binding.txtCustCode.getText().toString());
                    }
                    qIR = (QuotationItemResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationItemResponse[].class);
                    return null;
                } else if(PARAM == 2){
                    String URL_GET_CUSTOMER = Constants.API_PREFIX + Constants.API_GET_CUSTOMER;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_CUSTOMER)
                            .concat("\\").concat(textCust);
                    listCust = (Customer[]) Helper.getWebserviceWithoutHeaders(url, Customer[].class);
                    return null;
                }else{
                    String URL_GET_QUOTATION = Constants.API_PREFIX + Constants.API_GET_ORDER;

                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_QUOTATION)
                            .concat(binding.txtSONo.getText().toString());

                    return (QuotationResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationResponse[].class);
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Helper.setItemParam(Constants.INTERNAL_SERVER_ERROR, ex.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(QuotationResponse[] quotation) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if (quotation != null && quotation.length != 0) {
                    binding.txtCustName.setText(quotation[0].getCustomerName());
                    binding.txtCustCode.setText(quotation[0].getCustomerCode());
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 1) {
                getProgressDialog().dismiss();
                if (qIR.length != 0) {
                    setDataItem(qIR);
                } else {
                    setToast("item not found");
                }
            } else if(PARAM == 2) {
                if (listCust != null && listCust.length != 0) {
                    setCustomer(listCust);
                } else {
                    setToast("customer not found");
                }
            }else{
                getProgressDialog().dismiss();
                if (quotation != null && quotation.length != 0) {
                    binding.txtCustName.setText(quotation[0].getCustomerName());
                    binding.txtCustCode.setText(quotation[0].getCustomerCode());
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void setCustomer(Customer[] listCust) {
        String[] cust = new String[listCust.length];
        ;
        for (int i = 0; i < listCust.length; i++) {
            cust[i] = listCust[i].getCustName();
        }

        ArrayAdapter adapter = new
                ArrayAdapter(this, R.layout.list_customer, R.id.item, cust);
        binding.txtCustName.setAdapter(adapter);
        binding.txtCustName.showDropDown();
    }

    private void setDataItem(QuotationItemResponse[] qIR) {
//        binding.txtItem.clearFocus();
        double disc = qIR[0].getPrice() * qIR[0].getDisc()/100;
        double nettPrice = qIR[0].getPrice() - disc;
        double totPrice = nettPrice * Integer.parseInt(qIR[0].getQty());
        qIR[0].setTotPrice(Double.parseDouble(String.format("%.2f",totPrice)));
        qIR[0].setNettPrice(Double.parseDouble(String.format("%.2f",nettPrice)));
        if (listQIR == null) {
            listQIR = new ArrayList<>();
            listQIR.add(qIR[0]);
            adapter = new MainAdapter(MainActivity.this, listQIR);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

            binding.rcItem.setLayoutManager(layoutManager);

            binding.rcItem.setAdapter(adapter);
        } else {
            int FLAG = 0;
            for (QuotationItemResponse temp : listQIR) {
                if (temp.getItemCode().equals(qIR[0].getItemCode())) {
                    temp.setQty(String.valueOf(Double.parseDouble(temp.getQty()) + Double.parseDouble(qIR[0].getQty())));
                    FLAG = 1;
                    break;
                } else {
                    continue;
                }
            }
            if (FLAG == 0) {
                listQIR.add(qIR[0]);
            }
            adapter.filterList(listQIR);
        }
        countData(listQIR);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                binding.txtItem.requestFocus();
//            }
//        }, 2000);
    }

    private void countData(List<QuotationItemResponse> listQIR) {
        if (listQIR.size() == 0) {
            binding.txtSubtot.setText("S$ 0");
            binding.txtTaxTotal.setText("S$ 0");
            binding.txtGrandTotal.setText("S$ 0");
        } else {
            subTotal = 0;
            grandTotal = 0;
            taxTotal = 0;

            for (QuotationItemResponse temp : listQIR) {
//                subTotal = subTotal + temp.getPrice() * Double.parseDouble(temp.getQty());
                subTotal = subTotal + temp.getTotPrice();
            }
            if (binding.spinner.getSelectedItemPosition() == 0) {
                if (binding.txtDisc.getText().toString().trim().isEmpty() || binding.txtDisc.getText().toString().trim().equals("-")) {
                    disc = subTotal * (0 / 100);
                }else {
                    disc = subTotal * (Double.parseDouble(binding.txtDisc.getText().toString()) / 100);
                }
            } else {
                if (binding.txtDisc.getText().toString().trim().isEmpty()) {
                    disc = 0;
                } else {
                    disc = subTotal - (Double.parseDouble(binding.txtDisc.getText().toString()));
                }
            }
            taxTotal = (subTotal - disc) * (0.07);
            grandTotal = (subTotal - disc) + taxTotal;

            binding.txtSubtot.setText("S$ " + String.format("%.2f", subTotal));
            binding.txtTaxTotal.setText("S$ " + String.format("%.2f", taxTotal));
            binding.txtGrandTotal.setText("S$ " + String.format("%.2f", grandTotal));
//            edtScan.setText("");
//            dialog.dismiss();
            binding.txtItem.setText("");
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}