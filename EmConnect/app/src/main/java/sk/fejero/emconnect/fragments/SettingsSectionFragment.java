package sk.fejero.emconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.fejero.emconnect.R;
import sk.fejero.emconnect.management.ContainerManagement;
import sk.fejero.emconnect.management.DataLoader;

/**
 * Created by fejero on 23.10.2014.
 */
public class SettingsSectionFragment extends Fragment{

    private View linkAccountSettingsView;
    private View linkLookSettingsView;
    private View accountSettingsView;
    private View lookSettingsView;

    private LinearLayout linkAccountSettingsLayout;
    private LinearLayout linkLookSettingsLayout;
    private LinearLayout accountSettingsLayout;
    private LinearLayout lookSettingsLayout;

    private TextView linkAccountTextView;
    private TextView linkLookTextView;

    private int changedTab=0;
    private LinearLayout removedLinkLayout=null;

    private DataLoader loader;
    private ContainerManagement cm;


    public void loadLoader(DataLoader loader, ContainerManagement cm){
        this.loader = loader;
        this.cm = cm;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final LinearLayout parentLayout = (LinearLayout)rootView.findViewById(R.id.settings_list_layout);

        accountSettingsView = inflater.inflate(R.layout.account_settings_layout, parentLayout, false);
        accountSettingsLayout = (LinearLayout) accountSettingsView.findViewById(R.id.account_settings_layout);

        lookSettingsView = inflater.inflate(R.layout.look_settings_layout, parentLayout, false);
        lookSettingsLayout = (LinearLayout) lookSettingsView.findViewById(R.id.look_settings_layout);



        linkAccountSettingsView = inflater.inflate(R.layout.single_setting_layout, parentLayout, false);
        linkAccountSettingsLayout = (LinearLayout)linkAccountSettingsView.findViewById(R.id.setting_link_layout);
        linkAccountTextView = (TextView)linkAccountSettingsView.findViewById(R.id.setting_link);
        linkAccountTextView.setText("Accounts");
        parentLayout.addView(linkAccountSettingsLayout);

        linkAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(removedLinkLayout!=null){
                    parentLayout.removeViewAt(changedTab);
                    parentLayout.addView(removedLinkLayout, changedTab);
                }
                removedLinkLayout = linkAccountSettingsLayout;
                changedTab = 0;
                parentLayout.removeViewAt(0);
                initAccountSettings(accountSettingsLayout);
                parentLayout.addView(accountSettingsLayout,0);

            }
        });

        linkLookSettingsView = inflater.inflate(R.layout.single_setting_layout, parentLayout, false);
        linkLookSettingsLayout = (LinearLayout)linkLookSettingsView.findViewById(R.id.setting_link_layout);
        linkLookTextView = (TextView)linkLookSettingsView.findViewById(R.id.setting_link);
        linkLookTextView.setText("Look");
        parentLayout.addView(linkLookSettingsLayout);

        linkLookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removedLinkLayout!=null){
                    parentLayout.removeViewAt(changedTab);
                    parentLayout.addView(removedLinkLayout,changedTab);
                }
                removedLinkLayout = linkLookSettingsLayout;
                changedTab = 1;
                parentLayout.removeViewAt(1);
                parentLayout.addView(lookSettingsLayout,1);
            }
        });

        return rootView;
    }

    private void initAccountSettings(LinearLayout accountSettingsLayout){
        loader.loadAccounts(cm);
        TextView accountNameTextView = (TextView)accountSettingsLayout.findViewById(R.id.account_name_text);
        accountNameTextView.setText(loader.loadCurrentAccount(cm).getAddress());
    }
}
