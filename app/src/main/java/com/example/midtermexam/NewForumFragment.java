package com.example.midtermexam;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class NewForumFragment extends Fragment {
    EditText editTextTitle, editTextDescription;
    DataServices.AuthResponse authResponse;

    public NewForumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_new_forum, container, false);
        editTextTitle = view.findViewById(R.id.editForumTitle);
        editTextDescription = view.findViewById(R.id.editForumDescription);

        view.findViewById(R.id.buttonCancleForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.buttonSubmitForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextTitle.getText().toString().isEmpty() || editTextDescription.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Enter the missing fields", Toast.LENGTH_SHORT).show();
                } else {
                    new doAsyncTaskCreateForum().execute(editTextTitle.getText().toString(), editTextDescription.getText().toString());
                }
            }
        });
        return view;
    }

    public void setResponse(DataServices.AuthResponse response) {
        this.authResponse = response;
    }


    class doAsyncTaskCreateForum extends AsyncTask<String, Integer, DataServices.Forum> {
        @Override
        protected DataServices.Forum doInBackground(String... strings) {
            String title = strings[0];
            String description = strings[1];

            try {
                DataServices.Forum forum = DataServices.createForum(authResponse.token, title, description);
                return forum;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DataServices.Forum forum) {
            super.onPostExecute(forum);
            if (forum != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}