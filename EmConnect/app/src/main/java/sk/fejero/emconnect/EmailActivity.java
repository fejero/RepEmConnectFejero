package sk.fejero.emconnect;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import java.io.File;

import javax.mail.MessagingException;

import sk.fejero.emconnect.asynctasks.EmailDownloaderTask;
import sk.fejero.emconnect.fragments.NewMessageSectionFragment;
import sk.fejero.emconnect.mailclient.AccountSettings;
import sk.fejero.emconnect.mailclient.incomming.ImapClient;
import sk.fejero.emconnect.mailclient.outcomming.SmtpClient;
import sk.fejero.emconnect.management.ContainerManagement;
import sk.fejero.emconnect.management.DataLoader;

public class EmailActivity extends FragmentActivity implements ActionBar.TabListener {


    private SectionPagerAdapter mAppSectionsPagerAdapter;
    private ContainerManagement containerManagement;
    private DataLoader loader;
    private ViewPager mViewPager;
    private int cf;
    private AccountSettings acc;
    private ImapClient imapClient;
    private SmtpClient smtpClient;
    private boolean offline;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        setContentView(R.layout.activity_email);
        containerManagement = new ContainerManagement();
        String dwnf = this.getApplicationContext().getFilesDir().getAbsolutePath()+"/";
        loader = new DataLoader(dwnf);

        Intent intent = getIntent();

        acc = new AccountSettings(intent.getStringExtra("userName"),intent.getStringExtra("userPwd"));
        acc.setSmtpServer(intent.getStringExtra("smtpServer"));
        acc.setImapServer(intent.getStringExtra("imapServer"));
        acc.setImapPort(intent.getIntExtra("imapPort", -1));
        acc.setSmtpPort(intent.getIntExtra("smtpPort", -1));
        acc.setDwnFolder(dwnf+intent.getStringExtra("dwnFolder")+"/");
        final File dir = new File(acc.getDwnFolder());
        dir.mkdirs();
        acc.setStoreMails(intent.getIntExtra("storeMails", -1));
        acc.setAuthor(intent.getStringExtra("author"));
        offline = intent.getBooleanExtra("offline",false);

        imapClient = new ImapClient(acc);
        smtpClient = new SmtpClient(acc);
        try {
            imapClient.initStore();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        mAppSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),loader,containerManagement, smtpClient);
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mAppSectionsPagerAdapter.loadPagerView(mViewPager);


        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        if (offline) {
            loader.loadAllFolders(containerManagement);
        } else {
            new EmailDownloaderTask(containerManagement,imapClient,dwnf).execute("Inbox","Sent","Trash");
        }

        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.

        mViewPager.setCurrentItem(tab.getPosition());
        cf = tab.getPosition();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_activity_action, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){

            switch(cf){
                case 0:
                    /*loader.loadInbox(containerManagement);
                    loader.loadSpam(containerManagement);
                    loader.loadTrash(containerManagement);*/
                    break;
                case 1:
                    /*loader.loadSent(containerManagement);
                    loader.loadConcepts(containerManagement);*/
                    break;
                case 2:
                    if(containerManagement.getTempMessage() != null) {
                        ((NewMessageSectionFragment)mAppSectionsPagerAdapter.getItem(2)).setTempMessage(containerManagement.getTempMessage());
                    }
                    break;
                case 3:
                    break;
                default:
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOffline() {
        return offline;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            imapClient.closeStore();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
