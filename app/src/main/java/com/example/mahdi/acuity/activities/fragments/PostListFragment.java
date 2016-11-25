package com.example.mahdi.acuity.activities.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.PostViewHolder;
import com.example.mahdi.acuity.models.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private FirebaseAuth mAuth;
    private boolean myAccount;

    public PostListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

       Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);
                setPostPhoto(viewHolder, model.imageUrl);
                setPosterPhoto(viewHolder, model.authorUrl);

                if (model.likes.containsKey(getUid())) {
                    viewHolder.likesView.setImageResource(R.drawable.thumb_up_blue);
                } else {
                    viewHolder.likesView.setImageResource(R.drawable.thumb_up);
                }
                if (model.dislikes.containsKey(getUid())) {
                    viewHolder.dislikesView.setImageResource(R.drawable.thumb_down_blue);
                } else {
                    viewHolder.dislikesView.setImageResource(R.drawable.thumb_down);
                }

                if ((model.uid).equals(mAuth.getCurrentUser().getUid())) {
                    myAccount=true;
                }
                else {
                    myAccount=false;
                }
                final DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                final DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        onLikeClicked(globalPostRef);
                        onLikeClicked(userPostRef);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDislikeClicked(globalPostRef);
                        onDislikeClicked(userPostRef);
                    }
                }, getClick(model.uid), myAccount, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.alert)
                                .setTitle("Delete post")
                                .setMessage("Are you sure you want to delete this post?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePost(globalPostRef, userPostRef, model.imageRef);
                                    }
                                })
                                .setNegativeButton("CANCEL", null)
                                .show();
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private void setPostPhoto(final PostViewHolder viewHolder, String url) {
        final Context contextPostPhoto = viewHolder.postPhotoView.getContext();
        Glide.with(contextPostPhoto).load(url).centerCrop().into(viewHolder.postPhotoView);
    }

    private void setPosterPhoto(final PostViewHolder viewHolder, String authorUrl) {
        final Context contextUserPhoto = viewHolder.userPhotoView.getContext();
        if (authorUrl != null) {
            Glide.with(contextUserPhoto).load(authorUrl).bitmapTransform(new CropCircleTransformation(contextUserPhoto)).into(viewHolder.userPhotoView);
        }
    }

    private void onLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.likes.containsKey(getUid())) {
                    p.likesCount = p.likesCount - 1;
                    p.likes.remove(getUid());
                } else if (!p.likes.containsKey(getUid())) {
                    if (p.dislikes.containsKey(getUid())) {
                        p.dislikesCount = p.dislikesCount - 1;
                        p.dislikes.remove(getUid());
                    }
                    p.likesCount = p.likesCount + 1;
                    p.likes.put(getUid(), true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void onDislikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.dislikes.containsKey(getUid())) {
                    p.dislikesCount = p.dislikesCount - 1;
                    p.dislikes.remove(getUid());
                } else if (!p.dislikes.containsKey(getUid())) {
                    if (p.likes.containsKey(getUid())) {
                        p.likesCount = p.likesCount - 1;
                        p.likes.remove(getUid());
                    }
                    p.dislikesCount = p.dislikesCount + 1;
                    p.dislikes.put(getUid(), true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void deletePost(DatabaseReference globalPostRef, DatabaseReference userPostRef, String imgRef) {
        globalPostRef.removeValue();
        userPostRef.removeValue();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgRef);
        storageRef.delete();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
    public abstract View.OnClickListener getClick(String uid);

}
