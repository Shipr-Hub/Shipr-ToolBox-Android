package tech.shipr.toolboxdev.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tech.shipr.toolboxdev.R;
import tech.shipr.toolboxdev.adapter.CustomExpandableListAdapter;
import tech.shipr.toolboxdev.model.Tool;
import tech.shipr.toolboxdev.model.User;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    FirebaseFirestore db;
    LinearLayout AllCalLayout;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser user;
    HashMap<String, List<Tool>> expandableListDetail;

    List<String> allCatExpandableListTitle;
    ExpandableListView allCatExpandableListView;
    ExpandableListAdapter allCatExpandableListAdapter;

    GridView gv;
    ArrayAdapter<String> gridViewArrayAdapter;
    List<String> favToolList;
    List<Tool> favClickToolList;

    GridView pgv;
    ArrayAdapter<String> pgridViewArrayAdapter;
    List<String> pToolList;
    List<Tool> pClickToolList;


    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv = findViewById(R.id.favouriteAppsGrid);
        pgv = findViewById(R.id.personalAppsGrid);
        AllCalLayout = findViewById(R.id.allCategoriesContainer);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        startAuthListener();
        authSignupChecker();

        gv.setOnItemClickListener((parent, v, position, id) -> {
            Tool tool = favClickToolList.get(position);
            Intent intent = new Intent(MainActivity.this, ToolViewActivity.class);
            intent.putExtra("tool", tool);
            startActivity(intent);
        });

        pgv.setOnItemClickListener((parent, v, position, id) -> {
            Tool tool = pClickToolList.get(position);
            Intent intent = new Intent(MainActivity.this, ToolViewActivity.class);
            intent.putExtra("tool", tool);
            startActivity(intent);
        });
    }

    private void startAuthListener() {
        mAuthStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //User is signed in
                uid = user.getUid();
                Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                MainActivity.this.onSignedInInitialize();
            } else {
                // User is signed out
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build());
                MainActivity.this.startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setLogo(R.mipmap.ic_launcher)
                                .setTheme(R.style.AppTheme)
                                .setAvailableProviders(providers)
                                .build(),
                        1);
            }
        };
    }

    private void authSignupChecker() {

        if (mFirebaseAuth.getCurrentUser() == null) {
            Log.d("auth", "null user so authsignupchecker is running");
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    1);

        } else {
            //    Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show();
            user = mFirebaseAuth.getCurrentUser();
            uid = user.getUid();
            onSignedInInitialize();
        }
    }

    private void onSignedInInitialize() {
        loadAllCatNamesFromFirebase(AllCalLayout);
        setupFavToolAdapter();
        setupPersonalToolAdapter();
        loadFavCategoriesAndProducts();
        loadPersonalTool();
    }

    private void loadAllCatNamesFromFirebase(final LinearLayout layout) {
        db.collection("cat")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final String cat = document.getId();
                            addCatFromFirebaseToView(cat, layout);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void setupFavToolAdapter() {
        favToolList = new ArrayList<String>();
        favClickToolList = new ArrayList<Tool>();
        gridViewArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favToolList);
        gv.setAdapter(gridViewArrayAdapter);

    }

    private void setupPersonalToolAdapter() {
        pToolList = new ArrayList<String>();
        pClickToolList = new ArrayList<Tool>();
        pgridViewArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pToolList);
        pgv.setAdapter(pgridViewArrayAdapter);

    }

    private void loadFavCategoriesAndProducts() {
        final DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);

                    ArrayList favcat = (ArrayList) user.getFavcat();
                    if (favcat != null && favcat.size() > 0) {
                        for (int i = 0; i < favcat.size(); i++) {
                            addCatFromFirebaseToView(favcat.get(i).toString(), findViewById(R.id.favouriteCategoriesContainer));
                        }
                    }

                    ArrayList favtool = (ArrayList) user.getFavtool();
                    if (favtool != null && favtool.size() > 0) {
                        for (int i = 0; i < favtool.size(); i++) {
                            loadToolFromFirebase((DocumentReference) favtool.get(i));
                        }
                    }


                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void loadPersonalTool() {
        db.collection("users").document(uid).collection("tools")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tool tool = document.toObject(Tool.class);
                            addToolToPView(tool);

                            // TODO: 7/23/19 set the adapter
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

    }

    private void loadToolFromFirebase(DocumentReference ref) {
        final DocumentReference docRef = ref;
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Tool tool = document.toObject(Tool.class);
                    addToolToFavView(tool);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void addToolToFavView(Tool tool) {
        favToolList.add(favToolList.size(), tool.getName());
        favClickToolList.add(favClickToolList.size(), tool);
        gridViewArrayAdapter.notifyDataSetChanged();
    }

    private void addToolToPView(Tool tool) {
        pToolList.add(pToolList.size(), tool.getName());
        pClickToolList.add(pClickToolList.size(), tool);
        pgridViewArrayAdapter.notifyDataSetChanged();
    }

    private void addCatFromFirebaseToView(final String cat, final LinearLayout layout) {

        db.collection("cat").document(cat).collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Tool> data = new ArrayList<Tool>();


                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "onComplete: " + document.getId());


                            Tool tool = document.toObject(Tool.class);
                            tool.setKey(document.getId());
                            tool.setCat(cat);
                            data.add(tool);

                        }
                        expandableListDetail = new HashMap<String, List<Tool>>();
                        expandableListDetail.put(cat, data);

                        allCatExpandableListView = new ExpandableListView(getApplicationContext());
                        allCatExpandableListView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));


                        layout.addView(allCatExpandableListView);

                        Log.d(TAG, "onCoomplete: " + expandableListDetail);
                        allCatExpandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                        allCatExpandableListAdapter = new CustomExpandableListAdapter(getApplicationContext(), allCatExpandableListTitle, expandableListDetail);
                        allCatExpandableListView.setAdapter(allCatExpandableListAdapter);

                        allCatExpandableListView.setOnGroupExpandListener(groupPosition -> {
                            //  Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
                        });

                        allCatExpandableListView.setOnGroupCollapseListener(groupPosition -> {
                            //  Toast.makeText(getApplicationContext(), allCatExpandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
                        });

                        allCatExpandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                            setListViewHeight(parent, groupPosition);
                            return false;
                        });

                        allCatExpandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                            Log.d(TAG, "onChildClick: " + expandableListDetail);
                            Tool tool;
                            Log.d(TAG, "onChildClick: " + expandableListDetail.get(allCatExpandableListTitle.get(groupPosition)).get(childPosition));
                            tool = Objects.requireNonNull(expandableListDetail.get(allCatExpandableListTitle.get(groupPosition))).get(childPosition);
                            Intent intent = new Intent(MainActivity.this, ToolViewActivity.class);
                            intent.putExtra("tool", tool);
                            startActivity(intent);
                            return false;
                        });


                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });


    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("auth", "onActivityResult");
        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d("auth", "result is okay");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
                onSignedInInitialize();
            } else {
                Log.d("auth", "result is not okay");
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                //      Log.w("auth", "signInEmail:failure", Objects.requireNonNull(String.valueOf(response.getError().getErrorCode()));
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("auth", "requestcode is not signin");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            startActivity(new Intent(MainActivity.this, AddTool.class));
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            startActivity(new Intent(MainActivity.this, AddToolAdmin.class));
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}
