package com.example.travel_mobile_app.Adapter;

import android.os.Bundle;
import  android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.DetailInfor;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.Location;

import java.util.List;

public class SlideAdapter extends PagerAdapter {

    private List<Location> mListLocation;

    public SlideAdapter(List<Location> mListLocation) {
        this.mListLocation = mListLocation;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from((container).getContext()).inflate(R.layout.itemsuggestion, container, false);
        ImageView imgloc = view.findViewById(R.id.imgsug);
        TextView nameloc = view.findViewById(R.id.namesug);
        TextView addloc = view.findViewById(R.id.addsug);
        ImageButton disloc = view.findViewById(R.id.disbtn);
        Location loc = mListLocation.get(position);
        Glide.with(view.getContext())
                .load(loc.getImglink()) // Đường dẫn hình ảnh từ đối tượng Location
                .placeholder(R.drawable.boy) // Hình ảnh mặc định trong khi đang tải
                .into(imgloc); // ImageView để hiển thị hình ảnh
        addloc.setText(loc.getAddress());
        nameloc.setText(loc.getName());
        disloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo Bundle để truyền dữ liệu
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
                DetailInfor detailFragment = new DetailInfor();
                detailFragment.setArguments(bundle);

                // Thay đổi Fragment trong Activity hiện tại
                FragmentTransaction transaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, detailFragment);
                transaction.addToBackStack(null); // Thêm Fragment vào back stack
                transaction.commit();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (mListLocation != null){
            return mListLocation.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}