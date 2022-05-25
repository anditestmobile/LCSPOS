package id.co.lcs.pos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import id.co.lcs.pos.R;
import id.co.lcs.pos.adapter.MainAdapter;
import id.co.lcs.pos.adapter.SerialNumberAdapter;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityMainBinding;
import id.co.lcs.pos.models.Customer;
import id.co.lcs.pos.models.Employee;
import id.co.lcs.pos.models.Payment;
import id.co.lcs.pos.models.PaymentItem;
import id.co.lcs.pos.models.QuotationItemResponse;
import id.co.lcs.pos.models.QuotationResponse;
import id.co.lcs.pos.models.SerialNumber;
import id.co.lcs.pos.service.SessionManager;
import id.co.lcs.pos.utils.Helper;
import id.co.lcs.pos.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, SerialNumberAdapter.RecyclerViewItemClickListener {
    private MainAdapter adapter;
    private ActivityMainBinding binding;
    public int PARAM = 0;
    String[] method = {"%", "$"};
    private Button btnNext, btnScan;
    private Dialog dialog;
    private QuotationItemResponse[] qIR;
    private Customer[] listCust;
    private Employee[] listEmp;
    private SerialNumber[] snArray;
    private ArrayList<SerialNumber> listSN;
    private String scan;
    private List<QuotationItemResponse> listQIR;
    //    private List<SerialNumber> serialNumbers;
    private List<PaymentItem> listPaymentItem;
    private Payment payment = new Payment();
    private double subTotal, disc, taxTotal, grandTotal = 0;
    private String textCust, textEmp, textItem, employeeName, itemName, itemCode;
    private int FLAG_CUST = 0;
    private EditText edtScan;
    private RequestUrl mAsyncTask;
    private CustomListViewDialog customDialog;
    private QuotationItemResponse itemTemp;

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
                } else if (listQIR == null) {
                    setToast("Item cannot empty");
                } else if (listQIR.size() == 0) {
                    setToast("Item cannot empty");
                } else {
                    if (binding.txtQuoNo.getText().toString().isEmpty() || binding.txtSONo.getText().toString().isEmpty()) {
                        if (binding.txtQuoNo.getText().toString().isEmpty()) {
                            Helper.setItemParam(Constants.SO_FLAG, true);
                        } else {
                            Helper.setItemParam(Constants.SO_FLAG, false);
                        }
                    } else {
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
                    if (!binding.txtQuoNo.getText().toString().isEmpty()) {
                        payment.setQuoNo(binding.txtQuoNo.getText().toString());
                    } else {
                        payment.setQuoNo(binding.txtSONo.getText().toString());
                    }
                    payment.setDocDate("2021-07-26");
                    listPaymentItem = new ArrayList<>();
                    for (QuotationItemResponse temp : listQIR) {
                        PaymentItem paymentItem = new PaymentItem();
                        paymentItem.setItemName(temp.getDescription());
                        paymentItem.setUom(temp.getUom());
                        if (temp.getIsSerialNumber().equals("Y")) {
                            paymentItem.setSerialNumbers(temp.getSerialNumber());
                        }
                        paymentItem.setItemCode(temp.getItemCode());
                        paymentItem.setUnitPrice(temp.getPrice());
                        paymentItem.setQty(Double.parseDouble(temp.getQty()));
                        paymentItem.setUom(temp.getUom());
                        if (temp.getDiscMethod() == 0) {
                            paymentItem.setDiscPer(temp.getDisc());
                        } else {
                            paymentItem.setDiscSum(temp.getDisc());
                        }
                        paymentItem.setLineTotal(Double.parseDouble(String.format("%.2f", temp.getPrice() * Double.parseDouble(temp.getQty()))));
                        listPaymentItem.add(paymentItem);
                    }
                    payment.setProductInfo(listPaymentItem);
                    payment.setEmployeeName(employeeName);
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
                    if (binding.txtQuoNo.getText().toString().isEmpty() || binding.txtQuoNo.getText().toString().isEmpty()) {
                        if (binding.txtQuoNo.getText().toString().isEmpty()) {
                            Helper.setItemParam(Constants.SO_FLAG, true);
                        } else {
                            Helper.setItemParam(Constants.SO_FLAG, false);
                        }
                    } else {
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
                    if (!binding.txtQuoNo.getText().toString().isEmpty()) {
                        payment.setQuoNo(binding.txtQuoNo.getText().toString());
                    } else {
                        payment.setQuoNo(binding.txtSONo.getText().toString());
                    }
                    payment.setDocDate("2021-07-26");
                    listPaymentItem = new ArrayList<>();
                    for (QuotationItemResponse temp : listQIR) {
                        PaymentItem paymentItem = new PaymentItem();
                        paymentItem.setItemName(temp.getDescription());
                        paymentItem.setItemCode(temp.getItemCode());
                        paymentItem.setUnitPrice(temp.getPrice());
                        if (temp.getIsSerialNumber().equals("Y")) {
                            paymentItem.setSerialNumbers(temp.getSerialNumber());
                        }
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
                                        callASyncTask();
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
                        callASyncTask();
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
                                        callASyncTask();
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
                        callASyncTask();
                        getProgressDialog().show();
//                        new RequestUrl().execute();
//                        getProgressDialog().show();
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
                if (FLAG_CUST == 0) {
//                        && editable.toString().length() >= 3) {
                    binding.txtCustCode.setText("");
                    textCust = editable.toString();
                    PARAM = 2;
                    callASyncTask();
//                    getProgressDialog().show();
                } else {
                    FLAG_CUST = 0;
                }
            }
        });

        binding.edtEmp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listEmp != null && listEmp.length != 0) {
                    for (Employee temp : listEmp) {
                        if ((temp.getFirstName() + " " + temp.getLastName()).equals(editable.toString())) {
                            employeeName = temp.getFirstName() + " " + temp.getLastName();
                            FLAG_CUST = 1;
                        }
                    }
                }
                if (FLAG_CUST == 0) {
                    textEmp = editable.toString();
                    PARAM = 5;
                    callASyncTask();
//                    getProgressDialog().show();
                } else {
                    FLAG_CUST = 0;
                }
            }
        });

        binding.txtItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    if (qIR != null && qIR.length != 0) {
                        for (QuotationItemResponse temp : qIR) {
                            if ((temp.getItemCode() + "\n" + temp.getDescription()).equals(editable.toString())) {
                                if (temp.getIsSerialNumber().equals("N")) {
                                    itemName = temp.getDescription();
                                    setDataItem(temp);
                                } else {
                                    itemName = temp.getDescription();
                                    itemCode = temp.getItemCode();
                                    itemTemp = temp;
                                    getProgressDialog().show();
                                    PARAM = 6;
                                    callASyncTask();
                                }
                                FLAG_CUST = 1;

                            }
                        }
                    }
                    if (FLAG_CUST == 0) {
                        textItem = editable.toString();
                        PARAM = 1;
                        callASyncTask();
//                    getProgressDialog().show();
                    } else {
                        FLAG_CUST = 0;
                    }
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
                    if (!binding.txtSONo.getText().toString().isEmpty() || !binding.txtQuoNo.getText().toString().isEmpty()) {
                        PARAM = 1;
                        callASyncTask();
                        getProgressDialog().show();
                    } else {
                        if (!binding.txtCustCode.getText().toString().isEmpty()) {
                            PARAM = 1;
                            callASyncTask();
                            getProgressDialog().show();
                        } else {
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

    public void callASyncTask() {
        if (mAsyncTask != null) {
            if (mAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                mAsyncTask.cancel(true);
            }
        }
        mAsyncTask = new RequestUrl();
        mAsyncTask.execute();
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
                    callASyncTask();
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
                    callASyncTask();
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
                                .concat(binding.txtQuoNo.getText().toString().concat("\\").concat(textItem));
                    } else if (!binding.txtSONo.getText().toString().trim().isEmpty()) {
                        String URL_GET_ITEM = Constants.API_PREFIX + Constants.API_GET_ORDER_ITEM;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_ITEM)
                                .concat(binding.txtSONo.getText().toString().concat("\\").concat(textItem));
                    } else {
                        String URL_GET_QUO_ITEM = Constants.API_PREFIX + Constants.API_GET_ITEM;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_QUO_ITEM)
                                .concat("\\").concat(textItem).concat("\\").concat(binding.txtCustCode.getText().toString());
                    }
                    qIR = (QuotationItemResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationItemResponse[].class);
                    return null;
                } else if (PARAM == 2) {
                    String URL_GET_CUSTOMER = Constants.API_PREFIX + Constants.API_GET_CUSTOMER;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_CUSTOMER)
                            .concat("\\").concat(textCust);
                    listCust = (Customer[]) Helper.getWebserviceWithoutHeaders(url, Customer[].class);
                    return null;
                } else if (PARAM == 3) {
                    String URL_GET_QUOTATION = Constants.API_PREFIX + Constants.API_GET_ORDER;

                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_QUOTATION)
                            .concat(binding.txtSONo.getText().toString());

                    return (QuotationResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationResponse[].class);
                } else if (PARAM == 4) {
                    String url = "";
                    if (!binding.txtQuoNo.getText().toString().trim().isEmpty()) {
                        String URL_GET_ITEM = Constants.API_PREFIX + Constants.API_GET_QUOTATION_DETAILS;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_ITEM)
                                .concat(binding.txtQuoNo.getText().toString());
                    } else {
                        String URL_GET_ITEM = Constants.API_PREFIX + Constants.API_GET_SALES_ORDER_DETAILS;
                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_ITEM)
                                .concat(binding.txtSONo.getText().toString());
                    }
                    qIR = (QuotationItemResponse[]) Helper.getWebserviceWithoutHeaders(url, QuotationItemResponse[].class);
                    return null;
                } else if (PARAM == 5) {
                    String URL_GET_EMPLOYEE = Constants.API_PREFIX + Constants.API_GET_EMPLOYEE;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_EMPLOYEE)
                            .concat("\\").concat(textEmp);
                    listEmp = (Employee[]) Helper.getWebserviceWithoutHeaders(url, Employee[].class);
                    return null;
                } else {
                    String URL_GET_SN = Constants.API_PREFIX + Constants.API_GET_SN;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_SN)
                            .concat("\\").concat(itemCode);
                    snArray = (SerialNumber[]) Helper.getWebserviceWithoutHeaders(url, SerialNumber[].class);
                    return null;
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
                    listCust = new Customer[1];
                    listCust[0] = new Customer(quotation[0].getCustomerCode(), quotation[0].getCustomerName());
                    binding.txtCustName.setText(quotation[0].getCustomerName());
                    binding.txtCustCode.setText(quotation[0].getCustomerCode());
                    getItem();
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 1) {
                getProgressDialog().dismiss();
                if (qIR != null && qIR.length != 0) {
//                    setDataItem(qIR);
                    setItem(qIR);
                } else {
                    setToast("item not found");
                }
            } else if (PARAM == 2) {
                getProgressDialog().dismiss();
                if (listCust != null && listCust.length != 0) {
                    setCustomer(listCust);
                } else {
//                    setToast("customer not found");
                }
            } else if (PARAM == 3) {
                getProgressDialog().dismiss();
                if (quotation != null && quotation.length != 0) {
                    listCust = new Customer[1];
                    listCust[0] = new Customer(quotation[0].getCustomerCode(), quotation[0].getCustomerName());
                    binding.txtCustName.setText(quotation[0].getCustomerName());
                    binding.txtCustCode.setText(quotation[0].getCustomerCode());
                    getItem();
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 4) {
                getProgressDialog().dismiss();
                if (qIR != null && qIR.length != 0) {
                    setDataItemQuotation(qIR);
                }
            } else if (PARAM == 5) {
                getProgressDialog().dismiss();
                if (listEmp != null && listEmp.length != 0) {
                    setEmployee(listEmp);
                } else {
//                    setToast("customer not found");
                }
            } else {
                getProgressDialog().dismiss();
                if (snArray != null && snArray.length != 0) {
                    showDialogSN(snArray);
                } else {
                    setToast("Serial Number not found/No Stock");
                }
            }
        }

    }

    private void showDialogSN(SerialNumber[] snArray) {
        listSN = new ArrayList<>();

        for (SerialNumber sn : snArray) {
            int FLAG = 0;
            if(listQIR != null && listQIR.size() != 0) {
                for (QuotationItemResponse temp : listQIR) {
                    if(temp.getSerialNumber() != null && temp.getSerialNumber().size() != 0){
                        if (sn.getSysSerial().equals(temp.getSerialNumber().get(0).getSysSerial())
                                && sn.getSerialNumber().equals(temp.getSerialNumber().get(0).getSerialNumber())) {
                            FLAG = 1;
                            break;
                        }
                    }
                }
                if (FLAG == 0) {
                    listSN.add(sn);
                }
            }else{
                listSN.add(sn);
            }
        }
        SerialNumberAdapter dataAdapter = new SerialNumberAdapter(listSN, this);
        customDialog = new CustomListViewDialog(MainActivity.this, dataAdapter);

        customDialog.show();
        customDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void clickOnItem(SerialNumber data) {
//        clickButton.setText(data);
        List<SerialNumber> serialNumbers = new ArrayList<>();
        serialNumbers.add(data);
        itemTemp.setSerialNumber(serialNumbers);
        setDataItem(itemTemp);
        if (customDialog != null) {
            customDialog.dismiss();
        }
    }

    private void getItem() {
        PARAM = 4;
        callASyncTask();
        getProgressDialog().show();
    }

    private void setCustomer(Customer[] listCust) {
        String[] cust = new String[listCust.length];
        for (int i = 0; i < listCust.length; i++) {
            cust[i] = listCust[i].getCustName();
        }

        ArrayAdapter adapter = new
                ArrayAdapter(this, R.layout.list_customer, R.id.item, cust);
        binding.txtCustName.setAdapter(adapter);
        binding.txtCustName.showDropDown();
    }

    private void setEmployee(Employee[] listEmp) {
        String[] emp = new String[listEmp.length];
        for (int i = 0; i < listEmp.length; i++) {
            emp[i] = listEmp[i].getFirstName() + " " + listEmp[i].getLastName();
        }

        ArrayAdapter adapter = new
                ArrayAdapter(this, R.layout.list_customer, R.id.item, emp);
        binding.edtEmp.setAdapter(adapter);
        binding.edtEmp.showDropDown();
    }

    private void setItem(QuotationItemResponse[] item) {
        String[] itm = new String[item.length];
        for (int i = 0; i < item.length; i++) {
            itm[i] = item[i].getItemCode() + "\n" + item[i].getDescription();
        }

        ArrayAdapter adapter = new
                ArrayAdapter(this, R.layout.list_customer, R.id.item, itm);
        binding.txtItem.setAdapter(adapter);
        binding.txtItem.showDropDown();
    }

    private void setDataItem(QuotationItemResponse item) {
//        binding.txtItem.clearFocus();
        double disc = item.getPrice() * item.getDisc() / 100;
        double nettPrice = item.getDiscPrice();
        double totPrice = nettPrice * Integer.parseInt(item.getQty());
        item.setTotPrice(Double.parseDouble(String.format("%.2f", totPrice)));
        item.setNettPrice(Double.parseDouble(String.format("%.2f", nettPrice)));
        if (listQIR != null && listQIR.size() != 0) {
            int FLAG = 0;
            for (QuotationItemResponse temp : listQIR) {
                if (temp.getIsSerialNumber().equals("N")) {
                    if (temp.getItemCode().equals(item.getItemCode())) {
                        temp.setQty(String.valueOf(Double.parseDouble(temp.getQty()) + Double.parseDouble(item.getQty())));
                        FLAG = 1;
                        break;
                    } else {
                        continue;
                    }
                } else {
                    if (temp.getItemCode().equals(item.getItemCode())
                            && temp.getSerialNumber().get(0).getSerialNumber().equals(item.getSerialNumber().get(0).getSerialNumber())) {
                        temp.setQty(String.valueOf(Double.parseDouble(temp.getQty()) + Double.parseDouble(item.getQty())));
                        FLAG = 1;
                        break;
                    } else {
                        continue;
                    }
                }
            }
            if (FLAG == 0) {
                listQIR.add(item);
            }
            adapter.filterList(listQIR);
        } else {
//            if(listQIR == null) {
            listQIR = new ArrayList<>();
//                for (QuotationItemResponse q : item) {
            listQIR.add(item);
//                }
            adapter = new MainAdapter(MainActivity.this, listQIR);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

            binding.rcItem.setLayoutManager(layoutManager);

            binding.rcItem.setAdapter(adapter);
//            }else{
//                for (QuotationItemResponse q : item) {
//                    listQIR.add(q);
//                }
//                adapter.filterList(listQIR);
//            }
        }
        countData(listQIR);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        binding.txtItem.requestFocus();
//            }
//        }, 2000);
        binding.txtItem.dismissDropDown();
    }

    private void setDataItemQuotation(QuotationItemResponse[] item) {
//        binding.txtItem.clearFocus();
        double disc = item[0].getPrice() * item[0].getDisc() / 100;
        double nettPrice = item[0].getDiscPrice();
        double totPrice = nettPrice * Integer.parseInt(item[0].getQty());
        item[0].setTotPrice(Double.parseDouble(String.format("%.2f", totPrice)));
        item[0].setNettPrice(Double.parseDouble(String.format("%.2f", nettPrice)));
        if (listQIR != null && listQIR.size() != 0) {
            int FLAG = 0;
            for (QuotationItemResponse temp : listQIR) {
                if (temp.getItemCode().equals(item[0].getItemCode())) {
                    temp.setQty(String.valueOf(Double.parseDouble(temp.getQty()) + Double.parseDouble(item[0].getQty())));
                    FLAG = 1;
                    break;
                } else {
                    continue;
                }
            }
            if (FLAG == 0) {
                listQIR.add(item[0]);
            }
            adapter.filterList(listQIR);
        } else {
//            if(listQIR == null) {
            listQIR = new ArrayList<>();
            for (QuotationItemResponse q : item) {
                listQIR.add(q);
            }
            adapter = new MainAdapter(MainActivity.this, listQIR);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

            binding.rcItem.setLayoutManager(layoutManager);

            binding.rcItem.setAdapter(adapter);
//            }else{
//                for (QuotationItemResponse q : item) {
//                    listQIR.add(q);
//                }
//                adapter.filterList(listQIR);
//            }
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
                } else {
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

