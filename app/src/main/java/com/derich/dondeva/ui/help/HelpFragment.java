package com.derich.dondeva.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.derich.dondeva.R;

public class HelpFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        final TextView tvHelpContact = root.findViewById(R.id.edtHelpContact);
        final TextView tvLocation = root.findViewById(R.id.edtHelpLocation);
        final TextView tvSocial = root.findViewById(R.id.edtHelpSocial);
        final TextView tvOpen = root.findViewById(R.id.edtHelpOpen);
        tvHelpContact.setText(R.string.help_fragment);
        tvLocation.setText(R.string.help_fragment);
        tvSocial.setText(R.string.help_fragment);
        tvOpen.setText(R.string.open_days);
        return root;
    }
}