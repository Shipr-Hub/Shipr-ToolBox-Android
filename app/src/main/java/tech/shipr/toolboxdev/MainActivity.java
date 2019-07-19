package tech.shipr.toolboxdev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView debugTextView;
    String TAG = "MainActivity";
    FirebaseFirestore db;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    //ExpandableListView expandableListView;
    //ExpandableListAdapter expandableListAdapter;
    //List<String> expandableListTitle;
    //
    // final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debugTextView = findViewById(R.id.debugTextView);
        mFirebaseAuth = FirebaseAuth.getInstance();
        startAuthListener();
        db = FirebaseFirestore.getInstance();
        getData();

        // startActivity(new Intent(MainActivity.this, ToolActivity.class));
//        List<String> cricket = new ArrayList<String>();
//        cricket.add("India");
//        cricket.add("Pakistan");
//        cricket.add("Australia");
//        cricket.add("England");
//        cricket.add("South Africa");
//
//        List<String> football = new ArrayList<String>();
//        football.add("Brazil");
//        football.add("Spain");
//        football.add("Germany");
//        football.add("Netherlands");
//        football.add("Italy");


//        expandableListDetail.put("CRICKET TEAMS", cricket);
//        expandableListDetail.put("FOOTBALL TEAMS", football);

        //  expandableListView =  findViewById(R.id.allCategoriesAddedByJava);
        // expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        // expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        //     expandableListView.setAdapter(expandableListAdapter);
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " -> " + expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }

    private void getData() {

        db.collection("cat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cat = document.getId();
                                Log.d(TAG, cat + " => " + document.getData());
                                db.collection("cat").document(cat).collection("products")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        appendDebug(document);
                                                    }
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
        debugTextView.append(" \n" + debugString.toString());
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
}
