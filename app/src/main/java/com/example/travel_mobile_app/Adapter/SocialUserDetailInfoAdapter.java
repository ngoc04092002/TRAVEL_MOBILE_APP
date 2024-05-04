package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.UserToken;
import com.example.travel_mobile_app.fragments.CreatePostFragment;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.SaveItemModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.MyFirebaseMessagingService;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.example.travel_mobile_app.utils.CustomDateTime;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SocialUserDetailInfoAdapter {

    PostModel model;
    Context context;
    FirebaseFirestore db;
    LinearLayout userInfos;
    Activity activity;
    FragmentManager fragmentManager;

    public SocialUserDetailInfoAdapter(PostModel model, Context context, FirebaseFirestore db, Activity activity, LinearLayout userInfos,FragmentManager fragmentManager) {
        this.model = model;
        this.context = context;
        this.db = db;
        this.userInfos = userInfos;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    ImageView postImage, postimg;
    TextView name, about, des;

    MaterialButton like, comment, share;
    MaterialToolbar more;
    PostModel postModel;

    public View onCreateView(@NonNull View itemView, PostModel postModel) {
        this.postModel = postModel;
        postimg = itemView.findViewById(R.id.postimg);
        postImage = itemView.findViewById(R.id.profile_image);
        name = itemView.findViewById(R.id.username);
        about = itemView.findViewById(R.id.timestamp);
        like = itemView.findViewById(R.id.like);
        comment = itemView.findViewById(R.id.comment);
        share = itemView.findViewById(R.id.share);
        des = itemView.findViewById(R.id.des);
        more = itemView.findViewById(R.id.more);

        CollectionReference users = db.collection("users");
        users.document(postModel.getPostedBy())
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     if (userModel != null && userModel.getFullName() != null) {
                         name.setText(userModel.getFullName());
                     } else {
                         name.setText("bot");
                     }

                     if (userModel != null & userModel.getAvatarURL() != null) {
                         Glide.with(context)
                              .load(Uri.parse(userModel.getAvatarURL()))
                              .centerCrop()
                              .placeholder(R.drawable.image_empty)
                              .into(postImage);
                     }
                 }
             });

        about.setText(CustomDateTime.formatDate(postModel.getPostedAt()));

        if (postModel.getLikes() != null) {
            like.setText(String.valueOf(postModel.getLikes().size()));
        }
        if (postModel.getComments() != null) {
            comment.setText(String.valueOf(postModel.getComments().size()));
        }

        CollectionReference posts = db.collection("posts");
        posts.whereEqualTo("share", true)
             .whereEqualTo("originPostId", postModel.getPostId())
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful() && task.getResult() != null) {
                     share.setText(String.valueOf(task.getResult().size()));
                 }
             });
        des.setText(postModel.getPostDescription());


        UserModel user = SharedPreferencesManager.readUserInfo();
        if (postModel.getPostImage() != null) {
            Glide.with(context)
                 .load(Uri.parse(postModel.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(postimg);
        } else {
            Glide.with(context).clear(postimg);
            postimg.setImageURI(null);
            postimg.setVisibility(View.GONE);
        }


        StringBuilder amountShare = new StringBuilder("");
        posts.whereEqualTo("share", true)
             .whereEqualTo("originPostId", postModel.getPostId())
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful() && task.getResult() != null) {
                     if (task.getResult().size() != 0) {
                         share.setText(String.valueOf(task.getResult().size()));
                         amountShare.append(task.getResult().size());
                     } else {
                         share.setText("");
                     }

                 }
             }).addOnFailureListener(e -> {
                 share.setText("");
             });


        final boolean[] isLike = {false};
        getLikeInfo(like, isLike, postModel, user.getId());
        like.setOnClickListener(v -> {
            toggleBtnLike(postModel, isLike);
        });

        comment.setOnClickListener(v -> {
            showBottomDialog(postModel, isLike, amountShare);
        });

        share.setOnClickListener(v -> {
            try {
                showDialogShare(postModel);
            } catch (CloneNotSupportedException e) {

            }
        });

        postimg.setOnClickListener(v -> {
            String url = postModel.getPostImage();
            if (url != null && url.contains("video")) {
                showCenterDialog(postModel.getPostImage());
            } else {
                showCenterDialog(postimg.getDrawable());
            }
        });

        MaterialToolbar materialToolbar = more;
        Menu menu = materialToolbar.getMenu();
        MenuItem selectDeletePost = menu.findItem(R.id.del_post);
        MenuItem selectEditPost = menu.findItem(R.id.edit_post);

        materialToolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.del_post) {
                removePost(postModel, itemView);
            } else if (itemId == R.id.save_post) {
                savePost(postModel, user.getId());
            } else if (itemId == R.id.edit_post) {
                replaceScreen(R.id.container, new CreatePostFragment(postModel.getPostId()), "social_fragment");
            }
            return true;
        });

        if (user.getId().equals(postModel.getPostedBy())) {
            selectDeletePost.setVisible(true);
            selectEditPost.setVisible(true);
        } else {
            selectDeletePost.setVisible(false);
            selectEditPost.setVisible(false);
        }

        return itemView;
    }

    private void replaceScreen(@IdRes int containerViewId, @NonNull Fragment fragment, String backTrackName) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(backTrackName);
        fragmentTransaction.commit();
    }

    private void toggleBtnLike(PostModel post, final boolean[] isLike) {
        UserModel user = SharedPreferencesManager.readUserInfo();
        CollectionReference posts = db.collection("posts");
        List<String> likes;
        if (isLike[0]) {
            //pull
            likes = post.getLikes();
            if (likes != null) {
                likes.remove(user.getId());
            }
        } else {
            //push
            likes = new ArrayList<>();
            if (post.getLikes() != null) {
                likes.addAll(post.getLikes());
            }
            likes.add(user.getId());
            addNotification(post, "like");
            sendNotification(post, "like");
        }
        isLike[0] = !isLike[0];
        if (likes != null) {
            post.setLikes(likes);
            posts.document(post.getPostId()).set(post);
        }
    }

    private void getLikeInfo(MaterialButton button, final boolean[] isLike, PostModel post, String userId) {
        if (post.getLikes() != null && post.getLikes().contains(userId)) {
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
    }

    private void showBottomDialog(PostModel post, final boolean[] isLike, CharSequence amountShare) {
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

        MaterialButton btnShare = dialog.findViewById(R.id.share);
        btnShare.setText(amountShare);

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
        UserModel user = SharedPreferencesManager.readUserInfo();

        btnSend.setOnClickListener(v -> {
            String msg = commentEditText.getText().toString().trim();
            if (msg.equals("")) return;
            String commentId = UUID.randomUUID().toString().replace("-", "");

            CommentModel comment = new CommentModel(user.getId(), commentId, user.getAvatarURL(), user.getId(), msg, new Date().getTime());
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

    private void removePost(PostModel post, View itemView) {
        CollectionReference posts = db.collection("posts");
        posts.document(post.getPostId())
             .delete()
             .addOnSuccessListener(unused -> {
                 Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                 userInfos.removeView(itemView);
             })
             .addOnFailureListener(e -> {
                 Toast.makeText(context, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
             });
    }

    private void savePost(PostModel post, String userId) {
        String savePostId = UUID.randomUUID().toString().replace("-", "");
        SaveItemModel itemModel = new SaveItemModel(savePostId, post.getPostId(), userId, post.getPostDescription(), new Date().getTime(), post.getPostImage());
        CollectionReference posts = db.collection("save_posts");
        posts.document(savePostId)
             .set(itemModel)
             .addOnSuccessListener(unused -> {
                 Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show();
             })
             .addOnFailureListener(e -> {
                 Toast.makeText(context, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
             });
    }

    private void addNotification(PostModel post, String type) {
        UserModel user = SharedPreferencesManager.readUserInfo();

        if (post.getPostedBy().equals(user.getId())) {
            return;
        }

        String notificationId = UUID.randomUUID().toString().replace("-", "");

        NotificationModel notification = new NotificationModel();
        notification.setNotificationId(notificationId);
        notification.setUserId(user.getId());
        notification.setUserImage(user.getAvatarURL());
        notification.setNotificationBy(user.getFullName());
        notification.setNotificationAt(new Date().getTime());
        notification.setPostId(post.getPostId());
        notification.setPostedBy(post.getPostedBy());
        notification.setType(type);

        CollectionReference notifications = db.collection("notifications");
        notifications.add(notification);
    }

    private void sendNotification(PostModel post, String type) {
        UserModel user = SharedPreferencesManager.readUserInfo();

        if (post.getPostedBy().equals(user.getId())) {
            return;
        }

        HashMap<String, String> conent = new HashMap<>();
        if (type.equals("like")) {
            conent.put("0", "Bài đăng");
            conent.put("1", user.getFullName() + " Thích bài đăng của bạn");
        } else if (type.equals("comment")) {
            conent.put("0", "Bài đăng");
            conent.put("1", user.getFullName() + " Bình luận bài đăng của bạn");
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


    private void showDialogShare(PostModel post) throws CloneNotSupportedException {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);

        PostModel newPost = (PostModel) post.clone();


        UserModel user = SharedPreferencesManager.readUserInfo();

        String postId = UUID.randomUUID().toString().replace("-", "");
        CollectionReference posts = db.collection("posts");
        dialog.findViewById(R.id.btn_share_internal).setOnClickListener(v -> {
            newPost.setPostId(postId);
            newPost.setShare(true);
            newPost.setShareBy(user.getId());
            newPost.setShareAt(new Date().getTime());
            newPost.setPostedAt(new Date().getTime());
            newPost.setOriginPostId(post.getPostId());
            newPost.setFullname(user.getId());
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

        dialog.findViewById(R.id.btn_share_external).setOnClickListener(v -> {
            Intent shareIntent;

            StringBuilder text = new StringBuilder("");
            if (post.getPostDescription() != null) {
                text.append(post.getPostDescription());
                text.append("\n");
            }

            if (post.getPostImage() != null) {
                text.append(post.getPostImage());
            }
            shareIntent = ShareCompat
                    .IntentBuilder
                    .from(activity)
                    .setType("*/*")
                    .setText(text)
                    .getIntent();
            shareIntent = Intent.createChooser(shareIntent, "Chia sẻ");
            activity.startActivity(shareIntent);

            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    ExoPlayer exoPlayer;

    private void showCenterDialog(String uri) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centersheet_video);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        StyledPlayerView playerView = dialog.findViewById(R.id.videoView_dialog);
        exoPlayer = new ExoPlayer.Builder(dialog.getContext()).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        ImageButton btnCloseDialog = dialog.findViewById(R.id.close_sheet);
        btnCloseDialog.setOnClickListener(v -> {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
            }
            exoPlayer = null;
            dialog.dismiss();
        });

        dialog.show();
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
