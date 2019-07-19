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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tech.shipr.toolboxdev.model.Tool;
import tech.shipr.toolboxdev.model.User;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    FirebaseFirestore db;
    LinearLayout AllCalLayout;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    HashMap<String, List<Tool>> expandableListDetail;
    List<String> allCatExpandableListTitle;
    ExpandableListView allCatExpandableListView;
    ExpandableListAdapter allCatExpandableListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllCalLayout = findViewById(R.id.allCategoriesContainer);
        mFirebaseAuth = FirebaseAuth.getInstance();
        startAuthListener();
        db = FirebaseFirestore.getInstance();
        loadAllCat(AllCalLayout);
        loadFavCategories();


    }

    private void loadFavCategories() {
        final DocumentReference docRef = db.collection("users").document("123");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        Object favcat = user.getFavcat();
                        ArrayList l = (ArrayList) favcat;

                        for (int i = 0; i < l.size(); i++) {
                            loadData(l.get(i).toString(), (LinearLayout) findViewById(R.id.favouriteCategoriesContainer));
                        }


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void loadAllCat(final LinearLayout layout) {
        db.collection("cat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String cat = document.getId();
                                Log.d(TAG, cat + " => " + document.getData());
                                loadData(cat, layout);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void loadData(final String cat, final LinearLayout layout) {

        db.collection("cat").document(cat).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Tool> data = new ArrayList<Tool>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Tool tool = document.toObject(Tool.class);
                                data.add(tool);

                            }

                            expandableListDetail = new HashMap<String, List<Tool>>();
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
                                    //   Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " -> " + expandableListDetail.get(allCatExpandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                                    Tool tool = expandableListDetail.get(allCatExpandableListTitle.get(groupPosition)).get(childPosition);
                                    Toast.makeText(getApplicationContext(), tool.toString(), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, ToolViewActivity.class);
                                    intent.putExtra("name", tool.getName());
                                    intent.putExtra("url", tool.getUrl());
                                    startActivity(intent);
                                    return false;
                                }
                            });


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


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

    private void setListViewHeight(ExpandableListView listView, int group) {
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
