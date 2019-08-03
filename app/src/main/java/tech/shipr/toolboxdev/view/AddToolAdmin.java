package tech.shipr.toolboxdev.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import tech.shipr.toolboxdev.R;
import tech.shipr.toolboxdev.model.Tool;

public class AddToolAdmin extends AppCompatActivity {
    EditText catEditText;
    EditText nameEditText;
    EditText urlEditText;
    EditText aboutEditText;

    String cat;
    String name;
    String url;
    String about;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tool_admin);
        findEditText();
        initFirebase();
    }

    private void findEditText() {
        nameEditText = findViewById(R.id.nameEditText);
        urlEditText = findViewById(R.id.urlEditText);
        catEditText = findViewById(R.id.catEditText);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public void submit(View v) {
        getDataFromEditText();
        uploadToolToCat(createTool());
    }

    private void getDataFromEditText() {
        cat = catEditText.getText().toString();
        name = nameEditText.getText().toString();
        url = urlEditText.getText().toString();
        about = aboutEditText.getText().toString();
    }

    private Tool createTool() {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setUrl(url);
        tool.setAbout(about);
        return tool;
    }

    private void uploadToolToCat(Tool tool) {

        db.collection("cat").document(cat).set(new HashMap<>(), SetOptions.merge());
        db.collection("cat").document(cat).collection("products").document(tool.getName()).set(tool);
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
