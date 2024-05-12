package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.DashboardRvBinding;
import com.example.travel_mobile_app.dto.UserToken;
import com.example.travel_mobile_app.fragments.CreatePostFragment;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.WriteResult;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    List<PostModel> list;
    Context context;
    FragmentManager fragmentManager;
    Activity activity;
    private FirebaseFirestore db;
    private MaterialButton btnLike, btnComment, btnShare;

    public PostAdapter(List<PostModel> list, Context context, FragmentManager fragmentManager, Activity activity) {
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    public PostAdapter(List<PostModel> list, Context context, FragmentManager fragmentManager, Activity activity, FirebaseFirestore db) {
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

        LinearLayout container = holder.binding.container;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) container.getLayoutParams();
        UserModel user = SharedPreferencesManager.readUserInfo();

        //load image
        if (post.getPostImage() != null) {
            Glide.with(context)
                 .load(Uri.parse(post.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.binding.postimg);
        } else {
            Glide.with(context).clear(holder.binding.postimg);
            holder.binding.postimg.setImageURI(null);
            holder.binding.postimg.setVisibility(View.GONE);
        }

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
            setUserName(holder.binding.usernameShare, post.getShareBy(), holder.binding.profileImageShare);

            holder.binding.timestampShare.setText(CustomDateTime.formatDate(post.getShareAt()));
            btnLike = holder.binding.likeShare;
            btnComment = holder.binding.commentShare;
            btnShare = holder.binding.shareShare;
        }else{
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            btnLike = holder.binding.like;
            btnComment = holder.binding.comment;
            btnShare = holder.binding.share;
            holder.binding.topbarShare.setVisibility(View.GONE);
            holder.binding.more.setVisibility(View.VISIBLE);
            holder.binding.moreShare.setVisibility(View.GONE);
            holder.binding.btnsShare.setVisibility(View.GONE);
            holder.binding.btns.setVisibility(View.VISIBLE);
        }
        setUserName(holder.binding.username, post.getPostedBy(), holder.binding.profileImage);
        holder.binding.timestamp.setText(CustomDateTime.formatDate(post.getPostedAt()));
        container.setLayoutParams(layoutParams);


        final boolean[] isLike = {false};
        getLikeInfo(btnLike, isLike, post, user.getId());

        btnLike.setOnClickListener(v -> {
            toggleBtnLike(post, isLike, position);
        });
        if (post.getLikes() == null || post.getLikes().size() == 0) {
            btnLike.setText("");
        } else {
            btnLike.setText(String.valueOf(post.getLikes().size()));
        }
        holder.binding.des.setText(post.getPostDescription());

        if (post.getComments() == null || post.getComments().size() == 0) {
            btnComment.setText("");
        } else {
            btnComment.setText(String.valueOf(post.getComments().size()));
        }

        btnShare.setOnClickListener(v -> {
            try {
                showDialogShare(post);
            } catch (CloneNotSupportedException e) {

            }
        });

        StringBuilder amountShare = new StringBuilder("");

        CollectionReference posts = db.collection("posts");
        posts.whereEqualTo("share", true)
             .whereEqualTo("originPostId", post.getPostId())
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful() && task.getResult() != null) {
                     if (task.getResult().size() != 0) {
                         btnShare.setText(String.valueOf(task.getResult().size()));
                         amountShare.append(task.getResult().size());
                     } else {
                         btnShare.setText("");
                     }

                 }

             }).addOnFailureListener(e -> {
                 btnShare.setText("");
             });

        btnComment.setOnClickListener(v -> {
            showBottomDialog(post, isLike, amountShare);
        });

        //See detail info
        holder.binding.postUserName.setOnClickListener(v -> {
            seeInfoDetail(post);
        });
        holder.binding.postUserNameShare.setOnClickListener(v -> {
            seeInfoDetail(post);
        });

        String url = post.getPostImage();
        if (url != null && url.contains("video")) {
            holder.binding.bgVideo.setVisibility(View.VISIBLE);
        } else {
            holder.binding.bgVideo.setVisibility(View.GONE);
        }
        holder.binding.postimg.setOnClickListener(v -> {
            if (url != null && url.contains("video")) {
                showCenterDialog(post.getPostImage());
            } else {
                showCenterDialog(holder.binding.postimg.getDrawable());
            }
        });


        List<String> idSavePost = new ArrayList<>();
        MaterialToolbar materialToolbar = holder.binding.more;
        Menu menu = materialToolbar.getMenu();
        MenuItem selectDeletePost = menu.findItem(R.id.del_post);
        MenuItem selectEditPost = menu.findItem(R.id.edit_post);
        MenuItem selectSavePost = menu.findItem(R.id.save_post);

        MaterialToolbar materialToolbarShare = holder.binding.moreShare;
        Menu menuShare = materialToolbarShare.getMenu();
        MenuItem selectDeletePostShare = menuShare.findItem(R.id.del_post);
        MenuItem selectEditPostShare = menuShare.findItem(R.id.edit_post);
        MenuItem selectSavePostShare = menuShare.findItem(R.id.save_post);

        materialToolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.del_post) {
                removePost(post);
            } else if (itemId == R.id.save_post) {
                savePost(post, user.getId(), idSavePost, selectSavePostShare, selectSavePost);
            } else if (itemId == R.id.edit_post) {
                replaceScreen(R.id.container, new CreatePostFragment(post.getPostId()), "social_fragment");
            }
            return true;
        });

        if (user.getId().equals(post.getPostedBy())) {
            selectDeletePost.setVisible(true);
            selectEditPost.setVisible(true);
        } else {
            selectDeletePost.setVisible(false);
            selectEditPost.setVisible(false);
        }

        materialToolbarShare.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.del_post) {
                removePost(post);
            } else if (itemId == R.id.save_post) {
                savePost(post, user.getId(), idSavePost, selectSavePostShare, selectSavePost);
            } else if (itemId == R.id.edit_post) {
//                replaceScreen(R.id.container,new CreatePostFragment(post.getPostId()),"social_fragment");
            }
            return true;
        });

        isSavePost(post, user.getId(), selectSavePost, selectSavePostShare, idSavePost);

        selectEditPostShare.setVisible(false);
        if (user.getId().equals(post.getShareBy())) {
            selectDeletePostShare.setVisible(true);
        } else {
            selectDeletePostShare.setVisible(false);
        }


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


        }

    }

    private void setUserName(TextView userName, String userId, ImageView imageView) {
        CollectionReference users = db.collection("users");
        users.document(userId)
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     if (userModel != null && userModel.getFullName() != null) {
                         userName.setText(userModel.getFullName());
                     }

                     if (userModel != null && userModel.getAvatarURL() != null) {
                         Glide.with(context)
                              .load(Uri.parse(userModel.getAvatarURL()))
                              .centerCrop()
                              .placeholder(R.drawable.image_empty)
                              .into(imageView);
                     }
                 }
             }).addOnFailureListener(e -> {
                 System.out.println("ERRORR::" + e.getMessage());
             });
    }

    private void seeInfoDetail(PostModel post) {
        replaceScreen(R.id.container, new SocialUserDetailInfoFragment(post.getPostedBy()), "userDetailInfo_fragment");
    }

    private void replaceScreen(@IdRes int containerViewId, @NonNull Fragment fragment, String backTrackName) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_animation,
                R.anim.exit_animation,
                R.anim.pop_enter_animation,
                R.anim.pop_exit_animation);

        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    private void toggleBtnLike(PostModel post, final boolean[] isLike, Integer position) {
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

        if (position != null) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }


    private void isSavePost(PostModel post, String userId, MenuItem selectSavePostShare, MenuItem selectSavePost, List<String> idSavePost) {
        CollectionReference savePosts = db.collection("save_posts");
        savePosts.whereEqualTo("savedBy", userId)
                 .whereEqualTo("postID", post.getPostId())
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             SaveItemModel itemModel = document.toObject(SaveItemModel.class);
                             if (itemModel != null) {
                                 idSavePost.add(itemModel.getId());
                                 selectSavePost.setTitle("Bỏ lưu");
                                 selectSavePostShare.setTitle("Bỏ lưu");
                             } else {
                                 selectSavePost.setTitle("Lưu");
                                 selectSavePostShare.setTitle("Lưu");
                             }
                         }

                     }
                 }).addOnFailureListener(e -> {
                     selectSavePost.setTitle("Lưu");
                     selectSavePostShare.setTitle("Lưu");
                 });


    }

    private void savePost(PostModel post, String userId, List<String> idSavePost, MenuItem selectSavePostShare, MenuItem selectSavePost) {
        String savePostId = UUID.randomUUID().toString().replace("-", "");
        SaveItemModel itemModel = new SaveItemModel(savePostId, post.getPostId(), userId, post.getPostDescription(), new Date().getTime(), post.getPostImage(), 0);
        CollectionReference posts = db.collection("save_posts");

        if (idSavePost.size() != 0) {
            posts.whereIn("id", idSavePost)
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         WriteBatch batch = db.batch();
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             batch.delete(document.getReference());
                         }
                         batch.commit().addOnSuccessListener(aVoid -> {
                             idSavePost.clear();
                         });
                     }
                 });

            selectSavePost.setTitle("Lưu");
            selectSavePostShare.setTitle("Lưu");
            return;
        }

        posts.document(savePostId)
             .set(itemModel)
             .addOnSuccessListener(unused -> {
                 Toast.makeText(activity, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                 idSavePost.add(savePostId);
                 selectSavePost.setTitle("Bỏ lưu");
                 selectSavePostShare.setTitle("Bỏ lưu");
             })
             .addOnFailureListener(e -> {
                 Toast.makeText(activity, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
             });
    }

    private void removePost(PostModel post) {
        CollectionReference posts = db.collection("posts");
        posts.document(post.getPostId())
             .delete()
             .addOnSuccessListener(unused -> {
                 Toast.makeText(activity, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                 list = list.stream().filter(item -> !item.getPostId().equals(post.getPostId())).collect(Collectors.toList());
                 notifyDataSetChanged();
             })
             .addOnFailureListener(e -> {
                 Toast.makeText(activity, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
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

        db.collection("users").document(post.getPostedBy())
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  UserModel userModel = task.getResult().toObject(UserModel.class);
                  if (userModel.isEnableNotification()) {
                      accessSend(post, conent);
                  }
              }
          }).addOnFailureListener(e -> {
              Log.e("ERROR::", e.getMessage());
          });


    }

    private void accessSend(PostModel post, HashMap<String, String> conent) {
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

    private void saveNewPost(CollectionReference posts, PostModel post) {

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
                     Toast.makeText(activity, "Chia sẻ thành công", Toast.LENGTH_SHORT).show();
                     notifyDataSetChanged();
                 }).addOnFailureListener(e -> {
                     Log.e("ERROR", "[ERROR-CREATE-POST]", e);
                     Toast.makeText(activity, "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
                 });
            dialog.dismiss();
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

    //comments UI
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
            toggleBtnLike(post, isLike, null);
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

            CommentModel comment = new CommentModel(user.getId(), commentId, user.getAvatarURL(), user.getFullName(), msg, new Date().getTime());
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
                Toast.makeText(activity, "Mạng kém", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, "Mạng kém", Toast.LENGTH_SHORT).show();
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

