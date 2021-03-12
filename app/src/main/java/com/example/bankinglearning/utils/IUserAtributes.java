package com.example.bankinglearning.utils;

import android.net.Uri;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface IUserAtributes {

    String getUserName(Firebase accountsRoot, String UID);

    Uri getUserPhoto(GoogleSignInAccount account);

    String getUserEmail(GoogleSignInAccount account);
}
