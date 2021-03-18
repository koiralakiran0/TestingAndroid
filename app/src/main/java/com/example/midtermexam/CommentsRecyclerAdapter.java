package com.example.midtermexam;

import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsViewHolder> {
    ArrayList<DataServices.Comment> comments;
    DataServices.AuthResponse response;
    DataServices.Forum forum;
    TextView numComments;

    public CommentsRecyclerAdapter(ArrayList<DataServices.Comment> comments, DataServices.AuthResponse response, DataServices.Forum forum, TextView numComments) {
        this.comments = comments;
        this.response = response;
        this.forum = forum;
        this.numComments = numComments;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        CommentsViewHolder viewHolder = new CommentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        DataServices.Comment comment = comments.get(position);
        holder.textViewAuthor.setText(comment.getCreatedBy().getName());
        holder.textViewDetails.setText(comment.getText());
        holder.textViewTime.setText(comment.getCreatedAt().toString());
        holder.imageView.setVisibility(View.INVISIBLE);

        if (comment.getCreatedBy().getEmail().equals(response.getAccount().getEmail())){
            holder.imageView.setVisibility(View.VISIBLE);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //token forumID, commentID
                new doAsyncTaskDeleteComments().execute(response.getToken(), String.valueOf(forum.getForumId()), String.valueOf(comment.getCommentId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAuthor, textViewDetails, textViewTime;
        ImageView imageView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewCommentAuthor);
            textViewDetails = itemView.findViewById(R.id.textViewCommentsDetails);
            textViewTime = itemView.findViewById(R.id.textViewcommentDate);
            imageView = itemView.findViewById(R.id.imageViewcommentDelete);
        }
    }

    private class doAsyncTaskDeleteComments extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            long commentID = Long.parseLong(strings[2]);
            try {
                DataServices.deleteComment(token, forumID, commentID);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            try {
                comments = DataServices.getForumComments(response.getToken(), forum.getForumId());
                numComments.setText(comments.size() + " Comments");
                notifyDataSetChanged();
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
        }
    }

}
