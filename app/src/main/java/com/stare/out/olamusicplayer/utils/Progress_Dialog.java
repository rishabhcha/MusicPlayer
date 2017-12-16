package com.stare.out.olamusicplayer.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by rishabh on 3/12/17.
 */

public class Progress_Dialog {

    private ProgressDialog progressDialog;
    private Context context;
    private String message;

    public Progress_Dialog(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.dismiss();
    }
}
