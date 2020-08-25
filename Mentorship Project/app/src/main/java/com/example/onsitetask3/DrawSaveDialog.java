package com.example.onsitetask3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DrawSaveDialog extends AppCompatDialogFragment {

    private EditText editTextTitle;
    private DrawSaveDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.draw_save_dialog, null);

        editTextTitle = v.findViewById(R.id.editTextDrawTitle);

        builder.setView(v);
        builder.setTitle("Enter a title")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = editTextTitle.getText().toString();
                        if(title == null || title.trim().equals("")) {
                            Toast.makeText(getActivity(), "Enter a title", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            listener.applyTitle(title);
                        }
                    }
                });



        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DrawSaveDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement DrawSaveDialogListener!");
        }
    }

    public interface DrawSaveDialogListener {
        void applyTitle(String title);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
