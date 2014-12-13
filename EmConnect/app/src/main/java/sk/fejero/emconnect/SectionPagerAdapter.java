package sk.fejero.emconnect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import sk.fejero.emconnect.fragments.CurrentFragment;
import sk.fejero.emconnect.fragments.InboxSectionFragment;
import sk.fejero.emconnect.fragments.MessageViewFragment;
import sk.fejero.emconnect.fragments.NewMessageSectionFragment;
import sk.fejero.emconnect.fragments.SentSectionFragment;
import sk.fejero.emconnect.fragments.SettingsSectionFragment;
import sk.fejero.emconnect.mailclient.outcomming.SmtpClient;
import sk.fejero.emconnect.management.ContainerManagement;
import sk.fejero.emconnect.management.DataLoader;

/**
 * Created by fejero on 23.10.2014.
 */
public class SectionPagerAdapter extends FragmentPagerAdapter {

    private DataLoader loader;
    private ContainerManagement cm;
    private FragmentManager fm;
    private ViewPager viewPager;
    private SmtpClient smtpClient;

    public SectionPagerAdapter(FragmentManager fm, DataLoader loader,ContainerManagement cm, SmtpClient smtpClient) {
        super(fm);
        this.fm = fm;
        //this.newMessageModel = newMessageModel;
        this.loader = loader;
        this.cm = cm;
        this.smtpClient = smtpClient;

    }

    public void loadPagerView(ViewPager viewPager){
        this.viewPager = viewPager;
    }


    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                InboxSectionFragment fragment = new InboxSectionFragment();
                fragment.loadLoader(loader,cm,viewPager);
                //MessageViewFragment fragment = new MessageViewFragment();
                return fragment;
            case 1:
                SentSectionFragment sentFragment = new SentSectionFragment();
                sentFragment.loadLoader(loader,cm,viewPager);
                return sentFragment;
            case 2:
                NewMessageSectionFragment newMessagefragment = new NewMessageSectionFragment();
                newMessagefragment.loadModel(loader,cm, smtpClient);
                return newMessagefragment;
            case 3:
                SettingsSectionFragment settingsSectionFragment = new SettingsSectionFragment();
                settingsSectionFragment.loadLoader(loader,cm);
                return settingsSectionFragment;
            default:
                return new NewMessageSectionFragment();
        }
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Inbox";
            case 1:
                return "Sent";
            case 2:
                return "New Message";
            case 3:
                return "Settings";
            default: return "Inbox";
        }
    }
}
