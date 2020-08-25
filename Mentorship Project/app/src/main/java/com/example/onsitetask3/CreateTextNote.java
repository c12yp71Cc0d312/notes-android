package com.example.onsitetask3;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateTextNote extends AppCompatActivity {

    private static final String TAG = "CreateTextNote";
    public static final int RECOGNIZER_RESULT = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private ConstraintLayout layout;
    private EditText editTextTitle, editTextContent;
    private String id, title, content;
    private Timestamp created;
    private boolean update = false;

    private ImageView imageView;
    private Uri imageUri;

    private int contentColor, titleColor, backgroundColor;
    private Drawable background;

    private Typeface typeface;

    private CollectionReference notebookRef = FirebaseFirestore.getInstance()
            .collection("Notebook");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_text_note);
        setTitle("New Text Note");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        layout = findViewById(R.id.layout_create_text_note);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        imageView = findViewById(R.id.imageViewInsert);

        background = layout.getBackground();
        if(background instanceof ColorDrawable) {
            backgroundColor = ((ColorDrawable) background).getColor();
        }

        contentColor = editTextContent.getCurrentTextColor();
        titleColor = editTextTitle.getCurrentTextColor();

        id = getIntent().getStringExtra("id");
        if(id != null) {
            update = true;
            setTitle("Edit Text Note");
            setNote();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_text_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button:
                saveNote();
                return true;
            case R.id.stt:
                speechToText();
                return true;
            case R.id.schoolbell:
                changeFont("schoolbell");
                return true;
            case R.id.satisfy:
                changeFont("satisfy");
                return true;
            case R.id.cbyg:
                changeFont("cbyg");
                return true;
            case R.id.colorContent:
                changeColors("text", contentColor);
                return true;
            case R.id.colorTitle:
                changeColors("title", titleColor);
                return true;
            case R.id.colorBackground:
                changeColors("bg", backgroundColor);
                return true;
            case R.id.imagePicker:
                insertImage();
                return true;
            case R.id.deleteImage:
                removeImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNote() {

        getValuesOfNote();

        if(content.trim().isEmpty()) {
            Toast.makeText(this, "Please enter content", Toast.LENGTH_SHORT).show();
            return;
        }

        if(update) {
            Map<String, Object> updatedNote = new HashMap<>();
            updatedNote.put("title", title);
            updatedNote.put("content", content);
            updatedNote.put("contentColor", contentColor);
            updatedNote.put("titleColor", titleColor);
            updatedNote.put("backgroundColor", backgroundColor);
            updatedNote.put("imgUri", imageUri);
            notebookRef.document(id)
                    .update(updatedNote);
            Log.d(TAG, "saveNote: new title: " + title + "new content: " + content);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }
        else {
            notebookRef.add(new TextNote(title, content, created, contentColor, titleColor, backgroundColor))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            id = documentReference.getId();
                            Log.d(TAG, "onSuccess: id: " + id);
                        }
                    });
            uploadImage();
        }

        finish();

    }

    public void setNote() {
        notebookRef.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TextNote note = documentSnapshot.toObject(TextNote.class);

                        title = note.getTitle();
                        content = note.getContent();
                        contentColor = note.getContentColor();
                        titleColor = note.getTitleColor();
                        backgroundColor = note.getBackgroundColor();

                        editTextTitle.setText(title);
                        editTextContent.setText(content);

                        editTextContent.setTextColor(contentColor);
                        editTextTitle.setTextColor(titleColor);
                        layout.setBackgroundColor(backgroundColor);

                        if(note.getImgUri() != null) {
                            Picasso.get()
                                    .load(Uri.parse(note.getImgUri()))
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(imageView);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    public void speechToText() {
        Intent speechText = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechText.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text");
        startActivityForResult(speechText, RECOGNIZER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(editTextTitle.hasFocus()) {
                String currentTitle = editTextTitle.getText().toString();
                String newTitle = currentTitle + " " + text.get(0);
                editTextTitle.setText(newTitle);
            }
            else {
                String currentContent = editTextContent.getText().toString();
                String newContent = currentContent + " " + text.get(0);
                editTextContent.setText(newContent);
            }
            Log.d(TAG, "onActivityResult: " + text.get(0));
        }

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }

    }

    public void changeFont(String font) {
        switch (font) {
            case "schoolbell":
                typeface = Typeface.createFromAsset(getAssets(), "fonts/schoolbell_regular.ttf");
                break;
            case "cbyg":
                typeface = Typeface.createFromAsset(getAssets(), "fonts/covered_by_your_grace_regular.ttf");
                break;
            case "satisfy":
                typeface = Typeface.createFromAsset(getAssets(), "fonts/satisfy.ttf");
                break;
        }
        editTextContent.setTypeface(typeface);
    }

    public void changeColors(final String str, int initialColor) {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, initialColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                switch (str){
                    case "text":
                        contentColor = color;
                        editTextContent.setTextColor(contentColor);
                        break;
                    case "title":
                        titleColor = color;
                        editTextTitle.setTextColor(titleColor);
                        break;
                    case "bg":
                        backgroundColor = color;
                        layout.setBackgroundColor(backgroundColor);
                    default:
                        Log.d(TAG, "onOk: invalid switch case");
                }
            }
        });
        dialog.show();

    }

    public void insertImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void removeImage() {
        imageView.setImageDrawable(null);
        imageUri = null;
    }

    public void uploadImage() {
        if(imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!urlTask.isSuccessful());
                            final Uri downloadUrl = urlTask.getResult();
                            Log.d(TAG, "onSuccess: url: " + downloadUrl.toString());

                            Log.d(TAG, "onSuccess: upload: id: " + id);

                            notebookRef.document(id).update("imgUri", downloadUrl.toString());
                            Toast.makeText(getApplicationContext(), "Note added", Toast.LENGTH_SHORT).show();

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateTextNote.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        }

    }

    public void getValuesOfNote() {

        title = editTextTitle.getText().toString();
        content = editTextContent.getText().toString();
        created = Timestamp.now();
        background = layout.getBackground();
        if(background instanceof ColorDrawable) {
            backgroundColor = ((ColorDrawable) background).getColor();
        }

        contentColor = editTextContent.getCurrentTextColor();
        titleColor = editTextTitle.getCurrentTextColor();

    }

}