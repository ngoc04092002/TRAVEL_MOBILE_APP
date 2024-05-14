package com.example.travel_mobile_app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.models.Location;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.SaveItemModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailInfor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailInfor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton btnback;
    private ImageButton btndirect, btnevent, btnsave;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private TextView nametv, introducetv, addresstv, opentimetv, pricetv;
    private ImageView imgv;
    private String saveLocationID;

    public DetailInfor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailInfor.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailInfor newInstance(String param1, String param2) {
        DetailInfor fragment = new DetailInfor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_infor, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = bundle.getString("location_id");
            String name = bundle.getString("location_name");
            String address = bundle.getString("location_address");
            String intro = bundle.getString("location_intro");
            String imglink = bundle.getString("location_imglink");
            String number = bundle.getString("location_number");
            String price = bundle.getString("location_price");
            long opentime = bundle.getLong("location_opentime");
            long closetime = bundle.getLong("location_closetime");

            // Hiển thị thông tin chi tiết trong TextView hoặc các phần tử khác trong Fragment
            TextView nameTextView = view.findViewById(R.id.tenditich);
            TextView addressTextView = view.findViewById(R.id.diachi);
            TextView introTextView = view.findViewById(R.id.gioithieuchitiet);
            TextView priceTextView = view.findViewById(R.id.giave);
            TextView opentimeTextView = view.findViewById(R.id.giomocua);
            TextView closetimeTextView = view.findViewById(R.id.giodongcua);
            ImageView loadimg = view.findViewById(R.id.anhditich);
            TextView numbertv = view.findViewById(R.id.sdttv);
            // Lấy giá trị opentime từ locationData
            System.out.println(opentime);

            Date openTime = new Date(opentime);
            SimpleDateFormat sdfopentime = new SimpleDateFormat("HH:mm:ss");

            String formattedopenTime = sdfopentime.format(openTime);
            Date closeTime = new Date(closetime);
            SimpleDateFormat sdfclosetime = new SimpleDateFormat("HH:mm:ss");
            String formattedcloseTime = sdfclosetime.format(closeTime);

            nameTextView.setText(name);
            addressTextView.setText(address);
            introTextView.setText(intro);
            priceTextView.setText(price);
            opentimeTextView.setText(formattedopenTime);
            closetimeTextView.setText(formattedcloseTime);
            numbertv.setText(number);
            Glide.with(loadimg).load(imglink).into(loadimg);
            btndirect = view.findViewById(R.id.directbtn);
            btndirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Directions directFragment = new Directions();
                    directFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, directFragment);
                    transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                    transaction.commit();
                }
            });

            db = FirebaseFirestore.getInstance();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            btnsave = view.findViewById(R.id.savebtn);
            btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(saveLocationID == null ) {
                        String saveId = UUID.randomUUID().toString().replace("-", "");
                        String userId = SharedPreferencesManager.readUserInfo().getId();

                        SaveItemModel itemModel = new SaveItemModel(saveId, id, userId, name, new Date().getTime(), imglink, 1);

                        saveLocation(itemModel);
                    } else {
                        unsaveLocation();
                    }
                }
            });
            checkSavedLocation(id);
        }

        btnback = view.findViewById(R.id.backbtn3);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();

            }
        });


        return view;}

    private void saveLocation(SaveItemModel itemModel) {
        CollectionReference posts = db.collection("save_posts");
        posts.document(itemModel.getId())
                .set(itemModel)
                .addOnSuccessListener(unused -> {
                    saveLocationID = itemModel.getId();
                    Toast.makeText(getContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    btnsave.setImageResource(R.drawable.icn_unsave);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                });
    }

    public void unsaveLocation() {
        // Xóa tài liệu từ Firestore
        db.collection("save_posts").document(saveLocationID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveLocationID = null;
                        btnsave.setImageResource(R.drawable.savebtn);
                        Toast.makeText(getContext(), "Bỏ lưu thành công!", Toast.LENGTH_SHORT).show();

                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xóa tài liệu thất bại
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }


    private void checkSavedLocation(String locationID) {
        System.out.println("locationID: " + locationID);
        db.collection("save_posts").whereEqualTo("postID", locationID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Kiểm tra nếu có bất kỳ document nào thỏa mãn điều kiện truy vấn
                        if (!querySnapshot.isEmpty()) {
                            saveLocationID = querySnapshot.getDocuments().get(0).getId();
                            btnsave.setImageResource(R.drawable.icn_unsave);
                        } else {
                            // Không có tài liệu nào trong collection thỏa mãn điều kiện truy vấn
                            btnsave.setImageResource(R.drawable.savebtn);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi khi thực hiện truy vấn
                        System.err.println("Lỗi: " + e.getMessage());
                    }
                });
    }

    private void saveLocation() {
        if (currentUser == null) {
            // Người dùng chưa đăng nhập, bạn có thể yêu cầu họ đăng nhập hoặc xử lý tùy ý
            Toast.makeText(getActivity(), "Vui lòng đăng nhập trước khi lưu địa điểm!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userID hiện tại
        String userId = currentUser.getUid();
        Bundle bundle = getArguments();
        String id = bundle.getString("location_id");
        System.out.println(id);

        // Lấy locationID hiện tại (đây là ví dụ, bạn cần thay thế thành cách bạn lấy locationID)
        String locationId = id;

        // Lấy thời gian hiện tại
        long currentTime = System.currentTimeMillis();

        System.out.println(currentTime);
        Date currentDate = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = sdf.format(currentDate);

        // Hiển thị chuỗi đã được chuyển đổi
        Log.d("Current Date", formattedDate);

        // Tạo dữ liệu cho document
        Map<String, Object> userLocationData = new HashMap<>();
        userLocationData.put("userid", userId);
        userLocationData.put("locationid", locationId);
        userLocationData.put("createdAt", currentTime);

        // Kiểm tra xem có document nào có userid và locationid giống hiện tại hay không
        db.collection("user_location")
                .whereEqualTo("userid", userId)
                .whereEqualTo("locationid", locationId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Nếu có document, xóa nó đi
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    db.collection("user_location").document(document.getId()).delete();
                                    Toast.makeText(getActivity(), "Đã bỏ theo dõi địa điểm!", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                            // Lưu document mới
                            db.collection("user_location")
                                    .add(userLocationData)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Đã lưu địa điểm để theo dõi!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Đã xảy ra lỗi khi lưu địa điểm!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });}
                        } else {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi khi kiểm tra trùng lặp!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}



