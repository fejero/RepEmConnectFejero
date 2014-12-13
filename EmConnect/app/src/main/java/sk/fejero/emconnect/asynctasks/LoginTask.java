package sk.fejero.emconnect.asynctasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


import javax.mail.MessagingException;

import sk.fejero.emconnect.EmailActivity;
import sk.fejero.emconnect.SigninActivity;
import sk.fejero.emconnect.mailclient.AccountSettings;
import sk.fejero.emconnect.mailclient.incomming.ImapClient;

/**
 * Created by Rastislav on 8.12.2014.
 */
public class LoginTask extends AsyncTask<ImapClient, Void, Boolean> {

    private ProgressDialog progressDialog;
    private SigninActivity activity;
    private AccountSettings acc;
    private String error;

    public LoginTask(SigninActivity activity, AccountSettings acc) {
        this.activity = activity;
        this.acc = acc;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Signing in");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(ImapClient... params) {
        Boolean verified = false;
        try {
            params[0].testConnection();
            params[0].initStore();
            acc.setAuthor(params[0].getAuthor());
            params[0].closeStore();
            verified = true;
        } catch (javax.mail.NoSuchProviderException e ) {
            e.printStackTrace();
            error = e.getMessage();
        } catch (MessagingException e) {
            error = e.getMessage();
        }
        return verified;
    }

    @Override
    protected void onPostExecute(Boolean verified) {
        super.onPostExecute(verified);
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (verified) {
            Intent myIntent;
            myIntent = new Intent(activity, EmailActivity.class);

            myIntent.putExtra("userName",acc.getUserName());
            myIntent.putExtra("userPwd",acc.getUserPwd());
            myIntent.putExtra("smtpServer",acc.getSmtpServer());
            myIntent.putExtra("imapServer",acc.getImapServer());
            myIntent.putExtra("dwnFolder","mailclient");
            myIntent.putExtra("smtpPort",acc.getSmtpPort());
            myIntent.putExtra("imapPort",acc.getImapPort());
            myIntent.putExtra("storeMails",1209600);
            myIntent.putExtra("author",acc.getAuthor());
            myIntent.putExtra("offline", false);

            activity.writeAccount(acc);

            activity.startActivity(myIntent);
        } else {
            Toast toast = Toast.makeText(activity.getApplicationContext(), error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
