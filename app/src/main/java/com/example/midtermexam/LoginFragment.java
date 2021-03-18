package com.example.midtermexam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    private static final String TAG = "TAG_LOGIN";
    EditText loginEmail, loginpassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");

        loginEmail = (EditText) view.findViewById(R.id.LoginEmail);
        loginpassword = (EditText) view.findViewById(R.id.LoginPassword);

        view.findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  loginEmail.getText().toString();
                String password = loginpassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(), "Please Enter Email/Password!!", Toast.LENGTH_SHORT).show();
                }else{
                    new doAsyncTaskLogin().execute(email, password);
                }
            }
        });

        view.findViewById(R.id.buttonNewAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment fragment = new RegisterFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).commit();
            }
        });

        return view;
    }

    LoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginListener) context;
    }

    interface LoginListener {
        void goToFormFragmentFromLogin(DataServices.AuthResponse response);
    }


    class doAsyncTaskLogin extends AsyncTask<String, Integer, DataServices.AuthResponse> {
        @Override
        protected DataServices.AuthResponse doInBackground(String... strings) {
            String email = strings[0];
            String password = strings[1];
            DataServices.AuthResponse response = null;
            try {
                response = DataServices.login(email, password);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(DataServices.AuthResponse authResponse) {
            super.onPostExecute(authResponse);
            if (authResponse!= null) {
                mListener.goToFormFragmentFromLogin(authResponse);
            } else {
                Toast.makeText(getContext(), "Auth Response Not Valid, cannot login", Toast.LENGTH_SHORT).show();
            }
        }
    }


}