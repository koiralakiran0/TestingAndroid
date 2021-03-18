package com.example.midtermexam;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ForumDetailFragment extends Fragment {

    TextView textViewTitle, textViewAuthor, textViewDetails, textViewNumComments;
    EditText editTextWriteComment;
    DataServices.Forum forum;
    DataServices.AuthResponse response;
    ArrayList<DataServices.Comment> comments;

    RecyclerView recyclerViewComments;
    LinearLayoutManager layoutManager;
    CommentsRecyclerAdapter adapter;

    public ForumDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_forum_detail, container, false);
        getActivity().setTitle("Forum");

        comments = new ArrayList<>();
        textViewDetails = view.findViewById(R.id.textViewdDescription);
        textViewTitle = view.findViewById(R.id.textViewdTitle);
        textViewAuthor = view.findViewById(R.id.textViewdAuthor);
        textViewNumComments = view.findViewById(R.id.textViewdComment);
        editTextWriteComment = view.findViewById(R.id.editTextdComment);

        textViewTitle.setText(forum.getTitle());
        textViewAuthor.setText(forum.getCreatedBy().getName());
        textViewDetails.setText(forum.getDescription());
        new doAsyncTaskGetComments().execute(response.getToken(), String.valueOf(forum.getForumId()));

        view.findViewById(R.id.buttondPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doAsyncTaskCreateComments().execute(response.getToken(), String.valueOf(forum.getForumId()), editTextWriteComment.getText().toString());
            }
        });

        recyclerViewComments = view.findViewById(R.id.recyclerviewdContainer);
        recyclerViewComments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewComments.setLayoutManager(layoutManager);
        return view;
    }

    public void setForumID(DataServices.Forum forum, DataServices.AuthResponse response) {
        this.forum = forum;
        this.response = response;
        Log.d("TAG", "setForumID: " + forum.toString());
    }


    private class doAsyncTaskGetComments extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            try {
                comments = DataServices.getForumComments(token, forumID);
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
                comments = DataServices.getForumComments(token, forum.getForumId());
                textViewNumComments.setText(comments.size() + " Comments");
                adapter = new CommentsRecyclerAdapter(comments, response, forum, textViewNumComments);
                recyclerViewComments.setAdapter(adapter);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }

        }
    }

    private class doAsyncTaskCreateComments extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            long forumID = Long.parseLong(strings[1]);
            String comment = strings[2];
            try {
                DataServices.createComment(token, forumID, comment);
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
                adapter.comments = DataServices.getForumComments(response.getToken(), forum.getForumId());
                adapter.notifyDataSetChanged();
                editTextWriteComment.setText("");
                textViewNumComments.setText(comments.size() +  " Comments");
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }

        }
    }
}