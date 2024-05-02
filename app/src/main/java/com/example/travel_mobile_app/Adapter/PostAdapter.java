package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
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
import com.example.travel_mobile_app.dto.UserToken;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.services.MyFirebaseMessagingService;
import com.example.travel_mobile_app.utils.CustomDateTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;
    FragmentManager fragmentManager;
    Activity activity;
    private FirebaseFirestore db;
    private MaterialButton btnLike, btnComment, btnShare;

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

        LinearLayout container = holder.binding.container;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) container.getLayoutParams();
        if (post.getShare() != null && post.getShare().equals(true)) {
            holder.binding.topbarShare.setVisibility(View.VISIBLE);
            int desiredWidthInDp = 380;
            float scale = context.getResources().getDisplayMetrics().density;
            int desiredWidthInPx = (int) (desiredWidthInDp * scale + 0.5f);
            layoutParams.width = desiredWidthInPx;
            holder.binding.more.setVisibility(View.GONE);
            holder.binding.moreShare.setVisibility(View.VISIBLE);
            holder.binding.btnsShare.setVisibility(View.VISIBLE);
            holder.binding.btns.setVisibility(View.GONE);
            holder.binding.usernameShare.setText(post.getShareBy());
            holder.binding.timestamp.setText(CustomDateTime.formatDate(post.getShareAt()));
            btnLike = holder.binding.likeShare;
            btnComment = holder.binding.commentShare;
            btnShare = holder.binding.shareShare;
        } else {
            holder.binding.topbarShare.setVisibility(View.GONE);
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            holder.binding.more.setVisibility(View.VISIBLE);
            holder.binding.moreShare.setVisibility(View.GONE);
            holder.binding.btnsShare.setVisibility(View.GONE);
            holder.binding.btns.setVisibility(View.VISIBLE);
            holder.binding.username.setText(post.getPostedBy());
            holder.binding.timestamp.setText(CustomDateTime.formatDate(post.getPostedAt()));
            btnLike = holder.binding.like;
            btnComment = holder.binding.comment;
            btnShare = holder.binding.share;

        }
        container.setLayoutParams(layoutParams);

        final boolean[] isLike = {false};
        getLikeInfo(btnLike, isLike, post.getPostId());
        btnLike.setOnClickListener(v -> {
            toggleBtnLike(post, isLike);
        });
        if (post.getLikes() == null || post.getLikes().size() == 0) {
            btnLike.setText("");
        } else {
            btnLike.setText(String.valueOf(post.getLikes().size()));
        }
        holder.binding.des.setText(post.getPostDescription());
        btnComment.setOnClickListener(v -> {
            showBottomDialog(post, isLike);
        });
        if (post.getComments() == null || post.getComments().size() == 0) {
            btnComment.setText("");
        } else {
            btnComment.setText(String.valueOf(post.getComments().size()));
        }

        btnShare.setOnClickListener(v -> {
            showDialogShare(post);
        });

//        if (post.getShare() == null || post.getShare().size() == 0) {
//            holder.binding.share.setText("");
//        } else {
//            holder.binding.share.setText(String.valueOf(post.getShare().size()));
//        }


        //See detail info
        holder.binding.postUserName.setOnClickListener(v->{
            seeInfoDetail(post);
        });
        holder.binding.postUserNameShare.setOnClickListener(v->{
            seeInfoDetail(post);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        FragmentManager fragmentManager;
        DashboardRvBinding binding;

        public viewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            binding = DashboardRvBinding.bind(itemView);
            this.fragmentManager = fragmentManager;

            binding.postimg.setOnClickListener(v -> {
                showCenterDialog(binding.postimg.getDrawable());
            });
        }

    }

    private void seeInfoDetail(PostModel post){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment(post.getPostedBy()));
        fragmentTransaction.addToBackStack("userDetailInfo_fragment");
        // Commit transaction
        fragmentTransaction.commit();
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
            addNotification(post, "like");
            sendNotification(post, "like");
        }
        isLike[0] = !isLike[0];
        if (likes != null) {
            post.setLikes(likes);
            posts.document(post.getPostId()).set(post);
        }
        notifyDataSetChanged();
    }

    private void addNotification(PostModel post, String type) {
        //fix 8c89d98007c54f34b44f2f619a8684b3 is userID
        NotificationModel notification = new NotificationModel();
        notification.setNotificationBy("8c89d98007c54f34b44f2f619a8684b3");
        notification.setNotificationAt(new Date().getTime());
        notification.setPostId(post.getPostId());
        notification.setPostedBy(post.getPostedBy());
        notification.setType(type);

        CollectionReference notifications = db.collection("notifications");
        notifications.add(notification);
    }

    private void sendNotification(PostModel post, String type) {
        //fix userid
        String userId = "8c89d98007c54f34b44f2f619a8684b3";
        HashMap<String, String> conent = new HashMap<>();
        if (type.equals("like")) {
            conent.put("0", "Bài đăng");
            conent.put("1", userId + " Thích bài đăng của bạn");
        } else if (type.equals("comment")) {
            conent.put("0", "Bài đăng");
            conent.put("1", userId + " Bình luận bài đăng của bạn");
        }

        FirebaseFirestore.getInstance().collection("tokens")
                         .whereEqualTo("userId", post.getPostedBy())
                         .get()
                         .addOnCompleteListener(task -> {
                             if (task.isSuccessful()) {
                                 for (QueryDocumentSnapshot document : task.getResult()) {
                                     UserToken userToken = document.toObject(UserToken.class);
                                     send(userToken.getDeviceToken(), conent.get("0"), conent.get("1"));
                                 }
                             }
                         }).addOnFailureListener(e -> {
                             Log.e("ERROR::", e.getMessage());
                         });

    }

    private void send(String deviceToken, String title, String body) {
        new Thread(() -> {
            try {
                MyFirebaseMessagingService.pushNotification(deviceToken, title, body);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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

    private void saveNewPost(CollectionReference posts, PostModel post) {

    }

    private void showDialogShare(PostModel post) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
        PostModel newPost = post;
        String postId = UUID.randomUUID().toString().replace("-", "");
        CollectionReference posts = db.collection("posts");
        dialog.findViewById(R.id.btn_share_internal).setOnClickListener(v -> {
            newPost.setShare(true);
            newPost.setShareBy("8c89d98007c54f34b44f2f619a8684b3");
            newPost.setShareAt(new Date().getTime());
            newPost.setOriginPostId(post.getPostId());
            posts.document(postId).set(newPost)
                 .addOnSuccessListener(unused -> {
                     Toast.makeText(context, "Chia sẻ thành công", Toast.LENGTH_SHORT).show();
                     dialog.dismiss();
                 }).addOnFailureListener(e -> {
                     Log.e("ERROR", "[ERROR-CREATE-POST]", e);
                     Toast.makeText(context, "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
                     dialog.dismiss();
                 });
        });


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    //comments UI
    private void showBottomDialog(PostModel post, final boolean[] isLike) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);
        dialog.setOnShowListener(dialog1 -> {
            View bottomSheetView = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
            bottomSheetBehavior.setPeekHeight(bottomSheetView.getHeight());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

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
                addNotification(post, "comment");
                sendNotification(post, "comment");
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

