package sk.fejero.emconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sk.fejero.emconnect.asynctasks.LoginTask;
import sk.fejero.emconnect.mailclient.AccountSettings;
import sk.fejero.emconnect.mailclient.incomming.ImapClient;


public class SigninActivity extends Activity {

    private Button loginButton;
    private EditText userEditText;
    private EditText passEditText;
    private EditText imapEditText;
    private EditText smtpEditText;
    private EditText imapPortEditText;
    private EditText smtpPortEditText;
    AccountSettings acc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signin);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        acc = readAccount();

        if(acc == null) {
            String message = "Application is being started for the first time, regular online login is required";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
            showInterface();
            checkConnection(false);
        } else {
            checkConnection(true);
        }
    }

    public void showInterface() {
        setContentView(R.layout.register_layout);
        userEditText = (EditText)findViewById(R.id.username_text);
        userEditText.setText("rc301ve");
        passEditText = (EditText)findViewById(R.id.pass_text);
        imapEditText = (EditText)findViewById(R.id.imap_text);
        imapEditText.setText("posta.tuke.sk");
        smtpEditText = (EditText)findViewById(R.id.smtp_text);
        smtpEditText.setText("smtp.tuke.sk");
        imapPortEditText = (EditText)findViewById(R.id.imap_port);
        imapPortEditText.setText("993");
        smtpPortEditText = (EditText)findViewById(R.id.smtp_port);
        smtpPortEditText.setText("465");

        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean verified = false;
                String user = userEditText.getText().toString();
                String pwd = passEditText.getText().toString();

                String imapServer = imapEditText.getText().toString();
                String imapPort = imapPortEditText.getText().toString();
                String smtpServer = smtpEditText.getText().toString();
                String smtpPort = smtpPortEditText.getText().toString();


                acc = new AccountSettings(user,pwd);
                if(!imapPort.isEmpty()){
                    acc.setImapPort(Integer.parseInt(imapPort));
                } else {
                    acc.setImapPort(993);
                }

                if(!smtpPort.isEmpty()){
                    acc.setSmtpPort(Integer.parseInt(smtpPort));
                } else {
                    acc.setSmtpPort(465);
                }

                acc.setImapServer(imapServer);
                acc.setSmtpServer(smtpServer);

                ImapClient imapclient = new ImapClient(acc);

                new LoginTask(SigninActivity.this,acc).execute(imapclient);
            }
        });
    }

    public void startEmailActivity() {
        Intent myIntent;
        myIntent = new Intent(this, EmailActivity.class);
        myIntent.putExtra("userName",acc.getUserName());
        myIntent.putExtra("userPwd",acc.getUserPwd());
        myIntent.putExtra("smtpServer",acc.getSmtpServer());
        myIntent.putExtra("imapServer",acc.getImapServer());
        myIntent.putExtra("dwnFolder","mailclient");
        myIntent.putExtra("smtpPort",acc.getSmtpPort());
        myIntent.putExtra("imapPort",acc.getImapPort());
        myIntent.putExtra("storeMails",acc.getStoreMails());
        myIntent.putExtra("offline", true);
        startActivity(myIntent);
    }

    public AccountSettings readAccount() {
        AccountSettings accSet = null;
        String uname;
        String pwd;
        String path = getApplicationContext().getFilesDir().getAbsolutePath();
        File file = new File(path+"/emconconfig.emcc");
        try {
            Log.i("File info", "File name :" + file.getAbsolutePath());
            FileInputStream filein = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(filein);
            Object input =  oin.readObject();
            if(input instanceof AccountSettings) {
                accSet = (AccountSettings)input;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return accSet;
    }

    public void writeAccount(AccountSettings accSet) {
        String path = getApplicationContext().getFilesDir().getAbsolutePath();
        File file = new File(path+"/emconconfig.emcc");
        try {
            FileOutputStream fileout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fileout);
            oos.writeObject(accSet);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkConnection(final boolean offline) {
        boolean connected;
        connected = isOnline();
        if(connected) {
            if(acc == null) {
                showInterface();
            } else {
                ImapClient imapclient = new ImapClient(acc);
                new LoginTask(SigninActivity.this,acc).execute(imapclient);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connection unavailable");
            builder.setCancelable(false);
            if(offline) {
                builder.setMessage("No connection available, do you want to enter offline mode?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SigninActivity.this.startEmailActivity();
                    }
                });
            } else {
                builder.setMessage("No connection available, please connect to internet");
            }
            builder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SigninActivity.this.checkConnection(offline);
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SigninActivity.this.finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
