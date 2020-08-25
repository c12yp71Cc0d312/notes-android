package com.example.onsitetask3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class DrawNoteAdapter extends RecyclerView.Adapter<DrawNoteAdapter.DrawNoteViewHolder> {

    private static final String TAG = "DrawNoteAdapter";

    private ArrayList<DrawNote> drawNotes;

    public static class DrawNoteViewHolder extends RecyclerView.ViewHolder {

        public ImageView drawing;
        public TextView title;

        public DrawNoteViewHolder(@NonNull View itemView) {
            super(itemView);

            drawing = itemView.findViewById(R.id.imageViewDraw);
            title = itemView.findViewById(R.id.textView_title_draw);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    public DrawNoteAdapter(ArrayList<DrawNote> drawNotes) {
        this.drawNotes = drawNotes;
    }

    @NonNull
    @Override
    public DrawNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.draw_note_item, parent, false);
        return new DrawNoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawNoteViewHolder holder, int position) {
        DrawNote currentDrawing = drawNotes.get(position);
        holder.title.setText(currentDrawing.getDrawTitle());
        File imagePath = new File(currentDrawing.getImagePath());
        Picasso.get().load(imagePath).into(holder.drawing);
    }

    @Override
    public int getItemCount() {
        if(drawNotes == null) {
            return 0;
        }
        Log.d(TAG, "getItemCount: " + drawNotes.size());
        return drawNotes.size();
    }


}
