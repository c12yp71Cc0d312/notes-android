package com.example.onsitetask3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class DrawNoteFragment extends Fragment {

    private static final String TAG = "DrawNoteFragment";

    private FloatingActionButton addButton;
    private RecyclerView recyclerView;
    private static ArrayList<DrawNote> drawings = new ArrayList<>();
    private DrawNoteAdapter drawNoteAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Draw Notes");
        View v = inflater.inflate(R.layout.fragment_draw_notes, container, false);

        addButton = v.findViewById(R.id.add_button);
        recyclerView = v.findViewById(R.id.recycler_view_drawing_note);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCanvas = new Intent(getContext(), DrawingCanvas.class);
                startActivity(toCanvas);
            }
        });

        buildDrawNotesCards();
        buildRecyclerView();

        return v;
    }

    public static void addDrawNote(String title, String path) {
        drawings.add(new DrawNote(title, path));
    }

    public void buildDrawNotesCards() {
        drawings.clear();
        String rootPath = getContext().getExternalFilesDir(null).getAbsolutePath();
        File rootFolder = new File(rootPath);
        Log.d(TAG, "buildDrawNotesCards: " + rootPath);
        File[] files = rootFolder.listFiles();

        for(File f : files) {
            String title = f.getName();
            title = title.substring(0, title.length() - 5);
            drawings.add(new DrawNote(title, f.getAbsolutePath()));
            Log.d(TAG, "buildDrawNotesCards: " + f.getAbsolutePath());
        }

    }

    public void buildRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        drawNoteAdapter = new DrawNoteAdapter(drawings);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(drawNoteAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                DrawNote deletedDrawing = drawings.get(viewHolder.getAdapterPosition());
                String imgPath = deletedDrawing.getImagePath();
                File image = new File(imgPath);
                image.delete();

                drawings.remove(viewHolder.getAdapterPosition());
                drawNoteAdapter.notifyDataSetChanged();
            }

        }).attachToRecyclerView(recyclerView);

    }

}
