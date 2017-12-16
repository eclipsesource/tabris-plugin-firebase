package com.eclipsesource.firebase.reference;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eclipsesource.tabris.android.RemoteObject;
import com.eclipsesource.tabris.android.TabrisActivity;
import com.eclipsesource.tabris.android.TabrisContext;
import com.eclipsesource.tabris.android.internal.toolkit.AppState;
import com.eclipsesource.tabris.android.internal.toolkit.IAppStateListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


class Reference { //Represents DatabaseReference... or should it represent FirebaseDatabase ?
    private DatabaseReference database;

    private final Activity activity;
    private final TabrisContext tabrisContext;
    private String path;

    Reference(Activity activity, TabrisContext context){
        this.activity = activity;
        this.tabrisContext = context;
        Log.e("Nery", "Reference created");
        if(this.activity == null){
            Log.e("ERROR", "activity es null");
        }
        if(this.tabrisContext == null){
            Log.e("ERROR", "tabrisContext es null");
        }
    }


    public void create(DatabaseReference ref){
        this.path = ref.toString();
        this.database = ref;
        registerListeners();
    }


    public Reference child(String path){
        Reference ref = new Reference(this.activity, this.tabrisContext);
        ref.create(this.database.child(path));
        return ref;
    }
    
    public Reference push(){
        Reference ref = new Reference(this.activity, this.tabrisContext);
        ref.create(this.database.push());
        return ref;
    }

    public void keepSynced(boolean keepSynced){
        this.database.keepSynced(keepSynced);
    }

    public void setValue(Object value){
        //Set the value and add a CompletitionListener
        database.setValue(value, completition);
    }

    public String getKey(){
        return this.database.getKey();
    }




    private void registerListeners(){
        database.addValueEventListener(valueListener);   

        database.addChildEventListener(childlistener);
    }
    
    private DatabaseReference.CompletionListener completition = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref){
            Log.e("Nery", "Saved");
            final RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) {
                if(error != null){
                    //... notify if there was an error.
                    remoteObject.notify("ErrorSaving", "error", error.getMessage());
                } else {
                    //... if everything OK, 
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // notify that tha value was saved.
                            remoteObject.notify("onValueSaved", "value", dataSnapshot.getValue());
                        }
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        }
    };

    private ValueEventListener valueListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            Log.e("Nery", "Changed..!");
            if(activity == null){
                Log.e("ERROR", "activity es null");
            }
            if(tabrisContext == null){
                Log.e("ERROR", "tabrisContext es null");
            }
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onDataChange", "data", dataSnapshot.getValue());
        }
        @Override public void onCancelled(DatabaseError databaseError) {}
    };
    
    private ChildEventListener childlistener = new ChildEventListener(){
        @Override
        public void onCancelled(DatabaseError error){
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onCancelled", "error", error.getMessage());
        }
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName){
            Map<String, Object> map = new HashMap();
            map.put("child", snapshot.getKey());
            map.put("value", snapshot.getValue());
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onChildAdded", map);
        }
        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName){
            Map<String, Object> map = new HashMap();
            map.put("child", snapshot.getKey());
            map.put("value", snapshot.getValue());
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onChildChanged", map);
        }
        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName){
            Map<String, Object> map = new HashMap();
            map.put("child", snapshot.getKey());
            map.put("value", snapshot.getValue());
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onChildMoved", map);
        }
        @Override
        public void onChildRemoved(DataSnapshot snapshot){
            Map<String, Object> map = new HashMap();
            map.put("child", snapshot.getKey());
            map.put("value", snapshot.getValue());
            RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Reference.this);
            if (remoteObject != null) remoteObject.notify("onChildRemoved", map);
        }
    };



    public void unregisterAllListeners() {
       this.database.removeEventListener(valueListener);
       this.database.removeEventListener(childlistener);
    }
    

}