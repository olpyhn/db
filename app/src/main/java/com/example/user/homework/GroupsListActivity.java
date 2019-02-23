package com.example.user.homework;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class Group{

    private String name = "", id = "", password = "";
    private ArrayList<String> admin = new ArrayList<>();

    public ArrayList<String> getAdmin() {
        return admin;
    }

    public void setAdmin(ArrayList<String> admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Group(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Group() {
    }
}

class GroupsAdapter extends BaseAdapter {

    private ArrayList<Group> groups = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;


    GroupsAdapter(ArrayList<Group> groups, Context context) {
        if (groups != null) this.groups = groups;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.groups_list_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.txt_item_name)).setText(groups.get(position).getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupViewActivity.class);
                intent.putExtra("GROUPID", groups.get(position).getId());
                context.startActivity(intent);
            }
        });
        return view;
    }
}

public class GroupsListActivity extends AppCompatActivity {

    ListView listView;
    ImageButton btnBurger;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    TextView txtName;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        listView = findViewById(R.id.groups_list);
        currentUser = new User("", "", "", new ArrayList<Group>());
        btnBurger = findViewById(R.id.btn_burger_groups);
        mDrawerLayout = findViewById(R.id.groups_list_layout);
        navigationView = findViewById(R.id.groups_menu);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_find_groups:{
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        break;
                    }
                    case R.id.nav_my_groups:{
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.nav_settings:{
                        startActivity(new Intent(getApplicationContext(), AccountSettingsActivity.class));
                        break;
                    }
                    case R.id.nav_add_group:{
                        startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
                        break;
                    }
                }
                return false;
            }
        });
        btnBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        reference.child("users").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                currentUser = user;
                if (txtName != null) txtName.setText(currentUser.getName() + " " + currentUser.getSurname());
                listView.setAdapter(new GroupsAdapter(currentUser.getGroups(), getApplicationContext()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                currentUser = user;
                if (txtName != null) txtName.setText(currentUser.getName() + " " + currentUser.getSurname());
                listView.setAdapter(new GroupsAdapter(currentUser.getGroups(), getApplicationContext()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}