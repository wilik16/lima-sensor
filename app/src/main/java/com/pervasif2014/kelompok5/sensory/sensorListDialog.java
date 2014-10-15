package com.pervasif2014.kelompok5.sensory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class sensorListDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.getArguments().getString("data"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().getAttributes();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(15);

        return alert;
    }
}
