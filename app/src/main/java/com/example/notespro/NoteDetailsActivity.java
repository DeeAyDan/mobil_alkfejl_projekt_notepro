package com.example.notespro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteButton;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextView = findViewById(R.id.delete_note_text_view_button);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode =  true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextView.setVisibility(View.VISIBLE);
        }

        saveNoteButton.setOnClickListener((v)-> saveNote());

        deleteNoteTextView.setOnClickListener((v)-> deleteNote());

    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForNores().document(docId);

        }else{
            documentReference = Utility.getCollectionReferenceForNores().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this, "Note added successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this, "Failed while adding note");
                }
            }
        });
    }

    void deleteNote(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNores().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this, "Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this, "Failed while deleting note");
                }
            }
        });
    }

}