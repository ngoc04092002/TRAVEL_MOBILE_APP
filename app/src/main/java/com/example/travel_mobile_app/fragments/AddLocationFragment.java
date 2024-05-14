package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.Location;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText addNameLoc, addAddressLoc, addIntroLoc, addNumberLoc, addPriceLoc, addOpenTimeLoc, addCloseTimeLoc, addImgLinkLoc;
    private Button saveButton;
    private boolean isForOpenTime;

    private FirebaseFirestore db;

    public AddLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddLocationFragment newInstance(String param1, String param2) {
        AddLocationFragment fragment = new AddLocationFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_add_location, container, false);
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các thành phần trong layout
        addNameLoc = rootView.findViewById(R.id.addnameloc);
        addAddressLoc = rootView.findViewById(R.id.addaddressloc);
        addIntroLoc = rootView.findViewById(R.id.addintroloc);
        addNumberLoc = rootView.findViewById(R.id.addnumberloc);
        addPriceLoc = rootView.findViewById(R.id.addpriceloc);
        addOpenTimeLoc = rootView.findViewById(R.id.addopentimeloc);
        addCloseTimeLoc = rootView.findViewById(R.id.addclosetimeloc);
        addImgLinkLoc = rootView.findViewById(R.id.addimglinkloc);
        saveButton = rootView.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveLocationToFirebase();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return rootView;

    }
    private void saveLocationToFirebase() throws ParseException {
        String name = addNameLoc.getText().toString().trim();
        String address = addAddressLoc.getText().toString().trim();
        String intro = addIntroLoc.getText().toString().trim();
        String number = addNumberLoc.getText().toString().trim();
        String price = addPriceLoc.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();

        // Đặt giờ, phút, giây thành 0
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Chuyển đổi thành kiểu long
        long millisOfDay = calendar.getTimeInMillis();
        String formattedopenTime = addOpenTimeLoc.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date openTimedate = sdf.parse(formattedopenTime);
        long openTime = openTimedate.getTime() + millisOfDay;
        String formattedcloseTime = addCloseTimeLoc.getText().toString().trim();
        Date closeTimedate = sdf.parse(formattedcloseTime);
        long closeTime = closeTimedate.getTime() + millisOfDay;
        String imgLink = addImgLinkLoc.getText().toString().trim();

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
        CollectionReference locationsRef = db.collection("locations");
        locationsRef.document().set(locationData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thành công
                        Toast.makeText(getContext(), "Đã lưu địa danh!", Toast.LENGTH_SHORT).show();
                        // Xóa nội dung trong các EditText sau khi lưu thành công
                        addNameLoc.setText("");
                        addAddressLoc.setText("");
                        addIntroLoc.setText("");
                        addNumberLoc.setText("");
                        addPriceLoc.setText("");
                        addOpenTimeLoc.setText("");
                        addCloseTimeLoc.setText("");
                        addImgLinkLoc.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lỗi
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}