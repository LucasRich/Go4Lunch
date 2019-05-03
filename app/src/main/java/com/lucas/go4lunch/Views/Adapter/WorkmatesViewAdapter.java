package com.lucas.go4lunch.Views.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Views.WorkmatesViewViewHolder;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkmatesViewAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewViewHolder> {

    String uid;
    FirebaseAuth auth;

    public interface Listener {
        void onDataChanged();
    }

    private Listener callback;

    public WorkmatesViewAdapter(@NonNull FirestoreRecyclerOptions<User> options, Listener callback) {
        super(options);
        this.callback = callback;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewViewHolder holder, int position, @NonNull User user) {
        DocumentSnapshot snapshot =  getSnapshots().getSnapshot(position);

        holder.updateWhitWorkmates(user);
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();

        if(this.getCurrentUser().getUid().equals(snapshot.getId())) {
            param.height = 0;
        } else {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
    }

    @NonNull
    @Override
    public WorkmatesViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item, parent, false));
    }

    @NonNull
    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
}
