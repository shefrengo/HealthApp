package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.Notifications;
import com.shefrengo.health.model.Posts;
import com.shefrengo.health.model.Users;
import com.shefrengo.health.R;
import com.shefrengo.health.utils.StringManipulation;


import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private static final String TAG = "NotificationAdapter";

    private final Context context;
    private final List<Data> dataList;
    private final List<Notifications> notificationList;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnNotificationClickListener onNotificationClickListener;

    public NotificationAdapter(Context context, List<Notifications> notificationList, List<Data> dataList
    ) {
        this.context = context;
        this.notificationList = notificationList;
        this.dataList = dataList;

    }


    public void setOnNotificationClickListener(OnNotificationClickListener onNotificationClickListener) {
        this.onNotificationClickListener = onNotificationClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recyclerview_layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String userid = notificationList.get(position).getUserid();
        String title = notificationList.get(position).getStoryTitle();
        String postUserid = notificationList.get(position).getPostUserid();
        String postid = notificationList.get(position).getPostid();


        String notificationType = notificationList.get(position).getText();
        String username = dataList.get(position).getUsername();
        String photoUrl = dataList.get(position).getProfilePhotoUrl();
        Date timestamp = notificationList.get(position).getTimestamp();


        //set likes notifications
        if (notificationType.equals(context.getString(R.string.liked_your_post))) {
            setCommonLikesNotifications(holder, timestamp, postid, notificationType);
        }


        //set comments notifications
        if (notificationType.equals(context.getString(R.string.comment_notification))) {
            setCommonCommentsNotifications(holder, timestamp, postid, notificationType);
        }
        //set following notifications
        if (notificationType.equals(context.getString(R.string.started_following_you))) {


            String finalText = username + " " + notificationType;
            SpannableStringBuilder spannableString = new SpannableStringBuilder(finalText);

            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, finalText.indexOf(context.getString(R.string.started_following_you)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.notificationType.setText(finalText);
        }

        //set reply notifications
        if (notificationType.equals(context.getString(R.string.comment_reply))) {

            setCommonReplyNotifications(holder, timestamp, postid, notificationType);
        }

        //adding main profile image
        holder.getUserInfo(photoUrl, position);


        //adding post image for post
        if (notificationList.get(position).isInPost()) {
            holder.likedImage.setVisibility(View.VISIBLE);
            getPostImage(holder.likedImage, postUserid, title);
        }

        //adding post image for comments
        if (notificationList.get(position).isInComment()) {
            holder.likedImage.setVisibility(View.VISIBLE);
            getPostImage(holder.likedImage, postUserid, title);
        }


        //set notification color
        if (!notificationList.get(position).isRead()) {
            holder.frameLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.notification_unread));
        } else {
            holder.frameLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.background_color));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    /**
     * getting main post image for profile photo
     **/
    private void getPostImage(final ImageView imageView, final String userid, final String title) {

        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("story_title", title);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Posts posts = queryDocumentSnapshot.toObject(Posts.class);
                String myUserid = posts.getUserid();
                if (myUserid.equals(userid)) {
                    String photoUrl = posts.getImageUrl();
                    Glide.with(context).load(photoUrl).placeholder(R.drawable.ic_app_background).into(imageView);
                }
            }

        });


    }


    /**
     * Setting likes notifications
     */
    private void setCommonLikesNotifications(final ViewHolder holder, final Date timestamp, final String postid, final String notificationTpe) {


        assert user != null;
        final CollectionReference collectionReference = db.collection("Notifications")
                .document(user.getUid()).collection("Notifications");

        Query query = collectionReference.whereEqualTo("postid", postid).whereEqualTo("timestamp", timestamp);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            holder.notifications = new StringBuilder();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                final Notifications notification = queryDocumentSnapshot.toObject(Notifications.class);
                List<String> likesUserid = notification.getLikesUserids();


                if (notification.getText().equals(context.getString(R.string.liked_your_post))) {
                    if (likesUserid.size() >= 2) {
                        holder.profilePhoto2.setVisibility(View.VISIBLE);

                        String profile1 = likesUserid.get(likesUserid.size() - 1);
                        String profile2 = likesUserid.get(likesUserid.size() - 2);
                        getDoubleProfile(profile1, profile2, holder);

                    }
                    for (int i = 0; i < likesUserid.size(); i++) {

                        Log.d(TAG, "likes list " + likesUserid.get(i));
                        setLikesNotification(likesUserid.get(i), holder, notificationTpe, timestamp.toString());
                    }
                }
            }
        });
    }

    /**
     * Getting the profile photos for the double circular imageview
     */
    private void getDoubleProfile(String userid1, String userid2, final ViewHolder holder) {
        //get first profile photo
        db.collection("Users").document(userid1).get().addOnSuccessListener(documentSnapshot -> {
            Users users = documentSnapshot.toObject(Users.class);
            assert users != null;
            String profile = users.getProfilePhotoUrl();


            Glide.with(context).load(profile).placeholder(R.drawable.ic_app_background).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.profilephoto);

        });


        //get second profile photo
        db.collection("Users").document(userid2).get().addOnSuccessListener(documentSnapshot -> {
            Users users = documentSnapshot.toObject(Users.class);
            assert users != null;
            String profile = users.getProfilePhotoUrl();
            Glide.with(context).load(profile).placeholder(R.drawable.ic_app_background).into(holder.profilePhoto2);

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e ));

    }

    /**
     * Setting likes notifications 2
     */
    private void setLikesNotification(String userid, final ViewHolder holder, final String notificationTpe, final String timestamp) {
        CollectionReference collectionReference = db.collection("Users");

        collectionReference.document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    Users users = documentSnapshot.toObject(Users.class);
                    assert users != null;
                    holder.notifications.append(users.getUsername());
                    holder.notifications.append(",");

                    String[] splitUser = holder.notifications.toString().split(",");

                    String[] usernames = StringManipulation.removeArrayDuplication(splitUser);


                    int length = usernames.length;

                    int lastUserid = length - 1;
                    int secondLastUserid = length - 2;

                    Log.d(TAG, users.getUsername());
                    if (length == 1) {
                        holder.notifcationsString = usernames[0];
                    } else if (length == 2) {

                        holder.notifcationsString = usernames[lastUserid]
                                + " and " + usernames[secondLastUserid];

                    } else if (length > 2) {
                        holder.notifcationsString = usernames[lastUserid]
                                + "," + usernames[secondLastUserid] + " and " + (usernames.length - 2) + " others";
                    } else {
                        holder.notifcationsString = "";
                    }

                    String notificationName = holder.notifcationsString;

                    String finalText = notificationName + " " + notificationTpe;
                    SpannableStringBuilder spannableString = new SpannableStringBuilder(finalText);

                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, finalText.indexOf("liked"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.notificationType.setText(finalText);
                }
            }
        });
    }

    /**
     * Setting Reply notifications
     */
    private void setReplyNotification(final String userid, final ViewHolder holder, final String notificationTpe, final String timestamp) {
        CollectionReference collectionReference = db.collection("Users");
        collectionReference.document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    Users users = documentSnapshot.toObject(Users.class);
                    assert users != null;
                    holder.reply.append(users.getUsername());
                    holder.reply.append(",");

                    String[] splitUser = holder.reply.toString().split(",");

                    String[] usernames = StringManipulation.removeArrayDuplication(splitUser);
                    int length = usernames.length;

                    int lastUserid = length - 1;
                    int secondLastUserid = length - 2;

                    if (length == 1) {
                        holder.replyString = usernames[0];
                    } else if (length == 2) {


                        holder.replyString = usernames[lastUserid]
                                + " and " + usernames[secondLastUserid];

                    } else if (length > 2) {
                        holder.replyString = usernames[lastUserid]
                                + "," + usernames[secondLastUserid] + " and " + (usernames.length - 2) + " others";
                    } else {
                        holder.replyString = "";
                    }


                    String notificationName = holder.replyString;


                    String finalText = notificationName + " " + notificationTpe;
                    SpannableStringBuilder spannableString = new SpannableStringBuilder(finalText);

                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, finalText.indexOf(context.getString(R.string.comment_reply)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.notificationType.setText(finalText);
                }
            }
        });
    }

    /**
     * Setting Reply notifications
     */
    private void setCommonReplyNotifications(final ViewHolder holder, final Date timestamp, final String postid, final String notificationTpe) {

        assert user != null;
        final CollectionReference collectionReference = db.collection("Notifications")
                .document(user.getUid()).collection("Notifications");

        Query query = collectionReference.whereEqualTo("postid", postid).whereEqualTo("timestamp", timestamp);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            holder.reply = new StringBuilder();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                final Notifications notification = queryDocumentSnapshot.toObject(Notifications.class);
                List<String> replyUserid = notification.getReplyUserid();
                if (notification.getText().equals(context.getString(R.string.comment_reply))) {

                    for (int i = 0; i < replyUserid.size(); i++) {

                        if (replyUserid.size() > 2) {
                            holder.profilePhoto2.setVisibility(View.VISIBLE);
                            String profile1 = replyUserid.get(replyUserid.size() - 1);
                            String profile2 = replyUserid.get(replyUserid.size() - 2);
                            getDoubleProfile(profile1, profile2, holder);
                        }
                        setReplyNotification(replyUserid.get(i), holder, notificationTpe, timestamp.toString());
                    }
                }
            }
        });

    }

    /**
     * Setting Comments notifications
     */
    private void setCommentsNotification(String userid, final ViewHolder holder, final String notificationTpe) {

        CollectionReference collectionReference = db.collection("Users");
        collectionReference.document(userid).get().addOnSuccessListener(documentSnapshot -> {
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                Users users = documentSnapshot.toObject(Users.class);
                assert users != null;
                holder.comments.append(users.getUsername());
                holder.comments.append(",");

                String[] splitUser = holder.comments.toString().split(",");

                String[] usernames = StringManipulation.removeArrayDuplication(splitUser);
                int length = usernames.length;
                int lastUserid = length - 1;
                int secondLastUserid = length - 2;
                if (length == 1) {
                    holder.commentsString = usernames[0];
                } else if (length == 2) {
                    holder.commentsString = usernames[lastUserid]
                            + " and " + usernames[secondLastUserid];
                } else if (length > 2) {
                    holder.commentsString = usernames[lastUserid]
                            + "," + usernames[secondLastUserid] + " and " + (usernames.length - 2) + " others";
                } else {
                    holder.commentsString = "";
                }

                String notificationName = holder.commentsString;


                String finalText = notificationName + " " + notificationTpe;
                SpannableStringBuilder spannableString = new SpannableStringBuilder(finalText);

                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, finalText.indexOf(context.getString(R.string.comment_notification)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificationType.setText(finalText);
            }
        });
    }

    /**
     * Setting Comments notifications
     */
    private void setCommonCommentsNotifications(final ViewHolder holder, final Date timestamp, final String postid, final String notificationTpe) {

        assert user != null;
        final CollectionReference collectionReference = db.collection("Notifications")
                .document(user.getUid()).collection("Notifications");

        Query query = collectionReference.whereEqualTo("postid", postid).whereEqualTo("timestamp", timestamp);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            holder.comments = new StringBuilder();
            assert queryDocumentSnapshots != null;

            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                final Notifications notification = queryDocumentSnapshot.toObject(Notifications.class);

                List<String> commentsUserid = notification.getCommentsUserids();

                if (notification.getText().equals(context.getString(R.string.comment_notification))) {

                    if (commentsUserid.size() >= 2) {
                        holder.profilePhoto2.setVisibility(View.VISIBLE);
                        String profile1 = commentsUserid.get(commentsUserid.size() - 1);
                        String profile2 = commentsUserid.get(commentsUserid.size() - 2);


                        getDoubleProfile(profile1, profile2, holder);
                    }
                    for (int i = 0; i < commentsUserid.size(); i++) {
                        setCommentsNotification(commentsUserid.get(i), holder, notificationTpe);
                    }
                }
            }
        });
    }

    /**
     * notification click interface
     ***/
    public interface OnNotificationClickListener {
        void onNotificationClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CircleImageView profilephoto;
        private final CircleImageView profilePhoto2;
        //  private TextView username;
        private final ImageView likedImage;
        private String notifcationsString = "";
        private String commentsString = "";
        private StringBuilder comments;
        private StringBuilder notifications;
        private StringBuilder reply;
        private String replyString = "";
        private final RelativeLayout frameLayout;
        private final TextView notificationType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilephoto = itemView.findViewById(R.id.notification_profile_pic);
            /// username = itemView.findViewById(R.id.notification_username);
            frameLayout = itemView.findViewById(R.id.color_layout);
            notificationType = itemView.findViewById(R.id.notificatin_type);
            profilePhoto2 = itemView.findViewById(R.id.notification_profile_pic2);
            likedImage = itemView.findViewById(R.id.notification_liked_post);
            itemView.setOnClickListener(this);
        }
        private void getUserInfo(String photoUrl, final int position) {
            Glide.with(context).load(photoUrl).placeholder(R.drawable.ic_app_background)
                    .into(profilephoto);
        }

        @Override
        public void onClick(View v) {
            if (onNotificationClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onNotificationClickListener.onNotificationClick(position);
                }
            }
        }
    }


}