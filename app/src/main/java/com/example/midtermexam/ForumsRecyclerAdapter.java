package com.example.midtermexam;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class ForumsRecyclerAdapter extends RecyclerView.Adapter<ForumsRecyclerAdapter.ForumsViewHolder> {
    ArrayList<DataServices.Forum> forums;
    ForumsFragment.ForumsListener mListener;
    DataServices.AuthResponse response;
    public static final String TAG = "TAG_ForumsRecyclerAdapter";

    public ForumsRecyclerAdapter(ArrayList<DataServices.Forum> forums, ForumsFragment.ForumsListener mListener, DataServices.AuthResponse response) throws DataServices.RequestException {
        this.forums = forums;
        //Log.d(TAG, "ForumsRecyclerAdapter: " + forums.size());
        this.mListener = mListener;
        this.response = response;
    }

    @NonNull
    @Override
    public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_item, parent, false);
        ForumsViewHolder forumsViewHolder = new ForumsViewHolder(view, mListener, forums, response);
        return forumsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumsViewHolder holder, int position) {
        DataServices.Forum forum = forums.get(position);

        holder.textViewTitle.setText(forum.getTitle());
        if (forum.getDescription().toCharArray().length > 200) {
            holder.textViewDescription.setText(forum.getDescription().substring(0, 200));
        } else {
            holder.textViewDescription.setText(forum.getDescription());
        }
        holder.textViewAuthor.setText(forum.getCreatedBy().getName());
        holder.textViewLikes.setText(forum.getLikedBy().size() + " Likes");
        //TODO change date format
        holder.textViewDateTime.setText(forum.getCreatedAt().toString());
        holder.imageViewTrash.setVisibility(View.INVISIBLE);
        holder.token = response.getToken();
        holder.forum = forum;
        holder.imageLike = false;
        holder.imageViewTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doAsyncTaskDelete().execute(response.getToken(), String.valueOf(holder.forumId));
            }
        });

        HashSet<DataServices.Account> likingAccounts = forum.getLikedBy();

        if (likingAccounts.contains(response.getAccount())) {
            //change the heart symbol
            holder.imageViewLike.setImageResource(R.drawable.like_favorite);
            holder.imageLike = true;
        }

        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imageLike == false){
                    holder.imageViewLike.setImageResource(R.drawable.like_favorite);
                    holder.imageLike = true;
                    new doAsyncTaskLikeForum().execute(response.getToken(), String.valueOf(holder.forumId));
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                    holder.imageLike = false;
                    new doAsyncTaskUnLikeForum().execute(response.getToken(), String.valueOf(holder.forumId));
                }
            }
        });
        if (forum.getCreatedBy().getEmail().equals(response.getAccount().getEmail())){
            holder.imageViewTrash.setVisibility(View.VISIBLE);
        }
        holder.forumId = forum.getForumId();
    }

    @Override
    public int getItemCount() {
        return this.forums.size();
    }

    public static class ForumsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewDescription, textViewLikes, textViewDateTime;
        ImageView imageViewTrash, imageViewLike;
        String token;
        long forumId;
        boolean imageLike;
        DataServices.Forum forum;
        ArrayList<DataServices.Forum> forums;
        ForumsFragment.ForumsListener mListener;
        DataServices.AuthResponse response;

        public ForumsViewHolder(@NonNull View itemView, ForumsFragment.ForumsListener mListener, ArrayList<DataServices.Forum> forums, DataServices.AuthResponse response) {
            super(itemView);
            this.forums = forums;
            this.response = response;
            this.mListener = mListener;
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);

            imageViewLike = itemView.findViewById(R.id.imageViewLike);
            imageViewTrash =itemView.findViewById(R.id.imageViewTrash);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.goToForumDetailFragment(forum, response);
                }
            });

            imageViewTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new doAsyncTaskDelete().execute(token, String.valueOf(forumId));
                    //need token and forumID
                    Log.d("TAG", "onClick: Trahs");
                }
            });

            imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // new doAsyncTaskLikeForum().execute(token, String.valueOf(forumId));
                    Log.d("TAG", "onClick: LIKE");
                }
            });
        }
    }

    private class doAsyncTaskDelete extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            try {
                DataServices.deleteForum(token, forumID);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            try {
                forums = DataServices.getAllForums(token);
                notifyDataSetChanged();
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
        }
    }

    private class doAsyncTaskLikeForum extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            try {
                DataServices.likeForum(token, forumID);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            try {
                forums = DataServices.getAllForums(token);
                notifyDataSetChanged();
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
        }
    }

    private class doAsyncTaskUnLikeForum extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            try {
                DataServices.unLikeForum(token, forumID);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            try {
                forums = DataServices.getAllForums(token);
                notifyDataSetChanged();
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
        }
    }
}
