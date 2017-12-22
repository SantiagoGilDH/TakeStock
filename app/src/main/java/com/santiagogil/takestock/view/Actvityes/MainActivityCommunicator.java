package com.santiagogil.takestock.view.Actvityes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.controller.UsersController;
import com.santiagogil.takestock.model.daos.ItemsFirebaseDAO;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.model.pojos.User;
import com.santiagogil.takestock.model.pojos.UserDatabase;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.DialogAddItem;
import com.santiagogil.takestock.view.Fragments.FragmentItemsViewPager;
import com.santiagogil.takestock.view.Fragments.FragmentRecyclerItems;
import com.santiagogil.takestock.view.Fragments.FragmentItemListsViewPager;
import com.santiagogil.takestock.view.onboarding.OnboardingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentRecyclerItems.FragmentActivityCommunicator, DialogAddItem.AddItemDialogCommunicator {


    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView;
    private FragmentItemListsViewPager fragmentItemListsViewPager;
    private String filter = "";
    private Toolbar toolbar;
    private EditText toolbarEditText;
    private BottomNavigationView bottomNavigationView;
    private Context context;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarEditText = (EditText) toolbar.findViewById(R.id.toolbar_edit_text_search);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setPadding(0,0,0,0);

        //addGroceriesListToCurrentUsers();

        bottomNavigationView.setMeasureAllChildren(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_item:
                        DialogAddItem dialogAddItem = new DialogAddItem();
                        dialogAddItem.setCommunicator(MainActivityCommunicator.this);
                        dialogAddItem.show(getFragmentManager(), null);
                        break;
                    case R.id.action_search:
                        toolbarEditText.setBackgroundColor(ContextCompat.getColor( context, R.color.icons));
                        toolbarEditText.setFocusableInTouchMode(true);
                        toolbarEditText.requestFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(toolbarEditText, 0);
                        break;
                    case R.id.action_back:
                        onBackPressed();
                        break;

                }
                return true;
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() == null) {

            Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {

            navigationView = (NavigationView) findViewById(R.id.navigationView);
            NavigationViewListener navigationViewListener = new NavigationViewListener();
            navigationView.setNavigationItemSelectedListener(navigationViewListener);


            fragmentItemListsViewPager = new FragmentItemListsViewPager();
            Bundle bundle = new Bundle();
            bundle.putString(FragmentRecyclerItems.FILTER, filter);
            fragmentItemListsViewPager.setArguments(bundle);
            fragmentItemListsViewPager.setFragmentActivityCommunicator(MainActivityCommunicator.this);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, fragmentItemListsViewPager);
            fragmentTransaction.commit();

            ConsumptionsController consumptionsController = new ConsumptionsController();
            consumptionsController.updateConsumptionsDatabase(this, new ResultListener<List<Consumption>>() {
                @Override
                public void finish(List<Consumption> result) {

                }
            });

            }
        }



    @Override
    public void onItemTouched (Item touchedItem, Integer touchedPosition, BehaviourGetItemList
    behaviourGetItemList, TextView textViewItemName, TextView textViewItemStock,
                               TextView textViewItemIndependence){

        FragmentItemsViewPager fragmentItemsViewPager = new FragmentItemsViewPager();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentItemsViewPager.BEHAVIOURGETITEMLIST, behaviourGetItemList);
        bundle.putString(FragmentItemsViewPager.ITEMID, touchedItem.getID());
        bundle.putInt(FragmentItemsViewPager.POSITION, touchedPosition);
        fragmentItemsViewPager.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_holder, fragmentItemsViewPager);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void updateActionBarTitle(String title) {

        getSupportActionBar().setTitle((CharSequence) title);
    }

    @Override
        public void onBackPressed () {

            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 0) {
                moveTaskToBack(true);
            }
            super.onBackPressed();
        }

    @Override
    public void addNewItem(String itemName) {


        ItemsController itemsController = new ItemsController();
        Item item = new Item(itemName);
        itemsController.addItemToDatabases(this, item);
        //TODO: check if item already exists
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
    }

    private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.action_logout) {

                    logout();
                }


                return false;
            }
        }

        @Override
        protected void onStart () {
            super.onStart();
            fAuth.addAuthStateListener(authStateListener);
        }

    private void logout() {

        fAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);

        addListenerToSearchEditText();

        return true;
    }

    private void addListenerToSearchEditText() {
        EditText editTextSearch = (EditText) toolbar.findViewById(R.id.toolbar_edit_text_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filter = s.toString();
                fragmentItemListsViewPager.getArguments().putString(FragmentRecyclerItems.FILTER, filter);
                fragmentItemListsViewPager.getItemListsViewPagerAdapter().updateFragmentsWithFilter(filter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addGroceriesListToCurrentUsers() {

        final FirebaseHelper firebaseHelper = new FirebaseHelper();
        DatabaseReference databaseReference =  firebaseHelper.getUserDB();
        ItemsController itemsController = new ItemsController();
        List<Item> items = itemsController.getAllItems(context);
        for(Item item: items){

            databaseReference.child("Lists").child("Groceries").child("Items").child(item.getID()).setValue(item.getID());

        }

    }


}


