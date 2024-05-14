package com.example.travel_mobile_app.Adapter;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.DetailInfor;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.fragments.AllLocationFragment;
import com.example.travel_mobile_app.fragments.EditLocationFragment;
import com.example.travel_mobile_app.models.Location;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.AccessControlContext;
import java.util.List;

public class AllLocationAdapter extends RecyclerView.Adapter<AllLocationAdapter.SuggestionViewHolder> {
    private AccessControlContext mContext;
    private List<Location> mListLocation;

    public AllLocationAdapter(AllLocationFragment mContext) {
        this.mContext = getContext();
    }

    public void setData(List<Location> list){
        this.mListLocation = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.all_location_item, parent, false) ;

        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Location loc = mListLocation.get(position);
        if (loc == null){
            return;
        }
        Glide.with(holder.itemView.getContext())
                .load(loc.getImglink()) // Đường dẫn hình ảnh từ đối tượng Location
                .placeholder(R.drawable.boy) // Hình ảnh mặc định trong khi đang tải
                .into(holder.imglocationiv); // ImageView để hiển thị hình ảnh

        holder.namelocationtv.setText(loc.getName());
        holder.addresslocationtv.setText(loc.getAddress());
        holder.deletelocbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("locations").document(loc.getId());
                documentReference.delete();
                AllLocationFragment frag = new AllLocationFragment();
                FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, frag);
                transaction.addToBackStack(null); // Thêm Fragment vào back stack
                transaction.commit();
            }
        });
        holder.editlocbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("location_id", loc.getId());
                bundle.putString("location_name", loc.getName());
                bundle.putString("location_address", loc.getAddress());
                bundle.putString("location_intro", loc.getIntroduce());
                bundle.putString("location_imglink", loc.getImglink());
                bundle.putString("location_number", loc.getNumber());
                bundle.putString("location_price", loc.getPrice());
                bundle.putLong("location_opentime", loc.getOpentime());
                bundle.putLong("location_closetime", loc.getClosetime());
                // Thêm các dữ liệu khác nếu cần

                // Mở Fragment chi tiết và truyền dữ liệu
                EditLocationFragment frag = new EditLocationFragment();
                frag.setArguments(bundle);

                // Thay đổi Fragment trong Activity hiện tại
                FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, frag);
                transaction.addToBackStack(null); // Thêm Fragment vào back stack
                transaction.commit();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo Bundle để truyền dữ liệu
                Bundle bundle = new Bundle();
                bundle.putString("location_name", loc.getName());
                bundle.putString("location_address", loc.getAddress());
                bundle.putString("location_intro", loc.getIntroduce());
                bundle.putString("location_imglink", loc.getImglink());
                bundle.putString("location_number", loc.getNumber());
                bundle.putString("location_price", loc.getPrice());
                bundle.putLong("location_opentime", loc.getOpentime());
                bundle.putLong("location_closetime", loc.getClosetime());
                // Thêm các dữ liệu khác nếu cần

                // Mở Fragment chi tiết và truyền dữ liệu
                DetailInfor detailFragment = new DetailInfor();
                detailFragment.setArguments(bundle);

                // Thay đổi Fragment trong Activity hiện tại
                FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, detailFragment);
                transaction.addToBackStack(null); // Thêm Fragment vào back stack
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListLocation != null){
            return  mListLocation.size();
        }
        return 0;
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imglocationiv;
        private TextView namelocationtv;
        private TextView addresslocationtv;
        private Button deletelocbtn, editlocbtn;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            addresslocationtv =itemView.findViewById(R.id.addresslocation);
            imglocationiv = itemView.findViewById(R.id.imglocation);
            namelocationtv = itemView.findViewById(R.id.namelocation);
            deletelocbtn =itemView.findViewById(R.id.deletelocbtn);
            editlocbtn=itemView.findViewById(R.id.editlocbtn);

        }
    }
}
