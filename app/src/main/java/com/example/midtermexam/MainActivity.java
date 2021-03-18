package com.example.midtermexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener, ForumsFragment.ForumsListener {
    DataServices.AuthResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.layout, new LoginFragment()).commit();
    }

    @Override
    public void goToFormFragmentFromLogin(DataServices.AuthResponse response) {
        this.response = response;
        ForumsFragment fragment = new ForumsFragment();
        fragment.setResponse(this.response);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).addToBackStack(null).commit();
    }

    @Override
    public void goToForumFragmentFromRegister(DataServices.AuthResponse response) {
        this.response = response;
        ForumsFragment fragment = new ForumsFragment();
        fragment.setResponse(this.response);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).addToBackStack(null).commit();
    }

    @Override
    public void goToNewForumsFragment(DataServices.AuthResponse response) {
        NewForumFragment fragment = new NewForumFragment();
        fragment.setResponse(this.response);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).addToBackStack(null).commit();
    }

    @Override
    public void logOut() {
        response = null;
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new LoginFragment()).commit();
    }

    @Override
    public void goToForumDetailFragment(DataServices.Forum forum, DataServices.AuthResponse response) {
        ForumDetailFragment fragment = new ForumDetailFragment();
        fragment.setForumID(forum, response);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, fragment).addToBackStack(null).commit();
    }
}