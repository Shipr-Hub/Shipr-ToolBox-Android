package tech.shipr.toolboxdev.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tech.shipr.toolboxdev.R;
import tech.shipr.toolboxdev.model.Tool;

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
        Tool tool = (Tool) getIntent().getSerializableExtra("tool");
        setNameTextView(tool.getName());
        setUrlTextView(tool.getUrl());
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
