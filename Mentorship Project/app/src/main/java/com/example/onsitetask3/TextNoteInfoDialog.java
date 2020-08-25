package com.example.onsitetask3;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.Timestamp;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TextNoteInfoDialog extends AppCompatDialogFragment {

    private static final String TAG = "TextNoteInfoDialog";

    Timestamp timestamp;
    String createdDate;

    public TextNoteInfoDialog(Timestamp timestamp) {
        this.timestamp = timestamp;
        long seconds = timestamp.getSeconds()*1000;
        Date date = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        createdDate = sdf.format(date);
        Log.d(TAG, "TextNoteInfoDialog: date: " + date);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Info")
                .setMessage("Created on: " + createdDate)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
