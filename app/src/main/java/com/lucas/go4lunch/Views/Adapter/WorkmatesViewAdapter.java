package com.lucas.go4lunch.Views.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Views.WorkmatesViewViewHolder;

import androidx.annotation.NonNull;

public class WorkmatesViewAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewViewHolder> {

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
        holder.updateWhitWorkmates(user);
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
}
