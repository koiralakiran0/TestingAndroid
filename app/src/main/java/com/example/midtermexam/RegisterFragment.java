package com.example.midtermexam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

    private static final String TAG = "TAG_Register";
    EditText registeremail,registerpassword,registerName;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle("Register");

        registeremail = view.findViewById(R.id.RegisterEmail);
        registerpassword = view.findViewById(R.id.registerpassword);
        registerName = view.findViewById(R.id.RegisterName);

        view.findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  registeremail.getText().toString();
                String password = registerpassword.getText().toString();
                String name = registerName.getText().toString();
                if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                    Toast.makeText(getActivity(), "Please Fill in  Name/Login/Password!!", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getActivity(), "EMAIL incorrect", Toast.LENGTH_SHORT).show();
                }  else{
                    new doAsyncTaskRegister().execute(name,email, password);
                }
            }
        });

        view.findViewById(R.id.buttonCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment fragment = new LoginFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).commit();
            }
        });

        return view;
    }

    RegisterFragment.RegisterListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (RegisterFragment.RegisterListener)(context);
    }

    interface RegisterListener {
        void goToForumFragmentFromRegister(DataServices.AuthResponse response);
    }

    class doAsyncTaskRegister extends AsyncTask<String, Integer, DataServices.AuthResponse> {
        @Override
        protected DataServices.AuthResponse doInBackground(String... strings) {
            String name = strings[0];
            String email = strings[1];
            String password = strings[2];

            DataServices.AuthResponse response = null;
            try {
                response = DataServices.register(name, email, password);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(DataServices.AuthResponse authResponse) {
            super.onPostExecute(authResponse);
            if (authResponse!= null) {
                mListener.goToForumFragmentFromRegister(authResponse);
            } else {
                Toast.makeText(getContext(), "Auth Response Not Valid, cannot Register", Toast.LENGTH_SHORT).show();
            }
        }
    }
}