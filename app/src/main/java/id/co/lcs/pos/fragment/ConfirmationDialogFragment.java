package id.co.lcs.pos.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Samuel Gunawan on 10/12/2017.
 */

public class ConfirmationDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_PERMISSIONS = "permissions";
    private static final String ARG_REQUEST_CODE = "request_code";
    private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";
    private static Activity mActivity;

    public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                                                       String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE, message);
        args.putStringArray(ARG_PERMISSIONS, permissions);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(args.getInt(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                if (permissions == null) {
                                    throw new IllegalArgumentException();
                                }
                                ActivityCompat.requestPermissions(getActivity(),
                                        permissions, args.getInt(ARG_REQUEST_CODE));
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),
                                        args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                        Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        })
                .setCancelable(false)
                .create();
        alertDialog
                .setCanceledOnTouchOutside(false);
        return alertDialog;
    }

}