

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel_mobile_app.Adapter.AccountFollowAdapter;
import com.example.travel_mobile_app.Adapter.FollowAdapter;
import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.ProfileSaveAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;
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

public class ListFollowingFragment extends Fragment implements View.OnClickListener {
    private RecyclerView saveRv;
    private CircleImageView btnBack;
    private TextView tvEmpty;
    ArrayList<FollowDTO> list;
    private FirebaseFirestore db;
    AccountFollowAdapter followAdapter;

    public ListFollowingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_list_following, container, false);
        tvEmpty = view.findViewById(R.id.not_found);
        tvEmpty.setVisibility(View.GONE);

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        saveRv = view.findViewById(R.id.save_itemsRv);
        list = new ArrayList<>();

        UserModel user = SharedPreferencesManager.readUserInfo();

        list = new ArrayList<>();
        final boolean[] isFollow = {false};
        followAdapter = new AccountFollowAdapter(list, getContext(), true, db, requireActivity().getSupportFragmentManager());
        saveRv.setHasFixedSize(true);
        saveRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        saveRv.setAdapter(followAdapter);

        getFollowingData(followAdapter, user.getId());


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

    public void getFollowingData(AccountFollowAdapter followAdapter, String userId) {
        CollectionReference users = db.collection("users");

        users.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                getAllFollowingUser(userModel, users, followAdapter);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(unused -> {
            System.out.println("CHECK_ERROR::" + unused.getMessage());
        });
    }

    private void getAllFollowingUser(UserModel userModel, CollectionReference users, AccountFollowAdapter followAdapter) {
        if (userModel == null) {
            Toast.makeText(getContext(), "Người dùng không tồn tại!",
                    Toast.LENGTH_SHORT).show();
            //not found
            return;
        }

        if (!userModel.getFollowing().isEmpty()) {
            users.whereIn("id", userModel.getFollowing())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel model = document.toObject(UserModel.class);
                                list.add(new FollowDTO(model.getId(), model.getAvatarURL(), model.getFullName(), model.getFollowers().size()));
                            }
                            followAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("ERROR::", "Error getting documents: ", task.getException());
                        }

                    }).addOnFailureListener(
                            unused ->
                            {

                            });
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

    }
}