package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.travel_mobile_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout btnSaved, btnSetting, btnManagePost, btnLogout;
    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        btnSaved = view.findViewById(R.id.btnSaved);
        btnSaved.setOnClickListener(this);

        btnSetting = view.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);

        btnManagePost = view.findViewById(R.id.btnManagePost);
        btnManagePost.setOnClickListener(this);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.btnSaved){
//            fragmentTransaction.replace(R.id.container, new FriendsFragment());
        }
        else if(v.getId()==R.id.btnSetting){
            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        else if(v.getId()==R.id.btnManagePost){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        else if(v.getId()==R.id.btnLogout){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }

        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack("account_fragment");

        // Commit transaction
        fragmentTransaction.commit();
    }
}