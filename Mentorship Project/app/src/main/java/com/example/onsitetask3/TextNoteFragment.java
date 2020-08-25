package com.example.onsitetask3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TextNoteFragment extends Fragment {

    private static final String TAG = "TextNoteFragment";

    private FloatingActionButton addButton;
    private FloatingActionButton undoButton;
    private RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private TextNoteAdapter textNoteAdapter;
    private TextNote deletedNote;

    private Query queryAtoZ;
    private Query queryZtoA;
    private Query queryCreatedOldToNew;
    private Query queryCreatedNewToOld;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Text Notes");

        View v = inflater.inflate(R.layout.fragment_text_notes, container, false);

        addButton = v.findViewById(R.id.add_button);
        undoButton = v.findViewById(R.id.undo_button);
        recyclerView = v.findViewById(R.id.recycler_view_text_note);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createMessage = new Intent(getContext(), CreateTextNote.class);
                startActivity(createMessage);
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoButton.setVisibility(View.GONE);
                notebookRef.add(deletedNote);
            }
        });

        queryAtoZ = notebookRef.orderBy("title", Query.Direction.ASCENDING);
        queryZtoA = notebookRef.orderBy("title", Query.Direction.DESCENDING);
        queryCreatedNewToOld = notebookRef.orderBy("created", Query.Direction.DESCENDING);
        queryCreatedOldToNew = notebookRef.orderBy("created", Query.Direction.ASCENDING);

        setUpRecyclerView(queryCreatedNewToOld);

        return v;
    }

    public void setUpRecyclerView(Query query) {

        FirestoreRecyclerOptions<TextNote> options = new FirestoreRecyclerOptions.Builder<TextNote>()
                .setQuery(query, TextNote.class)
                .build();

        textNoteAdapter = new TextNoteAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(textNoteAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        deletedNote = textNoteAdapter.backupDeletedTexNote(viewHolder.getAdapterPosition());
                        textNoteAdapter.deleteTextNote(viewHolder.getAdapterPosition());
                        undoButton.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onSwiped: ");

                        break;
                    case ItemTouchHelper.RIGHT:
                        textNoteAdapter.deleteTextNote(viewHolder.getAdapterPosition());
                        break;
                }
            }

        }).attachToRecyclerView(recyclerView);

        textNoteAdapter.setOnItemClickListener(new TextNoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                String id = documentSnapshot.getId();
                Intent toEditTextNote = new Intent(getActivity(), CreateTextNote.class);
                toEditTextNote.putExtra("id", id);
                startActivity(toEditTextNote);
            }
        });

        textNoteAdapter.setOnItemLongClickListener(new TextNoteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int pos) {
                TextNote note = documentSnapshot.toObject(TextNote.class);
                Timestamp timestamp = note.getCreated();
                TextNoteInfoDialog dialog = new TextNoteInfoDialog(timestamp);
                dialog.show(getFragmentManager(), "info");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        textNoteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        textNoteAdapter.stopListening();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.text_notes_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.TitleAtoz:
                setUpRecyclerView(queryAtoZ);
                textNoteAdapter.startListening();
                return true;
            case R.id.TitleztoA:
                setUpRecyclerView(queryZtoA);
                textNoteAdapter.startListening();
                return true;
            case R.id.createdNewToOld:
                setUpRecyclerView(queryCreatedNewToOld);
                textNoteAdapter.startListening();
                return true;
            case R.id.createdOldToNew:
                setUpRecyclerView(queryCreatedOldToNew);
                textNoteAdapter.startListening();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
