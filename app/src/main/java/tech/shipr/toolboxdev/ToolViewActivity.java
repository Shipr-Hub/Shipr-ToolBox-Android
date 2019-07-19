package tech.shipr.toolboxdev;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ToolViewActivity extends AppCompatActivity {
    TextView textTextView;
    TextView nameTextView;
    TextView urlTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_view);

        nameTextView = findViewById(R.id.toolNameTextView);
        urlTextView = findViewById(R.id.toolUrlTextView);
        textTextView = findViewById(R.id.toolTextTextView);
        setNameTextView(getIntent().getStringExtra("name"));
        setUrlTextView(getIntent().getStringExtra("url"));
    }

    private void setTextTextView(String text) {
        textTextView.setText(text);
    }

    private void setNameTextView(String name) {
        nameTextView.setText(name);
    }

    private void setUrlTextView(String url) {
        urlTextView.setText(url);
    }

}
