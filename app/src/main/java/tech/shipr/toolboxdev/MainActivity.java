package tech.shipr.toolboxdev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tech.shipr.toolboxdev.model.Tool;

public class MainActivity extends AppCompatActivity {

    // TextView debugTextView;
    String TAG = "MainActivity";
    FirebaseFirestore db;
    LinearLayout AllCalLayout;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;


    HashMap<String, List<String>> expandableListDetail;
    List<String> allCatExpandableListTitle;
    ExpandableListView allCatExpandableListView;
    ExpandableListAdapter allCatExpandableListAdapter;

    //ExpandableListView expandableListView;
    //ExpandableListAdapter expandableListAdapter;
    //List<String> expandableListTitle;
    //
    // final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // debugTextView = findViewById(R.id.debugTextView);
        AllCalLayout = findViewById(R.id.allCategoriesContainer);
        mFirebaseAuth = FirebaseAuth.getInstance();
        startAuthListener();
        db = FirebaseFirestore.getInstance();
        loadAllCat("cat", "products", AllCalLayout);
    }

    private void loadAllCat(final String firstCat, final String secondCat, final LinearLayout layout) {
        db.collection(firstCat)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String cat = document.getId();
                                Log.d(TAG, cat + " => " + document.getData());
                                db.collection(firstCat).document(cat).collection(secondCat)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    List<String> data = new ArrayList<String>();

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                     Tool tool = document.toObject(Tool.class);
                                                        Log.d(TAG, "onComplete() called with: task = [" + tool + "]");
                                                        String name = document.getData().get("name").toString();
                                                        data.add(name);

                                                    }

                                                    expandableListDetail = new HashMap<String, List<String>>();
                                                    expandableListDetail.put(cat, data);
                                                    Log.d("list", expandableListDetail.toString());


                                                    allCatExpandableListView = new ExpandableListView(getApplicationContext());
                                                    allCatExpandableListView.setLayoutParams(new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT));


                                                    layout.addView(allCatExpandableListView);


                                                    allCatExpandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                                                    allCatExpandableListAdapter = new CustomExpandableListAdapter(getApplicationContext(), allCatExpandableListTitle, expandableListDetail);
                                                    allCatExpandableListView.setAdapter(allCatExpandableListAdapter);

                                                    allCatExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                                                        @Override
                                                        public void onGroupExpand(int groupPosition) {
                                                            Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    allCatExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                                                        @Override
                                                        public void onGroupCollapse(int groupPosition) {
                                                            Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    allCatExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                                        @Override
                                                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                                            setListViewHeight(parent, groupPosition);
                                                            return false;
                                                        }
                                                    });

                                                    allCatExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                                        @Override
                                                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                                        //    Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " -> " + expandableListDetail.get(allCatExpandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                                                            return false;
                                                        }
                                                    });




                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });


                            }


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void appendDebug(Object debugString) {
     //   debugTextView.append(" \n" + debugString.toString());
    }



    public void openTools(View v) {
        startActivity(new Intent(MainActivity.this, ToolActivity.class));
    }

    private void startAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //   onSignedInInitialize(user.getDisplayName(), user.getUid());
                    Toast.makeText(MainActivity.this, "You're now signed in", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    // onSignedOutCleanup();
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Collections.singletonList(
                            new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            1);


                }

            }
        };
    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}
