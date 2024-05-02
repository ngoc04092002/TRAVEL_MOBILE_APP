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

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.SaveItemModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsFragment extends Fragment implements View.OnClickListener {
    private RecyclerView myPostRv;
    private CircleImageView btnBack;
    private ArrayList<PostModel> postList;
    private FirebaseFirestore db;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    public static MyPostsFragment newInstance(String param1, String param2) {
        MyPostsFragment fragment = new MyPostsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {



        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        myPostRv = view.findViewById(R.id.save_itemsRv);
        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList, getContext(), requireActivity().getSupportFragmentManager(), getActivity(), db);
        setPostListData(postAdapter);
        myPostRv.setHasFixedSize(true);
        myPostRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        myPostRv.setAdapter(postAdapter);

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        return view;

    }

    private void setPostListData(PostAdapter postAdapter) {
        UserModel user = SharedPreferencesManager.readUserInfo();

        db.collection("posts")
                .whereEqualTo("postedBy", user.getId())
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostModel postModel = document.toObject(PostModel.class);
                            postList.add(postModel);
                        }
//                        shimmerFrameLayout.showShimmer(false);
//                        shimmerFrameLayout.setVisibility(View.GONE);
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("record", "Error getting documents: ", task.getException());
                    }
                });
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
}