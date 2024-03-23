package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.DashboardRvBinding;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.PostModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;

    FragmentManager fragmentManager;
    Activity activity;
    private FirebaseFirestore db;

    public PostAdapter(ArrayList<PostModel> list, Context context, FragmentManager fragmentManager, Activity activity) {
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    public PostAdapter(ArrayList<PostModel> list, Context context, FragmentManager fragmentManager, Activity activity, FirebaseFirestore db) {
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
        this.db = db;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv, parent, false);

        return new viewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel post = list.get(position);

        //load image
        if (post.getPostImage() == null) {
            Glide.with(context).clear(holder.binding.postimg);
            holder.binding.postimg.setImageURI(null);
            holder.binding.postimg.setVisibility(View.GONE);

        } else {
            Glide.with(context)
                 .load(Uri.parse(post.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.binding.postimg);
        }

        final boolean[] isLike = {false};
        getLikeInfo(holder.binding.like, isLike, post.getPostId());
        holder.binding.like.setOnClickListener(v -> {
            toggleBtnLike(post, isLike);
        });
        if (post.getLikes() == null || post.getLikes().size() == 0) {
            holder.binding.like.setText("");
        } else {
            holder.binding.like.setText(String.valueOf(post.getLikes().size()));
        }
        holder.binding.des.setText(post.getPostDescription());
        holder.binding.comment.setOnClickListener(v -> {
            showBottomDialog(post, isLike);
        });
        if (post.getComments() == null || post.getComments().size() == 0) {
            holder.binding.comment.setText("");
        } else {
            holder.binding.comment.setText(String.valueOf(post.getComments().size()));
        }

        if (post.getShare() == null || post.getShare().size() == 0) {
            holder.binding.share.setText("");
        } else {
            holder.binding.share.setText(String.valueOf(post.getShare().size()));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FragmentManager fragmentManager;
        DashboardRvBinding binding;

        public viewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            binding = DashboardRvBinding.bind(itemView);
            this.fragmentManager = fragmentManager;

            binding.profileImage.setOnClickListener(this);
            binding.postUserName.setOnClickListener(this);
            binding.postimg.setOnClickListener(v -> {
                showCenterDialog(binding.postimg.getDrawable());
            });
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.profile_image || v.getId() == R.id.post_user_name) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment(1));
                // Thêm transaction vào back stack (nếu cần)
                fragmentTransaction.addToBackStack("userDetailInfo_fragment");

                // Commit transaction
                fragmentTransaction.commit();
            }

        }
    }

    private void toggleBtnLike(PostModel post, final boolean[] isLike) {
        //fix 8c89d98007c54f34b44f2f619a8684b3 is userID
        CollectionReference posts = db.collection("posts");
        List<String> likes;
        if (isLike[0]) {
            //pull
            likes = post.getLikes();
            if (likes != null) {
                likes.remove("8c89d98007c54f34b44f2f619a8684b3");
            }
        } else {
            //push
            likes = new ArrayList<>();
            if (post.getLikes() != null) {
                likes.addAll(post.getLikes());
            }
            likes.add("8c89d98007c54f34b44f2f619a8684b3");
        }
        isLike[0] = !isLike[0];
        if (likes != null) {
            post.setLikes(likes);
            posts.document(post.getPostId()).set(post);
        }

        notifyDataSetChanged();
    }

    private void getLikeInfo(MaterialButton button, final boolean[] isLike, String postId) {
        //fix 8c89d98007c54f34b44f2f619a8684b3 is userID
        CollectionReference posts = db.collection("posts");
        posts.whereArrayContains("likes", "8c89d98007c54f34b44f2f619a8684b3").whereEqualTo("postId", postId)
             .get().addOnSuccessListener(documentSnapshots -> {
                 if (documentSnapshots.getDocuments().size() != 0) {
                     isLike[0] = true;
                     int tintColor = ContextCompat.getColor(context, R.color.yellow);
                     button.setIconTint(ColorStateList.valueOf(tintColor));
                     button.setIconResource(R.drawable.favorite_fill);
                 } else {
                     isLike[0] = false;
                     int tintColor = ContextCompat.getColor(context, R.color.black);
                     button.setIconTint(ColorStateList.valueOf(tintColor));
                     button.setIconResource(R.drawable.favorite);
                 }
             });
    }

    //comments UI
    private void showBottomDialog(PostModel post, final boolean[] isLike) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        View bottomSheetView = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setPeekHeight(bottomSheetView.getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        ImageButton bottomsheet_back = dialog.findViewById(R.id.bottomsheet_back);
        MaterialButton btnSend = dialog.findViewById(R.id.btn_chat_send);

        //handle click btn like
        MaterialButton btnLike = dialog.findViewById(R.id.like);
        int cntLike = post.getLikes() != null ? post.getLikes().size() : 0;
        refreshBtnLikeDialog(btnLike, isLike, post, cntLike);
        btnLike.setOnClickListener(v -> {
            toggleBtnLike(post, isLike);
            refreshBtnLikeDialog(btnLike, isLike, post, post.getLikes().size());
        });


        //handle comments
        EditText commentEditText = dialog.findViewById(R.id.comment_message);
        commentEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                commentEditText.postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);
                }, 200);
            }
        });

        ArrayList<CommentModel> comments;
        comments = new ArrayList<>();

        //fix 8c89d98007c54f34b44f2f619a8684b3 is userID and handle date
        CollectionReference posts = db.collection("posts");

        btnSend.setOnClickListener(v -> {
            String msg = commentEditText.getText().toString().trim();
            if (msg.equals("")) return;
            String commentId = UUID.randomUUID().toString().replace("-", "");
            CommentModel comment = new CommentModel(commentId, R.drawable.avatar_men, "8c89d98007c54f34b44f2f619a8684b3", msg, new Date().getTime());
            List<CommentModel> commentModelList = new ArrayList<>();
            if (post.getComments() != null) {
                commentModelList.addAll(post.getComments());
            }
            commentModelList.add(comment);
            post.setComments(commentModelList);
            posts.document(post.getPostId()).set(post).addOnSuccessListener(unused -> {
                commentEditText.setText("");
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Mạng kém", Toast.LENGTH_SHORT).show();
            });
        });

        RecyclerView commentsRv = dialog.findViewById(R.id.commentsRv);

        CommentAdapter commentAdapter = new CommentAdapter(comments, dialog.getContext(), db, post);
        commentsRv.setHasFixedSize(true);
        commentsRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        commentsRv.setAdapter(commentAdapter);

        //listening event change
        Query query = db.collection("posts").whereEqualTo("postId", post.getPostId());
        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.e("ERROR", "Listen failed.", e);
                Toast.makeText(context, "Mạng kém", Toast.LENGTH_SHORT).show();
                return;
            }
            // Convert query snapshot to a list of chats
            List<PostModel> resPosts = value.toObjects(PostModel.class);
            List<CommentModel> resComments = resPosts.get(0).getComments();
            if (resComments != null) {
                comments.clear();
                comments.addAll(resComments);
                commentAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        bottomsheet_back.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void refreshBtnLikeDialog(MaterialButton btnLike, final boolean[] isLike, PostModel post, int cnt) {

        if (isLike[0]) {
            int tintColor = ContextCompat.getColor(context, R.color.yellow);
            btnLike.setIconTint(ColorStateList.valueOf(tintColor));
            btnLike.setIconResource(R.drawable.favorite_fill);
        } else {
            int tintColor = ContextCompat.getColor(context, R.color.black);
            btnLike.setIconTint(ColorStateList.valueOf(tintColor));
            btnLike.setIconResource(R.drawable.favorite);
        }
        btnLike.setText(String.valueOf(cnt));
    }

    private ImageView imageView;
    private float x, y, dx, dy, initX, initY, limitCoordinatesY1, limitCoordinatesY2, limitCoordinatesX;

    //hieenr thị ảnh lên toàn màn hình khi click vào ảnh bài post
    private void showCenterDialog(Drawable drawable) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centersheet_image);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        imageView = dialog.findViewById(R.id.postimg);
        imageView.setImageDrawable(drawable);
        handleOnTouchImageOfPost(dialog, imageView);

        ImageButton btnCloseDialog = dialog.findViewById(R.id.btnClose_dialog);
        btnCloseDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleOnTouchImageOfPost(Dialog dialog, ImageView imageView) {
        // Đặt độ mờ ban đầu là 0
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 1;
        dialog.getWindow().setAttributes(layoutParams);


        imageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    initX = imageView.getX();
                    initY = imageView.getY();
                    limitCoordinatesY1 = -imageView.getHeight() / 2;
                    limitCoordinatesY2 = initY + imageView.getHeight() / 2;
                    limitCoordinatesX = imageView.getWidth() / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getRawX() - x;
                    dy = event.getRawY() - y;

                    float edgeX = imageView.getX() + dx;
                    float edgeY = imageView.getY() + dy;
                    float acreage1 = (Math.abs(edgeY) - initY + (imageView.getHeight() / 2)) * (Math.abs(edgeX) + (imageView.getWidth() / 2));
                    float acreage2 = limitCoordinatesX * 2 * (limitCoordinatesY2 - limitCoordinatesY1);
                    // Áp dụng độ mờ cho dialog
                    layoutParams.dimAmount = (acreage2 - acreage1) / acreage2;
                    dialog.getWindow().setAttributes(layoutParams);

                    imageView.setX(edgeX);
                    imageView.setY(edgeY);
                    x = event.getRawX();
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    if (Math.abs(imageView.getX()) >= limitCoordinatesX || imageView.getY() >= limitCoordinatesY2 || imageView.getY() <= limitCoordinatesY1) {
                        dialog.dismiss();
                    } else {
                        imageView.setX(initX);
                        imageView.setY(initY);
                        layoutParams.dimAmount = 1;
                        dialog.getWindow().setAttributes(layoutParams);
                    }
                    break;
            }
            return true;
        });

    }

}

