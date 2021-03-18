package com.example.midtermexam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ForumsFragment extends Fragment {
    DataServices.AuthResponse response;

    RecyclerView recyclerView_forums;
    LinearLayoutManager layoutManager;
    ForumsRecyclerAdapter adapter;
    ArrayList<DataServices.Forum> forums;

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forums, container, false);
        recyclerView_forums = view.findViewById(R.id.container);
        recyclerView_forums.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView_forums.setLayoutManager(layoutManager);

        new doAsyncTaskGetAllForums().execute(response.getToken());

        view.findViewById(R.id.buttonNewForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToNewForumsFragment(response);
            }
        });

        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logOut();
            }
        });
        return view;
    }

    ForumsFragment.ForumsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) (context);
    }

    interface ForumsListener {
        void goToNewForumsFragment(DataServices.AuthResponse response);
        void logOut();
        void goToForumDetailFragment(DataServices.Forum forumID, DataServices.AuthResponse response);
    }

    public void setResponse(DataServices.AuthResponse response) {
        this.response = response;
    }

    private class doAsyncTaskGetAllForums extends AsyncTask<String, Integer, ArrayList<DataServices.Forum>> {
        @Override
        protected ArrayList<DataServices.Forum> doInBackground(String... strings) {
            String token = strings[0];
            try {
                forums = DataServices.getAllForums(token);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return forums;
        }

        @Override
        protected void onPostExecute(ArrayList<DataServices.Forum> forums) {
            super.onPostExecute(forums);
            if (forums != null) {
                try {
                    adapter = new ForumsRecyclerAdapter(forums, mListener, response);
                    recyclerView_forums.setAdapter(adapter);
                } catch (DataServices.RequestException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "COULD NOT GET FORUMS", Toast.LENGTH_SHORT).show();
            }


        }
    }
}