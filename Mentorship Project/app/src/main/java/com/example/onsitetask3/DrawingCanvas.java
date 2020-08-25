package com.example.onsitetask3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DrawingCanvas extends AppCompatActivity implements DrawSaveDialog.DrawSaveDialogListener {

    private static final String TAG = "DrawingCanvas";

    private DrawingCanvasView drawingCanvasView;
    private Bitmap bitmap;
    private Canvas canvas;
    private DrawSaveDialog saveDialog;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Draw");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        drawingCanvasView = new DrawingCanvasView(this);
        setContentView(drawingCanvasView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_image_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button:
                openSaveDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveImage() {
        bitmap = Bitmap.createBitmap(drawingCanvasView.getWidth(), drawingCanvasView.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawingCanvasView.draw(canvas);
        String path = getExternalFilesDir(null).getAbsolutePath();
        File file = new File(path + "/" + title + ".jpeg");
        FileOutputStream fos;

        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            DrawNoteFragment.addDrawNote(title, path + "/" + title + ".jpeg");

        } catch (FileNotFoundException e) {
            Log.d(TAG, "saveImage: " + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "saveImage: " + e.toString());
        }

        finish();

    }

    public void openSaveDialog() {
        saveDialog = new DrawSaveDialog();
        saveDialog.show(getSupportFragmentManager(), "save dialog");
    }

    @Override
    public void applyTitle(String title) {
        this.title = title;
        saveImage();
    }

}
