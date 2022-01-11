package id.co.lcs.pos.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cat.xojan.numpad.NumPadButton;
import cat.xojan.numpad.OnNumPadClickListener;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityDownPaymentBinding;
import id.co.lcs.pos.databinding.ActivityPaymentBinding;
import id.co.lcs.pos.models.Payment;
import id.co.lcs.pos.models.WSMessage;
import id.co.lcs.pos.utils.Helper;

public class DownPaymentActivity extends BaseActivity {
    private ActivityDownPaymentBinding binding;
    private Payment payment = new Payment();
    public int PARAM = 0;
    private double ccAmount = 0;
    private double netsAmount = 0;
    private double cash = 0;
    private double check = 0;
    private boolean pCreditCard = true, pNets=false, pCash = false, pCheck=false;
    private double dpAmount = 0;
    private double totAmount = 0;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownPaymentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        payment = (Payment) Helper.getItemParam(Constants.PAYMENT);
        totAmount = Double.parseDouble(String.format("%.2f", payment.getDocTotal()));
        binding.edtTotAmount.setText("S$ " + String.format("%.2f", payment.getDocTotal()));
        binding.ccRefNumber.requestFocus();
//        binding.edtAmount.setText("S$ " + String.format("%.2f", payment.getDocTotal()));
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
                    }else if (ccAmount != 0 && binding.cardNumber.getText().toString().isEmpty()) {
                        errorMessage = "Credit card number cannot empty";
                    } else if (ccAmount != 0 && binding.ccMonth.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid month cannot empty";
                    } else if (ccAmount != 0 && binding.ccYear.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid year cannot empty";
                    } else if (netsAmount != 0 && binding.edtNetsRef.getText().toString().trim().isEmpty()) {
                        errorMessage = "Nets Ref No. cannot empty";
                    }  else if (check != 0 && binding.edtCheckRef.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check No. cannot empty";
                    } else if (check != 0 && binding.edtCheckAcc.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Account cannot empty";
                    } else if (check != 0 && binding.edtCheckBank.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Bank cannot empty";
                    } else if (check != 0 && binding.edtCheckCountry.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Country cannot empty";
                    } else if (check != 0 && binding.edtCheckDate.getText().toString().trim().isEmpty()) {
                        errorMessage = "Check Date cannot empty";
                    }
//                    else if (amount < totAmount) {
//                        errorMessage = "Amount not enough";
//                    }
                    else {
                        if (!binding.ccAmount.getText().toString().isEmpty()) {
                            ccAmount = Double.parseDouble(binding.ccAmount.getText().toString().split("\\s+")[1]);
                        }
                        if (!binding.edtNetsAmount.getText().toString().isEmpty()) {
                            netsAmount = Double.parseDouble(binding.edtNetsAmount.getText().toString().split("\\s+")[1]);
                        }
                        if (!binding.edtCash.getText().toString().isEmpty()) {
                            cash = Double.parseDouble(binding.edtCash.getText().toString().split("\\s+")[1]);
                        }
                        new AlertDialog.Builder(DownPaymentActivity.this)
                                .setMessage("Are you sure proceed to receipt?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        payment.setTransNo(binding.ccRefNumber.getText().toString());
//                                        payment.setCardNumber(binding.cardNumber.getText().toString());
//                                        payment.setcValidMonth(binding.ccMonth.getText().toString());
//                                        payment.setcValidYear(binding.ccYear.getText().toString());
//                                        payment.setCardAmount(ccAmount);
//                                        payment.setTransNetsNo(binding.edtNetsRef.getText().toString());
//                                        payment.setNetsAmount(netsAmount);
//                                        payment.setCash(cash);
//                                        payment.setDownPayment(ccAmount+netsAmount+cash);
//                                        if(ccAmount != 0) {
//                                            payment.setTransNo(binding.ccRefNumber.getText().toString());
//                                            payment.setCardNumber(binding.cardNumber.getText().toString());
//                                            payment.setcValidMonth(binding.ccMonth.getText().toString());
//                                            payment.setcValidYear(binding.ccYear.getText().toString());
//                                            payment.setCardAmount(ccAmount);
//                                        }
//                                        if(netsAmount != 0) {
//                                            payment.setTransNetsNo(binding.edtNetsRef.getText().toString());
//                                            payment.setNetsAmount(netsAmount);
//                                        }
//                                        if(cash != 0) {
//                                            payment.setCash(cash);
//                                        }
//                                        if(check!=0){
//                                            payment.setCheckNo(Integer.parseInt(binding.edtCheckRef.getText().toString()));
//                                            payment.setCheckAccount(binding.edtCheckAcc.getText().toString());
//                                            payment.setCheckBank(binding.edtCheckBank.getText().toString());
//                                            payment.setCheckCountry(binding.edtCheckCountry.getText().toString());
//                                            payment.setCheckAmount(check);
//                                            payment.setCheckDate(binding.edtCheckDate.getText().toString());
//                                        }

                                        if(ccAmount != 0) {
                                            payment.setTransNo(binding.ccRefNumber.getText().toString());
                                            payment.setCardNumber(binding.cardNumber.getText().toString());
                                            payment.setcValidMonth(binding.ccMonth.getText().toString());
                                            payment.setcValidYear(binding.ccYear.getText().toString());
                                            payment.setCardAmount(ccAmount);
                                        }
                                        else{
                                            payment.setTransNo("");
                                            payment.setCardNumber("");
                                            payment.setcValidMonth("0");
                                            payment.setcValidYear("0");
                                            payment.setCardAmount(0);
                                        }
                                        if(netsAmount != 0) {
                                            payment.setTransNetsNo(binding.edtNetsRef.getText().toString());
                                            payment.setNetsAmount(netsAmount);
                                        }
                                        else{
                                            payment.setTransNetsNo("");
                                            payment.setNetsAmount(0);
                                        }
                                        if(cash != 0) {
                                            payment.setCash(cash);
                                        }
                                        else{
                                            payment.setCash(0);
                                        }
                                        if(check!=0){
                                            payment.setCheckNo(Integer.parseInt(binding.edtCheckRef.getText().toString()));
                                            payment.setCheckAccount(binding.edtCheckAcc.getText().toString());
                                            payment.setCheckBank(binding.edtCheckBank.getText().toString());
                                            payment.setCheckCountry(binding.edtCheckCountry.getText().toString());
                                            payment.setCheckAmount(check);
                                            payment.setCheckDate(binding.edtCheckDate.getText().toString());
                                        }
                                        else{
                                            payment.setCheckNo(0);
                                            payment.setCheckAccount("");
                                            payment.setCheckBank("");
                                            payment.setCheckCountry("");
                                            payment.setCheckAmount(0);
                                            payment.setCheckDate("");
                                        }
                                        payment.setDownPayment(ccAmount+netsAmount+cash+check);
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
                    setToast(errorMessage);

                }
            }
        });

//        binding.imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
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
                    binding.ccAmount.setEnabled(true);
                    binding.ccMonth.setEnabled(true);
                } else {
                    binding.ccAmount.setEnabled(false);
                    binding.ccAmount.setText("");
                    binding.ccMonth.setEnabled(false);
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
                    if(Integer.parseInt(editable.toString()) > 12) {
                        binding.ccMonth.setText("");
                        setToast("Please input correct month");
                    }else{
//                        binding.ccYear.setEnabled(true);
                        binding.ccYear.setText("");
                    }
                }else{
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
                    if(Integer.parseInt(editable.toString()) < formattedDate) {
                        binding.ccYear.setText("");
                        setToast("Please input correct year");
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
                new DatePickerDialog(DownPaymentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                    dpAmount = ccAmount + cash + netsAmount + check;;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                    dpAmount = ccAmount + cash + netsAmount + check;;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
                }  else if (binding.edtCheckAmount.hasFocus()) {
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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
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
                }else if (binding.edtCheckAcc.hasFocus()) {
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
                    dpAmount = ccAmount + cash + netsAmount + check;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", dpAmount));
                }
            }
        });
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
                    if(!(Boolean) Helper.getItemParam(Constants.SO_FLAG)) {
                        String URL_POST_ARINVOICE = Constants.API_PREFIX + Constants.API_POST_ARINVOICE;

                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_POST_ARINVOICE);
                    }else{
                        String URL_POST_SO_ARINVOICE = Constants.API_PREFIX + Constants.API_POST_SO_ARINVOICE;

                        url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_POST_SO_ARINVOICE);
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(payment);
                    return (WSMessage) Helper.postWebservice(url, payment ,WSMessage.class);
                } else {
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
                    new AlertDialog.Builder(DownPaymentActivity.this)
                            .setMessage(message.getMessage() + ", " + "DocNo : " + message.getDocNo())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(DownPaymentActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            })
                            .show();
//                    setToast(message.getMessage());
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        setToast(Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString());
                    } else {
                        new AlertDialog.Builder(DownPaymentActivity.this)
                                .setMessage(message.getMessage())
                                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
//                        setToast(message.getMessage());
                    }
                }
            }else{
            }
        }

    }
}