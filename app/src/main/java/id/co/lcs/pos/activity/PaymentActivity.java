package id.co.lcs.pos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cat.xojan.numpad.NumPadButton;
import cat.xojan.numpad.OnNumPadClickListener;
import id.co.lcs.pos.R;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityPaymentBinding;
import id.co.lcs.pos.models.Customer;
import id.co.lcs.pos.models.Payment;
import id.co.lcs.pos.models.PaymentItem;
import id.co.lcs.pos.models.QuotationItemResponse;
import id.co.lcs.pos.models.QuotationResponse;
import id.co.lcs.pos.models.WSMessage;
import id.co.lcs.pos.utils.Helper;

public class PaymentActivity extends BaseActivity {
    private ActivityPaymentBinding binding;
    private Payment payment = new Payment();
    public int PARAM = 0;
    private double ccAmount = 0;
    private double cash = 0;
    private double netsAmount = 0;
    private double change = 0;
    private double amount = 0;
    private double totAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        payment = (Payment) Helper.getItemParam(Constants.PAYMENT);
        totAmount = Double.parseDouble(String.format("%.2f", payment.getDocTotal()));
        binding.edtTotAmount.setText("S$ " + totAmount);
        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtAmount.getText().toString().isEmpty()) {
                    setToast("Amount cannot empty");
                } else {
                    double amount = Double.parseDouble(binding.edtAmount.getText().toString().split("\\s+")[1]);
                    ccAmount = 0;
                    cash = 0;
                    String errorMessage = null;
                    if (!binding.ccRefNumber.getText().toString().trim().isEmpty() && binding.cardNumber.getText().toString().trim().isEmpty()) {
                        errorMessage = "Credit card detail cannot empty";
                    } else if (!binding.cardNumber.getText().toString().trim().isEmpty() && binding.ccMonth.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid month cannot empty";
                    } else if (!binding.cardNumber.getText().toString().trim().isEmpty() && binding.ccYear.getText().toString().isEmpty()) {
                        errorMessage = "Credit card valid year cannot empty";
                    } else if (!binding.cardNumber.getText().toString().trim().isEmpty() && binding.ccAmount.getText().toString().isEmpty()) {
                        errorMessage = "Credit card amount cannot empty";
                    } else if (!binding.cardNumber.getText().toString().trim().isEmpty() && binding.ccRefNumber.getText().toString().isEmpty()) {
                        errorMessage = "Credit card Ref No. cannot empty";
                    } else if (!binding.edtNetsRef.getText().toString().trim().isEmpty() && binding.edtNetsAmount.getText().toString().trim().isEmpty()) {
                        errorMessage = "Nets Amount cannot empty";
                    } else if (!binding.edtNetsAmount.getText().toString().trim().isEmpty() && binding.edtNetsRef.getText().toString().trim().isEmpty()) {
                        errorMessage = "Nets Ref No. cannot empty";
                    } else if (binding.cardNumber.getText().toString().trim().isEmpty() && binding.edtNetsAmount.getText().toString().trim().isEmpty() && binding.edtCash.getText().toString().isEmpty()) {
                        errorMessage = "Cash cannot empty";
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
                        new AlertDialog.Builder(PaymentActivity.this)
                                .setMessage("Are you sure proceed to receipt?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        payment.setTransNo(binding.ccRefNumber.getText().toString());
                                        payment.setCardNumber(binding.cardNumber.getText().toString());
                                        payment.setcValidMonth(binding.ccMonth.getText().toString());
                                        payment.setcValidYear(binding.ccYear.getText().toString());
                                        payment.setCardAmount(ccAmount);
                                        payment.setTransNetsNo(binding.edtNetsRef.getText().toString());
                                        payment.setNetsAmount(netsAmount);
                                        payment.setCash(cash);
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
                        binding.ccYear.setEnabled(true);
                        binding.ccYear.setText("");
                    }
                }else{
                    binding.ccYear.setEnabled(false);
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
                    amount = ccAmount + cash + netsAmount;
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
                    amount = ccAmount + cash + netsAmount;
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
                    amount = ccAmount + cash + netsAmount;
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
                    amount = ccAmount + cash + netsAmount;
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
                    amount = ccAmount + cash + netsAmount;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                } else {
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
                    amount = ccAmount + cash + netsAmount;
                    change = amount - totAmount;
                    binding.edtAmount.setText("S$ " + String.format("%.2f", amount));
                    binding.edtChangeGiven.setText("S$ " + String.format("%.2f", change));
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                    return (WSMessage) Helper.postWebservice(url, payment, WSMessage.class);
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
                    setToast(message.getMessage());
                    Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        setToast(Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString());
                    } else {
                        new AlertDialog.Builder(PaymentActivity.this)
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
            } else {
            }
        }

    }
}