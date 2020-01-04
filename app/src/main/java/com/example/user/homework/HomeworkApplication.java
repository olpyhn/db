package com.example.user.homework;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.user.homework.listeners.GroupListener;
import com.example.user.homework.listeners.UserListener;
import com.example.user.homework.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeworkApplication extends Application implements ValueEventListener {
    @Nullable
    private User user;
    @Nullable
    private List<String> groupsId;
    @NonNull
    private final List<UserListener> userListeners = new ArrayList<>();
    @NonNull
    private final List<GroupListener> groupsListeners = new ArrayList<>();

    public void addUserListener(@NonNull final UserListener userListener) {
        userListeners.add(userListener);
        if (user != null) {
            userListener.onUserUpdate(user);
        }
    }

    public void addGroupListener(@NonNull final GroupListener groupListener) {
        groupsListeners.add(groupListener);
        if (groupsId != null) {
            groupListener.onUpdate(groupsId);
        }
    }

    private void updateUser(DataSnapshot dataSnapshot) {
        if (user == null) {
            user = new User();
        }
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            switch (child.getKey()) {
                case "createCount":
                    user.setCreateCount(child.getValue(Integer.class));
                    break;
                case "surname":
                    user.setSurname((String) child.getValue());
                    break;
                case "name":
                    user.setName((String) child.getValue());
                    break;
                case "email":
                    user.setEmail((String) child.getValue());
                    break;
                case "groups":
                    user.setGroups((List<String>) child.getValue());
            }
        }
        for (final UserListener listener: userListeners) {
            listener.onUserUpdate(user);
        }
    }

    private void updateGroups(DataSnapshot dataSnapshot) {
        groupsId = (List<String>) dataSnapshot.getValue();
        for (final GroupListener listener: groupsListeners) {
            listener.onUpdate(groupsId);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                return;
            }
            final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference userReference = rootReference.child("users").child(currentUser.getUid()).child("userInformation");
            final DatabaseReference groupsReference = rootReference.child("groups");
            groupsReference.addValueEventListener(this);
            userReference.addValueEventListener(this);
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("userInformation")) {
            updateUser(dataSnapshot);
        } else {
            updateGroups(dataSnapshot);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
