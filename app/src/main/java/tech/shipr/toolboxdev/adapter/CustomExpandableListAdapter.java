package tech.shipr.toolboxdev.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tech.shipr.toolboxdev.R;
import tech.shipr.toolboxdev.model.Tool;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Tool>> expandableListDetail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<Tool>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Tool mTool = (Tool) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(mTool.getName());
        Button starButton = convertView.findViewById(R.id.starToolButton);
        starButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
        starButton.setOnClickListener(v -> {
            // Perform action on click
            starTool(mTool);

        });
        return convertView;
    }

    @SuppressLint("RestrictedApi")
    private void starTool(Tool mTool) {
        DocumentReference starToolRef = db.collection("users").document(uid);
        DocumentReference toolRef = db.collection("cat").document(mTool.getCat()).collection("products").document(mTool.getKey());
        starToolRef.set(new HashMap<>(), SetOptions.merge());
        starToolRef.update("favtool", FieldValue.arrayUnion(toolRef));
        Toast.makeText(getApplicationContext(), mTool.getName() + " added to favourites", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();

    }

    @SuppressLint("RestrictedApi")
    private void starCat(String mCat) {
        DocumentReference starToolRef = db.collection("users").document(uid);
        Log.d("meow", "now");
        Log.d("cat", mCat);
        Log.d("uid", uid);
        starToolRef.set(new HashMap<>(), SetOptions.merge());
        starToolRef.update("favcat", FieldValue.arrayUnion(mCat));
        Toast.makeText(getApplicationContext(), mCat + " added to favourites", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        // Log.d("tag", "getChildrenCount: " + this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size());
        Log.d("tag", "getChildrenCount: " + this.expandableListDetail);
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        Log.d("TAG", "getGroupCount: " + this.expandableListTitle.size());
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        Log.d("TAG", "getGroupView: " + listTitle);
        listTitleTextView.setText(listTitle);

        Button starButton = convertView.findViewById(R.id.catStarButton);
        starButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
        starButton.setOnClickListener(v -> {
            // Perform action on click

            starCat(listTitle);

        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
