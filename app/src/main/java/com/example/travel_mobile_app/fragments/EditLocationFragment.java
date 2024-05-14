package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travel_mobile_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditLocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editNameLoc, editAddressLoc, editIntroLoc, editNumberLoc, editPriceLoc, editOpenTimeLoc, editCloseTimeLoc, editImgLinkLoc;
    private Button saveedit;
    private FirebaseFirestore db;

    public EditLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditLocationFragment newInstance(String param1, String param2) {
        EditLocationFragment fragment = new EditLocationFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_location, container, false);
        Bundle bundle = getArguments();
        String id = bundle.getString("location_id");
        System.out.println(id);
        editNameLoc = view.findViewById(R.id.editnameloc);
        editAddressLoc = view.findViewById(R.id.editaddressloc);
        editIntroLoc = view.findViewById(R.id.editintroloc);
        editOpenTimeLoc = view.findViewById(R.id.editopentimeloc);
        editCloseTimeLoc = view.findViewById(R.id.editclosetimeloc);
        editNumberLoc = view.findViewById(R.id.editnumberloc);
        editPriceLoc = view.findViewById(R.id.editpriceloc);
        editImgLinkLoc = view.findViewById(R.id.editimglinkloc);
        saveedit = view.findViewById(R.id.save_edit);
        if (bundle != null) {
            String name = bundle.getString("location_name");
            String address = bundle.getString("location_address");
            String intro = bundle.getString("location_intro");
            String imglink = bundle.getString("location_imglink");
            String number = bundle.getString("location_number");
            String price = bundle.getString("location_price");
            long opentime = bundle.getLong("location_opentime");
            long closetime = bundle.getLong("location_closetime");
            Date openTime = new Date(opentime);
            SimpleDateFormat sdfopentime = new SimpleDateFormat("HH:mm:ss");
            String formattedopenTime = sdfopentime.format(openTime);
            Date closeTime = new Date(closetime);
            SimpleDateFormat sdfclosetime = new SimpleDateFormat("HH:mm:ss");
            String formattedcloseTime = sdfclosetime.format(closeTime);
            editImgLinkLoc.setText(imglink);
            editNameLoc.setText(name);
            editPriceLoc.setText(price);
            editAddressLoc.setText(address);
            editNumberLoc.setText(number);
            editIntroLoc.setText(intro);
            editOpenTimeLoc.setText(formattedopenTime);
            editCloseTimeLoc.setText(formattedcloseTime);
        }
       saveedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateLocationToFirebase();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }
    private void updateLocationToFirebase() throws ParseException {
        db = FirebaseFirestore.getInstance();
        String name = editNameLoc.getText().toString().trim();
        String address = editAddressLoc.getText().toString().trim();
        String intro = editIntroLoc.getText().toString().trim();
        String number = editNumberLoc.getText().toString().trim();
        String price = editPriceLoc.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();

        // Đặt giờ, phút, giây thành 0
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Chuyển đổi thành kiểu long
        long millisOfDay = calendar.getTimeInMillis();
        String formattedopenTime = editOpenTimeLoc.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date openTimedate = sdf.parse(formattedopenTime);
        long openTime = openTimedate.getTime() + millisOfDay;
        String formattedcloseTime = editCloseTimeLoc.getText().toString().trim();
        Date closeTimedate = sdf.parse(formattedcloseTime);
        long closeTime = closeTimedate.getTime() + millisOfDay;
        String imgLink = editImgLinkLoc.getText().toString().trim();

        // Kiểm tra xem các trường dữ liệu có trống không
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("name", name);
        locationData.put("introduce", intro);
        locationData.put("address", address);
        locationData.put("opentime", openTime);
        locationData.put("closetime", closeTime);
        locationData.put("price", price);
        locationData.put("imglink", imgLink);
        locationData.put("number", number);
        Bundle bundle = getArguments();
        String id = bundle.getString("location_id");
        System.out.println(id);
        System.out.println(locationData);
        CollectionReference locationsRef = db.collection("locations");
        locationsRef.document(id)
                .update(locationData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thành công
                        Toast.makeText(getContext(), "Đã cập nhật địa danh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lỗi
                        Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}