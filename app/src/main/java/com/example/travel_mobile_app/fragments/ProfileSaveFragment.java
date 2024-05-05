package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.ProfileSaveAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.SaveItemModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSaveFragment extends Fragment implements View.OnClickListener {
    private RecyclerView saveRv;
    private CircleImageView btnBack;
    ArrayList<SaveItemModel> list;
    private FirebaseFirestore db;

    public ProfileSaveFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_save, container, false);

        saveRv = view.findViewById(R.id.save_itemsRv);
        list = new ArrayList<>();


        ProfileSaveAdapter profileSaveAdapter = new ProfileSaveAdapter(list, getContext(), requireActivity().getSupportFragmentManager());
        saveRv.setHasFixedSize(true);
        saveRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        saveRv.setAdapter(profileSaveAdapter);

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        setRVData(profileSaveAdapter);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (v.getId() == R.id.btn_back) {
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

        fragmentTransaction.commit();
    }

    private void setRVData(ProfileSaveAdapter profileSaveAdapter) {
        CollectionReference postsRef = db.collection("save_posts");

        UserModel user = SharedPreferencesManager.readUserInfo();
        List<String> following = user.getFollowing();
        System.out.println("UserID: " + user.getId());
        //fix
        postsRef
                .whereEqualTo("savedBy", user.getId())
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        System.out.println("UserID: " + task.getResult().size());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SaveItemModel saveItemModel = document.toObject(SaveItemModel.class);
                            list.add(saveItemModel);
                        }

                        profileSaveAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("record", "Error getting documents: ", task.getException());
                    }
                });
    }
}