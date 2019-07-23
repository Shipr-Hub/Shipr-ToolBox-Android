package tech.shipr.toolboxdev.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import tech.shipr.toolboxdev.R;
import tech.shipr.toolboxdev.model.Tool;

public class AddTool extends AppCompatActivity {
    EditText nameEditText;
    EditText urlEditText;

    String name;
    String url;

    FirebaseFirestore db;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tool);
        findEditText();
        initFirebase();
    }

    private void findEditText() {
        nameEditText = findViewById(R.id.nameEditText);
        urlEditText = findViewById(R.id.urlEditText);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void submit(View v) {
        getDataFromEditText();
        uploadToolToPrivate(createTool());
    }

    private void getDataFromEditText() {
        name = nameEditText.getText().toString();
        url = urlEditText.getText().toString();
    }

    private Tool createTool() {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setUrl(url);
        return tool;
    }

    private void uploadToolToPrivate(Tool tool) {
        db.collection("users").document(uid).collection("tools").document(tool.getName()).set(tool);
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
