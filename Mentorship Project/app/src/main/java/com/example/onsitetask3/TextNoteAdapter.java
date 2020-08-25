package com.example.onsitetask3;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TextNoteAdapter extends FirestoreRecyclerAdapter<TextNote, TextNoteAdapter.NoteHolder> {
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public TextNoteAdapter(@NonNull FirestoreRecyclerOptions<TextNote> options) {
        super(options);
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        CardView cardView;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            content = itemView.findViewById(R.id.textView_content);
            cardView = itemView.findViewById(R.id.layoutCardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION && longClickListener != null) {
                        longClickListener.onItemLongClick(getSnapshots().getSnapshot(pos), pos);
                        return true;
                    }
                    return false;
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull TextNote model) {
        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());
        holder.cardView.setCardBackgroundColor(model.getBackgroundColor());
        holder.content.setTextColor(model.getContentColor());
        holder.title.setTextColor(model.getTitleColor());
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_note_item, parent, false);
        return new NoteHolder(v);
    }

    public void deleteTextNote(int pos) {
        getSnapshots().getSnapshot(pos).getReference().delete();
    }

    public TextNote backupDeletedTexNote(int pos) {
        return getSnapshots().getSnapshot(pos).toObject(TextNote.class);
    }

}
