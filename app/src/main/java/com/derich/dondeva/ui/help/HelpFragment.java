package com.derich.dondeva.ui.help;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.derich.dondeva.R;

import java.util.Locale;

public class HelpFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        final TextView tvHelpContact = root.findViewById(R.id.edtHelpContact);
        final TextView tvLocation = root.findViewById(R.id.edtHelpLocation);
        final TextView tvInsta = root.findViewById(R.id.edtHelpSocial);
        final TextView tvEmail = root.findViewById(R.id.edtHelpEmail);
        final TextView tvSnap = root.findViewById(R.id.edtHelpSnapchat);
        final TextView tvOpen = root.findViewById(R.id.edtHelpOpen);
        String phone=PhoneNumberUtils.formatNumber(getString(R.string.contact_us),Locale.getDefault().getCountry());
        tvHelpContact.setText(phone);
        tvLocation.setText(R.string.help_location);
        tvInsta.setText(R.string.help_insta);
        tvEmail.setText(R.string.help_email);
        tvSnap.setText(R.string.help_snap);
        tvOpen.setText(R.string.open_days);
        return root;
    }
}