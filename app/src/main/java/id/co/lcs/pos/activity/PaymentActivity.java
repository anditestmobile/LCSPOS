package id.co.lcs.pos.activity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.StarIoExt;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cat.xojan.numpad.NumPadButton;
import cat.xojan.numpad.OnNumPadClickListener;
import id.co.lcs.pos.R;
import id.co.lcs.pos.adapter.DialogPaymentAdapter;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityPaymentBinding;
import id.co.lcs.pos.models.Invoice;
import id.co.lcs.pos.models.Payment;
import id.co.lcs.pos.models.WSMessage;
import id.co.lcs.pos.printer.Communication;
import id.co.lcs.pos.printer.ILocalizeReceipts;
import id.co.lcs.pos.printer.ModelCapability;
import id.co.lcs.pos.printer.PrinterFunctions;
import id.co.lcs.pos.printer.PrinterSettingConstant;
import id.co.lcs.pos.printer.PrinterSettingManager;
import id.co.lcs.pos.printer.PrinterSettings;
import id.co.lcs.pos.utils.Helper;
import id.co.lcs.pos.utils.Utils;

public class PaymentActivity extends BaseActivity {
    private ActivityPaymentBinding binding;
    private Payment payment = new Payment();
    public int PARAM = 0;
    private double ccAmount = 0;
    private double cash = 0;
    private double netsAmount = 0;
    private double check = 0;
    private double change = 0;
    private double amount = 0;
    private double totAmount = 0;
    private boolean pCreditCard = true, pNets = false, pCash = false, pCheck = false;
    private Calendar myCalendar = Calendar.getInstance();
    private String docNumber, msg;
    private Invoice invoice = new Invoice();
    private File storageDir;
    private int openInv = 0;

    private static final int BLUETOOTH_REQUEST_CODE = 1000;
    private int mPrinterSettingIndex = 0;
    PrinterSettingManager settingManager = null;
    PrinterSettings settings = null;
    private List<PortInfo> mPortList;
    public static Dialog dialog;
    private int       mModelIndex;
    private String    mPortName;
    private String    mPortSettings;
    private String    mMacAddress;
    private String    mModelName;
    private Boolean   mDrawerOpenStatus;
    private int       mPaperSize;
    private Button btnPrint;
    private ProgressDialog mProgressDialog;
    // Static CONSTANT VALUE
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
    };
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        if (Build.VERSION_CODES.S <= SDK_INT) {
            requestBluetoothPermission();
        }
        verifyStoragePermission(this);
        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Communicating...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        payment = (Payment) Helper.getItemParam(Constants.PAYMENT);
        totAmount = Double.parseDouble(String.format("%.2f", payment.getDocTotal()));
        binding.edtTotAmount.setText("S$ " + totAmount);
        binding.ccRefNumber.requestFocus();

        binding.imgPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTask searchTask = new SearchTask();
                searchTask.execute("BT:");
            }
        });

        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtAmount.getText().toString().isEmpty()) {
                    setToast("Amount cannot empty");
                } else {
                    double amount = Double.parseDouble(binding.edtAmount.getText().toString().split("\\s+")[1]);
                    String errorMessage = null;
                    if (ccAmount != 0 && binding.ccRefNumber.getText().toString().isEmpty()) {
                        errorMessage = "Credit card Ref No. cannot empty";
                    } else if (ccAmount != 0 && binding.cardNumber.getText().toString().isEmpty()) {
                        errorMessage = "Credit card number cannot empty";
                    } else if (ccAmount != 0 && binding.ccMonth.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid month cannot empty";
                    } else if (ccAmount != 0 && binding.ccYear.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid year cannot empty";
                    } else if (netsAmount != 0 && binding.edtNetsRef.getText().toString().trim().isEmpty()) {
                        errorMessage = "Nets Ref No. cannot empty";
//                    }  else if (check != 0 && binding.edtCheckRef.getText().toString().trim().isEmpty()) {
//                        errorMessage = "Check No. cannot empty";
//                    } else if (check != 0 && binding.edtCheckAcc.getText().toString().trim().isEmpty()) {
//                        errorMessage = "Check Account cannot empty";
//                    } else if (check != 0 && binding.edtCheckBank.getText().toString().trim().isEmpty()) {
//                        errorMessage = "Check Bank cannot empty";
                    } else if (check != 0 && binding.edtRef.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Country cannot empty";
                    } else if (check != 0 && binding.edtCheckDate.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Date cannot empty";
                    } else if (amount < totAmount) {
                        errorMessage = "Amount not enough";
                    } else {
                        if (!binding.ccAmount.getText().toString().isEmpty()) {
                            ccAmount = Double.parseDouble(binding.ccAmount.getText().toString().split("\\s+")[1]);
                        }
                        if (!binding.edtNetsAmount.getText().toString().isEmpty()) {
                            netsAmount = Double.parseDouble(binding.edtNetsAmount.getText().toString().split("\\s+")[1]);
                        }
                        if (!binding.edtCash.getText().toString().isEmpty()) {
                            cash = Double.parseDouble(binding.edtCash.getText().toString().split("\\s+")[1]);
                        }
                        if (!binding.edtCheckAmount.getText().toString().isEmpty()) {
                            check = Double.parseDouble(binding.edtCheckAmount.getText().toString().split("\\s+")[1]);
                        }
                        new AlertDialog.Builder(PaymentActivity.this)
                                .setMessage("Are you sure proceed to receipt?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (ccAmount != 0) {
                                            payment.setTransNo(binding.ccRefNumber.getText().toString());
                                            payment.setCardNumber(binding.cardNumber.getText().toString());
                                            payment.setcValidMonth(binding.ccMonth.getText().toString());
                                            payment.setcValidYear(binding.ccYear.getText().toString());
                                            payment.setCardAmount(ccAmount - change);
                                        } else {
                                            payment.setTransNo("");
                                            payment.setCardNumber("");
                                            payment.setcValidMonth("0");
                                            payment.setcValidYear("0");
                                            payment.setCardAmount(0);
                                        }
                                        if (netsAmount != 0) {
                                            payment.setTransNetsNo(binding.edtNetsRef.getText().toString());
                                            payment.setNetsAmount(netsAmount - change);
                                        } else {
                                            payment.setTransNetsNo("");
                                            payment.setNetsAmount(0);
                                        }
                                        if (cash != 0) {
                                            payment.setCash(cash - change);
                                        } else {
                                            payment.setCash(0);
                                        }
                                        if (check != 0) {
                                            payment.setCheckNo(Integer.parseInt(binding.edtCheckRef.getText().toString()));
                                            payment.setCheckAccount(binding.edtCheckAcc.getText().toString());
                                            payment.setCheckBank(binding.edtCheckBank.getText().toString());
                                            payment.setCheckCountry(binding.edtRef.getText().toString());
                                            payment.setCheckAmount(check - change);
                                            payment.setCheckDate(binding.edtCheckDate.getText().toString());
                                        } else {
                                            payment.setCheckNo(0);
                                            payment.setCheckAccount("");
                                            payment.setCheckBank("");
                                            payment.setCheckCountry("");
                                            payment.setCheckAmount(0);
                                            payment.setCheckDate("");
                                        }
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
                    }
                    if(errorMessage!= null) {
                        setToast(errorMessage);
                    }

                }
            }
        });
        binding.btnCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ccRefNumber.requestFocus();
                pCreditCard = true;
                pNets = false;
                pCash = false;
                pCheck = false;
                binding.lPCreditCard.setVisibility(View.VISIBLE);
                binding.lPNets.setVisibility(View.GONE);
                binding.lPCash.setVisibility(View.GONE);
                binding.lPCheck.setVisibility(View.GONE);
            }
        });
        binding.btnNets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtNetsRef.requestFocus();
                pCreditCard = false;
                pNets = true;
                pCash = false;
                pCheck = false;
                binding.lPCreditCard.setVisibility(View.GONE);
                binding.lPNets.setVisibility(View.VISIBLE);
                binding.lPCash.setVisibility(View.GONE);
                binding.lPCheck.setVisibility(View.GONE);
            }
        });
        binding.btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtCash.requestFocus();
                pCreditCard = false;
                pNets = false;
                pCash = true;
                pCheck = false;
                binding.lPCreditCard.setVisibility(View.GONE);
                binding.lPNets.setVisibility(View.GONE);
                binding.lPCash.setVisibility(View.VISIBLE);
                binding.lPCheck.setVisibility(View.GONE);
            }
        });
        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtCheckRef.requestFocus();
                pCreditCard = false;
                pNets = false;
                pCash = false;
                pCheck = true;
                binding.lPCreditCard.setVisibility(View.GONE);
                binding.lPNets.setVisibility(View.GONE);
                binding.lPCash.setVisibility(View.GONE);
                binding.lPCheck.setVisibility(View.VISIBLE);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        binding.edtCheckDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(PaymentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
//        binding.edtAmount.addTextChangedListener(new TextWatcher() {
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
//                if (!editable.toString().isEmpty()) {
//                    change = 0;
//                    String[] amount = binding.edtAmount.getText().toString().split("\\s+");
//                    if (amount.length == 1) {
//                        change = payment.getDocTotal() - Double.parseDouble(amount[0]);
//                    } else {
//                        change = payment.getDocTotal() - Double.parseDouble(amount[1]);
//                    }
//                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
//                }
//            }
//        });

        binding.cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
//                    binding.ccAmount.setEnabled(true);
//                    binding.ccMonth.setEnabled(true);
                } else {
//                    binding.ccAmount.setEnabled(false);
                    binding.ccAmount.setText("");
//                    binding.ccMonth.setEnabled(false);
                    binding.ccMonth.setText("");
                }
            }
        });
        binding.ccMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    if (Integer.parseInt(editable.toString()) > 12) {
                        binding.ccMonth.setText("");
                        setToast("Please input correct month");
                    } else {
//                        binding.ccYear.setEnabled(true);
                        binding.ccYear.setText("");
                    }
                } else {
//                    binding.ccYear.setEnabled(false);
                    binding.ccYear.setText("");
                }
            }
        });
        binding.ccYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() && editable.toString().length() == 2) {
                    DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                    int formattedDate = Integer.parseInt(df.format(Calendar.getInstance().getTime()));
                    if (Integer.parseInt(editable.toString()) < formattedDate) {
                        binding.ccYear.setText("");
                        setToast("Please input correct year");
                    }
                }
            }
        });
//        binding.rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == binding.rdDP.getId()){
//                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
//                }else{
//                    if (!binding.edtAmount.toString().isEmpty()) {
//                        double change = 0;
//                        String[] amount = binding.edtAmount.getText().toString().split("\\s+");
//                        if (amount.length == 1) {
//                            change = payment.getDocTotal() - Double.parseDouble(amount[0]);
//                        } else {
//                            change = payment.getDocTotal() - Double.parseDouble(amount[1]);
//                        }
//                        binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
//                    }
//                }
//            }
//        });
        binding.btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numEdtCCAmount = binding.ccAmount.getText().toString();
                String numEdtCash = binding.edtCash.getText().toString();
                String numEdtNetsAmount = binding.edtNetsAmount.getText().toString();
                String numEdtCheckAmount = binding.edtCheckAmount.getText().toString();
                if (binding.ccAmount.hasFocus()) {
                    if (!binding.ccAmount.getText().toString().trim().isEmpty() && !numEdtCCAmount.contains(".")) {
                        numEdtCCAmount += ".";
                        binding.ccAmount.setText(numEdtCCAmount);
                    }
                    int pos = binding.ccAmount.getText().length();
                    binding.ccAmount.setSelection(pos);
                    if (binding.ccAmount.getText().toString().isEmpty()) {
                        ccAmount = 0;
                    } else {
                        ccAmount = Double.parseDouble(binding.ccAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtNetsAmount.hasFocus()) {
                    if (!binding.edtNetsAmount.getText().toString().trim().isEmpty() && !numEdtCCAmount.contains(".")) {
                        numEdtNetsAmount += ".";
                        binding.edtNetsAmount.setText(numEdtNetsAmount);
                    }
                    int pos = binding.edtNetsAmount.getText().length();
                    binding.edtNetsAmount.setSelection(pos);
                    if (binding.edtNetsAmount.getText().toString().isEmpty()) {
                        netsAmount = 0;
                    } else {
                        netsAmount = Double.parseDouble(binding.edtNetsAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtCash.hasFocus()) {
                    if (!binding.edtCash.getText().toString().trim().isEmpty() && !numEdtCash.contains(".")) {
                        numEdtCash += ".";
                        binding.edtCash.setText(numEdtCash);
                    }
                    int pos = binding.edtCash.getText().length();
                    binding.edtCash.setSelection(pos);
                    if (binding.edtCash.getText().toString().isEmpty()) {
                        cash = 0;
                    } else {
                        cash = Double.parseDouble(binding.edtCash.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtCheckAmount.hasFocus()) {
                    if (!binding.edtCheckAmount.getText().toString().trim().isEmpty() && !numEdtCash.contains(".")) {
                        numEdtCheckAmount += ".";
                        binding.edtCheckAmount.setText(numEdtCheckAmount);
                    }
                    int pos = binding.edtCheckAmount.getText().length();
                    binding.edtCheckAmount.setSelection(pos);
                    if (binding.edtCheckAmount.getText().toString().isEmpty()) {
                        check = 0;
                    } else {
                        check = Double.parseDouble(binding.edtCheckAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                }
            }
        });
        binding.customNumberPad.setNumberPadClickListener(new OnNumPadClickListener() {
            @Override
            public void onPadClicked(NumPadButton button) {
                String numEdtCC = binding.ccRefNumber.getText().toString();
                String numEdtCardNumber = binding.cardNumber.getText().toString();
                String numEdtCCMonth = binding.ccMonth.getText().toString();
                String numEdtCCYear = binding.ccYear.getText().toString();
                String numEdtCCAmount = binding.ccAmount.getText().toString();
                String numEdtNetsNo = binding.edtNetsRef.getText().toString();
                String numEdtNetsAmount = binding.edtNetsAmount.getText().toString();
                String numEdtCash = binding.edtCash.getText().toString();
                String numEdtCheckAmount = binding.edtCheckAmount.getText().toString();
                String numEdtCheckNo = binding.edtCheckRef.getText().toString();
                String numEdtCheckAcc = binding.edtCheckAcc.getText().toString();
                if (binding.ccRefNumber.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.ccRefNumber.getText().toString().isEmpty()) {
                            String text = binding.ccRefNumber.getText().toString();
                            binding.ccRefNumber.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.cardNumber.requestFocus();
                    } else {
                        numEdtCC += button.ordinal();
                        binding.ccRefNumber.setText(numEdtCC);
                    }
                    int pos = binding.ccRefNumber.getText().length();
                    binding.ccRefNumber.setSelection(pos);
                } else if (binding.cardNumber.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.cardNumber.getText().toString().isEmpty()) {
                            String text = binding.cardNumber.getText().toString();
                            binding.cardNumber.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.ccMonth.requestFocus();
                    } else {
                        numEdtCardNumber += button.ordinal();
                        binding.cardNumber.setText(numEdtCardNumber);
                    }
                    int pos = binding.cardNumber.getText().length();
                    binding.cardNumber.setSelection(pos);
                } else if (binding.ccMonth.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.ccMonth.getText().toString().isEmpty()) {
                            String text = binding.ccMonth.getText().toString();
                            binding.ccMonth.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.ccYear.requestFocus();
                    } else {
                        numEdtCCMonth += button.ordinal();
                        binding.ccMonth.setText(numEdtCCMonth);
                    }
                    int pos = binding.ccMonth.getText().length();
                    binding.ccMonth.setSelection(pos);
                } else if (binding.ccYear.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.ccYear.getText().toString().isEmpty()) {
                            String text = binding.ccYear.getText().toString();
                            binding.ccYear.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.ccAmount.requestFocus();
                    } else {
                        numEdtCCYear += button.ordinal();
                        binding.ccYear.setText(numEdtCCYear);
                    }
                    int pos = binding.ccYear.getText().length();
                    binding.ccYear.setSelection(pos);
                } else if (binding.ccAmount.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.ccAmount.getText().toString().isEmpty()) {
                            String text = binding.ccAmount.getText().toString();
                            if (text.substring(0, text.length() - 1).equals("S$ ")) {
                                binding.ccAmount.setText("");
                            } else {
                                binding.ccAmount.setText(text.substring(0, text.length() - 1));
                            }
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtCash.requestFocus();
                    } else {
                        if (binding.ccAmount.getText().toString().trim().isEmpty()) {
                            numEdtCCAmount = "S$ " + button.ordinal();
                            binding.ccAmount.setText(numEdtCCAmount);
                        } else {
                            numEdtCCAmount += button.ordinal();
                            binding.ccAmount.setText(numEdtCCAmount);
                        }
                    }
                    int pos = binding.ccAmount.getText().length();
                    binding.ccAmount.setSelection(pos);
                    if (binding.ccAmount.getText().toString().isEmpty()) {
                        ccAmount = 0;
                    } else {
                        ccAmount = Double.parseDouble(binding.ccAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtNetsRef.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtNetsRef.getText().toString().isEmpty()) {
                            String text = binding.edtNetsRef.getText().toString();
                            binding.edtNetsRef.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtNetsAmount.requestFocus();
                    } else {
                        numEdtNetsNo += button.ordinal();
                        binding.edtNetsRef.setText(numEdtNetsNo);
                    }
                    int pos = binding.edtNetsRef.getText().length();
                    binding.edtNetsRef.setSelection(pos);
                } else if (binding.edtNetsAmount.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtNetsAmount.getText().toString().isEmpty()) {
                            String text = binding.edtNetsAmount.getText().toString();
                            if (text.substring(0, text.length() - 1).equals("S$ ")) {
                                binding.edtNetsAmount.setText("");
                            } else {
                                binding.edtNetsAmount.setText(text.substring(0, text.length() - 1));
                            }
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtCash.requestFocus();
                    } else {
                        if (binding.edtNetsAmount.getText().toString().trim().isEmpty()) {
                            numEdtNetsAmount = "S$ " + button.ordinal();
                            binding.edtNetsAmount.setText(numEdtNetsAmount);
                        } else {
                            numEdtNetsAmount += button.ordinal();
                            binding.edtNetsAmount.setText(numEdtNetsAmount);
                        }
                    }
                    int pos = binding.edtNetsAmount.getText().length();
                    binding.edtNetsAmount.setSelection(pos);
                    if (binding.edtNetsAmount.getText().toString().isEmpty()) {
                        netsAmount = 0;
                    } else {
                        netsAmount = Double.parseDouble(binding.edtNetsAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtCash.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtCash.getText().toString().isEmpty()) {
                            String text = binding.edtCash.getText().toString();
                            if (text.substring(0, text.length() - 1).equals("S$ ")) {
                                binding.edtCash.setText("");
                            } else {
                                binding.edtCash.setText(text.substring(0, text.length() - 1));
                            }
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtTotAmount.requestFocus();
                    } else {
                        if (binding.edtCash.getText().toString().trim().isEmpty()) {
                            numEdtCash = "S$ " + button.ordinal();
                            binding.edtCash.setText(numEdtCash);
                        } else {
                            numEdtCash += button.ordinal();
                            binding.edtCash.setText(numEdtCash);
                        }
                    }
                    int pos = binding.edtCash.getText().length();
                    binding.edtCash.setSelection(pos);
                    if (binding.edtCash.getText().toString().isEmpty()) {
                        cash = 0;
                    } else {
                        cash = Double.parseDouble(binding.edtCash.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else if (binding.edtCheckRef.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtCheckRef.getText().toString().isEmpty()) {
                            String text = binding.edtCheckRef.getText().toString();
                            binding.edtCheckRef.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtCheckAcc.requestFocus();
                    } else {
                        numEdtCheckNo += button.ordinal();
                        binding.edtCheckRef.setText(numEdtCheckNo);
                    }
                    int pos = binding.edtCheckRef.getText().length();
                    binding.edtCheckRef.setSelection(pos);
                } else if (binding.edtCheckAcc.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtCheckAcc.getText().toString().isEmpty()) {
                            String text = binding.edtCheckAcc.getText().toString();
                            binding.edtCheckAcc.setText(text.substring(0, text.length() - 1));
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtCheckBank.requestFocus();
                    } else {
                        numEdtCheckAcc += button.ordinal();
                        binding.edtCheckAcc.setText(numEdtCheckAcc);
                    }
                    int pos = binding.edtCheckAcc.getText().length();
                    binding.edtCheckAcc.setSelection(pos);
                } else if (binding.edtCheckAmount.hasFocus()) {
                    if (button.ordinal() == 10) {
                        if (!binding.edtCheckAmount.getText().toString().isEmpty()) {
                            String text = binding.edtCheckAmount.getText().toString();
                            if (text.substring(0, text.length() - 1).equals("S$ ")) {
                                binding.edtCheckAmount.setText("");
                            } else {
                                binding.edtCheckAmount.setText(text.substring(0, text.length() - 1));
                            }
                        }
                    } else if (button.ordinal() == 11) {
                        binding.edtTotAmount.requestFocus();
                    } else {
                        if (binding.edtCheckAmount.getText().toString().trim().isEmpty()) {
                            numEdtCheckAmount = "S$ " + button.ordinal();
                            binding.edtCheckAmount.setText(numEdtCheckAmount);
                        } else {
                            numEdtCheckAmount += button.ordinal();
                            binding.edtCheckAmount.setText(numEdtCheckAmount);
                        }
                    }
                    int pos = binding.edtCheckAmount.getText().length();
                    binding.edtCheckAmount.setSelection(pos);
                    if (binding.edtCheckAmount.getText().toString().isEmpty()) {
                        check = 0;
                    } else {
                        check = Double.parseDouble(binding.edtCheckAmount.getText().toString().split("\\s+")[1]);
                    }
                    amount = ccAmount + cash + netsAmount + check;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                }
            }
        });

//        binding.imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

//        binding.ccRefNumber.addTextChangedListener(new TextWatcher() {
//
//            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
//            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
//            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
//            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
//            private static final char DIVIDER = '-';
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // noop
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // noop
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
//                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
//                }
//            }
//
//            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
//                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
//                for (int i = 0; i < s.length(); i++) { // check that every element is right
//                    if (i > 0 && (i + 1) % dividerModulo == 0) {
//                        isCorrect &= divider == s.charAt(i);
//                    } else {
//                        isCorrect &= Character.isDigit(s.charAt(i));
//                    }
//                }
//                return isCorrect;
//            }
//
//            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
//                final StringBuilder formatted = new StringBuilder();
//
//                for (int i = 0; i < digits.length; i++) {
//                    if (digits[i] != 0) {
//                        formatted.append(digits[i]);
//                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
//                            formatted.append(divider);
//                        }
//                    }
//                }
//
//                return formatted.toString();
//            }
//
//            private char[] getDigitArray(final Editable s, final int size) {
//                char[] digits = new char[size];
//                int index = 0;
//                for (int i = 0; i < s.length() && index < size; i++) {
//                    char current = s.charAt(i);
//                    if (Character.isDigit(current)) {
//                        digits[index] = current;
//                        index++;
//                    }
//                }
//                return digits;
//            }
//        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.edtCheckDate.setText(sdf.format(myCalendar.getTime()));
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String url = "";
                    if (!(Boolean) Helper.getItemParam(Constants.SO_FLAG)) {
                        String URL_POST_ARINVOICE = Constants.API_PREFIX + Constants.API_POST_ARINVOICE;

                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_POST_ARINVOICE);
                    } else {
                        String URL_POST_SO_ARINVOICE = Constants.API_PREFIX + Constants.API_POST_SO_ARINVOICE;

                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_POST_SO_ARINVOICE);
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(payment);
                    return (WSMessage) Helper.postWebservice(url, payment, WSMessage.class);
                } else {
                    String URL_GET_RECEIPT = Constants.API_PREFIX + Constants.API_GET_RECEIPT;

                    String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GET_RECEIPT).concat("\\").concat(docNumber);
                    invoice = (Invoice) Helper.getWebserviceWithoutHeaders(url, Invoice.class);
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
        protected void onPostExecute(WSMessage message) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if (message != null && message.getType().equals("S")) {
                    docNumber = message.getDocNo();
                    msg = message.getMessage();
                    PARAM = 1;
                    new RequestUrl().execute();
                    getProgressDialog().show();
                } else {
                    if (message != null && message.getType().equals("E")) {
                        new AlertDialog.Builder(PaymentActivity.this)
                                .setMessage(message.getMessage())
                                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        setToast(Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString());
                    }
                }
            } else {
                if(invoice != null && invoice.getInvoice() != null) {
                    if(!invoice.getInvoice().isEmpty()) {
                        printPDF();
                    }else{
                        setToast("No Invoice was found");
                    }
                }else{
                    setToast("No Invoice was found");
                }
//                new AlertDialog.Builder(PaymentActivity.this)
//                        .setMessage(msg + ", " + "DocNo : " + docNumber)
//                        .setCancelable(false)
//                        .setPositiveButton("Print Invoice", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                getProgressDialog().dismiss();
//                                printPDF();
//                            }
//                        })
//                        .setNegativeButton("Done", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .show();
            }
        }

    }

    private void printPDF() {
        FileOutputStream fos = null;
        try {
            if (invoice.getInvoice() != null) {
//                fos = new FileOutputStream(new File(storageDir.getAbsolutePath() + "/" +  docNumber + ".pdf"));
                fos = openFileOutput(docNumber + ".pdf", Context.MODE_PRIVATE);
                byte[] decodedString = android.util.Base64.decode(invoice.getInvoice(), android.util.Base64.DEFAULT);
                fos.write(decodedString);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {

        } finally {
            if (fos != null) {
                fos = null;
            }
        }
        openInv = 1;
        File pdfFile = new File("/data/data/" + getPackageName() + "/files/" + docNumber + ".pdf");
        print(pdfFile);
//        Uri path = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
//                BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);
//        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
//        try {
//            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getApplicationContext(), pdfFile.getAbsolutePath());
//            printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
//
//        } catch (Exception e) {
//            Log.e(getClass().getSimpleName(), "Exception printing PDF", e);
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setDataAndType(path, "application/pdf");
//        try {
//            openInv = 1;
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(PaymentActivity.this,
//                    "No Application Available to View PDF",
//                    Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openInv == 1) {
            new AlertDialog.Builder(PaymentActivity.this)
                    .setMessage(msg + ", " + "DocNo : " + docNumber)
                    .setCancelable(false)
                    .setPositiveButton("Reprint Invoice", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getProgressDialog().dismiss();
                            printPDF();
                        }
                    })
                    .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    })
                    .show();
        }
    }

    @RequiresApi(31)
    private void requestBluetoothPermission() {
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                    },
                    BLUETOOTH_REQUEST_CODE
            );
        }
    }

    //Permissions Check
    public void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        // Surrounded with if statement for Android R to get access of complete file.
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager() && permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSION_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);

                // Abruptly we will ask for permission once the application is launched for sake demo.
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private class SearchTask extends AsyncTask<String, Void, Void> {

        SearchTask() {
            super();
        }

        @Override
        protected Void doInBackground(String... interfaceType) {
            try {
                mPortList = StarIOPort.searchPrinter(interfaceType[0], getApplicationContext());
            } catch (StarIOPortException | SecurityException e) {
                mPortList = new ArrayList<>();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void doNotUse) {
//            for (PortInfo info : mPortList) {
            showDialogArticle();
//                addItem(info);
//            }
        }
    }

    public void showDialogArticle() {
        dialog = new Dialog(PaymentActivity.this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_article);

        TextView btndialog = dialog.findViewById(R.id.btnCancel);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.rcArticle);
        DialogPaymentAdapter adapter = new DialogPaymentAdapter(PaymentActivity.this, mPortList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        dialog.show();
    }

    public void setDataFromDialog(PortInfo listInfo) {
        for(PortInfo info : mPortList){
            if(info.getModelName().equals(listInfo.getModelName())
                    && info.getPortName().equals(listInfo.getPortName())
                    && info.getMacAddress().equals(listInfo.getMacAddress())){
                mModelName = info.getPortName().substring(PrinterSettingConstant.IF_TYPE_BLUETOOTH.length());
                mPortName = "BT:" + info.getMacAddress();
                mMacAddress = info.getMacAddress();
                mPaperSize = 576;
                mModelIndex = 21;
                mPortSettings = "";
                mDrawerOpenStatus = true;
                break;
            }
        }
        registerPrinter();
    }

    public void registerPrinter() {
        PrinterSettingManager settingManager = new PrinterSettingManager(getApplicationContext());

        settingManager.storePrinterSettings(
                mPrinterSettingIndex,
                new PrinterSettings(mModelIndex, mPortName, mPortSettings, mMacAddress, mModelName, mDrawerOpenStatus, mPaperSize)
        );
    }

    private void print(File file){
        mProgressDialog.show();


        PrinterSettingManager settingManager = new PrinterSettingManager(this);
        PrinterSettings       settings       = settingManager.getPrinterSettings();

        StarIoExt.Emulation emulation = ModelCapability.getEmulation(settings.getModelIndex());
        int paperSize = settings.getPaperSize();
        ILocalizeReceipts localizeReceipts = ILocalizeReceipts.createLocalizeReceipts(0, paperSize);

//        File file = new File(this.getFilesDir() + "/document.pdf");
        byte[] commands;
        mBitmap = Utils.pdfToBitmap(file, this);
        if (mBitmap != null) {
            commands = PrinterFunctions.createRasterData(emulation, mBitmap, paperSize, true);
        }
        else {
            commands = new byte[0];
        }
        Communication.sendCommands(this, commands, settings.getPortName(), settings.getPortSettings()
                , 10000, 30000, getApplicationContext(), mCallback);

    }

    private final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(Communication.CommunicationResult communicationResult) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                if (openInv == 1) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setMessage(msg + ", " + "DocNo : " + docNumber)
                            .setCancelable(false)
                            .setPositiveButton("Reprint Invoice", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getProgressDialog().dismiss();
                                    printPDF();
                                }
                            })
                            .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            })
                            .show();
                }
            }
        }
    };
}