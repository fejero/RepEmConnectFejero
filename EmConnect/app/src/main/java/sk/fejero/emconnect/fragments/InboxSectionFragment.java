package sk.fejero.emconnect.fragments;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sk.fejero.emconnect.R;
import sk.fejero.emconnect.SectionPagerAdapter;
import sk.fejero.emconnect.mailclient.EmailMessage;
import sk.fejero.emconnect.management.ContainerManagement;
import sk.fejero.emconnect.management.DataLoader;


/**
 * Created by fejero on 23.10.2014.
 */
public class InboxSectionFragment extends Fragment {

    private View linkView;
    private View messageView;
    private View contentView;
    private DataLoader loader;
    private ContainerManagement cm;
    private List<TextView> linkList;
    private int defaultTextColor;
    private TextView currentLinkView;
    private int currentLinkColor = Color.RED;
    private View rootView;
    private LayoutInflater inflater;
    private ViewGroup container;
    private boolean messageViewEnabled;
    private ViewPager viewPager;


    public InboxSectionFragment() {

    }

    public void loadLoader(DataLoader loader, ContainerManagement cm, ViewPager viewPager){
        this.loader = loader;
        this.cm = cm;
        this.viewPager = viewPager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        messageViewEnabled=false;
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        LinearLayout contentParentLayout = (LinearLayout)rootView.findViewById(R.id.inbox_list);
        LinearLayout leftBarParentLayout = (LinearLayout)rootView.findViewById(R.id.inbox_link_list_layout);


        linkList = new ArrayList<TextView>();

        initSideBar(inflater, leftBarParentLayout, contentParentLayout);
        /*loader.loadInbox(cm);
        loader.loadSpam(cm);
        loader.loadTrash(cm);*/
        initInboxContent(inflater,contentParentLayout,leftBarParentLayout);

        return rootView;
    }

    private void initialView(LinearLayout leftBarParentLayout, LinearLayout contentParentLayout){
        messageViewEnabled=false;
        leftBarParentLayout.removeAllViews();
        contentParentLayout.removeAllViews();

        initSideBar(inflater, leftBarParentLayout, contentParentLayout);
        /*loader.loadInbox(cm);
        loader.loadSpam(cm);
        loader.loadTrash(cm);*/
        initInboxContent(inflater,contentParentLayout,leftBarParentLayout);
    }




    private void initSideBar(final LayoutInflater inflater, final LinearLayout leftBarParentLayout, final LinearLayout contentParentLayout){

        linkView = inflater.inflate(R.layout.left_bar_link_layout, leftBarParentLayout, false);
        LinearLayout linkLayout = (LinearLayout)linkView.findViewById(R.id.link_content_layout);
        final TextView inboxLinkTextView = (TextView)linkView.findViewById(R.id.link_content);
        inboxLinkTextView.setText("Inbox");
        //inboxLinkTextView.setTextColor(Color.DKGRAY);
        final int currentTextColor = inboxLinkTextView.getCurrentTextColor();
        currentLinkView = inboxLinkTextView;

        defaultTextColor = currentTextColor;
        currentLinkView = inboxLinkTextView;
        inboxLinkTextView.setTextColor(currentLinkColor);

        inboxLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLinkView.setTextColor(defaultTextColor);
                inboxLinkTextView.setTextColor(currentLinkColor);
                currentLinkView = inboxLinkTextView;
                contentParentLayout.removeViewAt(0);
                initInboxContent(inflater, contentParentLayout,leftBarParentLayout);

            }
        });
        leftBarParentLayout.addView(linkLayout);

        linkView = inflater.inflate(R.layout.left_bar_link_layout, leftBarParentLayout, false);
        linkLayout = (LinearLayout)linkView.findViewById(R.id.link_content_layout);
        final TextView spamLinkTextView = (TextView)linkView.findViewById(R.id.link_content);
        spamLinkTextView.setText("Spam");
        spamLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLinkView.setTextColor(defaultTextColor);
                spamLinkTextView.setTextColor(currentLinkColor);
                currentLinkView = spamLinkTextView;
                contentParentLayout.removeViewAt(0);
                initSpamContent(inflater, contentParentLayout, leftBarParentLayout);
            }
        });
        leftBarParentLayout.addView(linkLayout);

        linkView = inflater.inflate(R.layout.left_bar_link_layout, leftBarParentLayout, false);
        linkLayout = (LinearLayout)linkView.findViewById(R.id.link_content_layout);
        final TextView trashLinkTextView = (TextView)linkView.findViewById(R.id.link_content);
        trashLinkTextView.setText("Trash");
        trashLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLinkView.setTextColor(defaultTextColor);
                trashLinkTextView.setTextColor(currentLinkColor);
                currentLinkView = trashLinkTextView;
                contentParentLayout.removeViewAt(0);
                initTrashContent(inflater, contentParentLayout, leftBarParentLayout);
            }
        });
        leftBarParentLayout.addView(linkLayout);

    }

    private void clearFragmentContent(LinearLayout leftBarParentLayout, final LinearLayout contentParentLayout){
        leftBarParentLayout.removeAllViews();
        contentParentLayout.removeAllViews();
        //initMessageViewLayout(leftBarParentLayout,contentParentLayout);
    }

    private void initInboxContent(LayoutInflater inflater, final LinearLayout contentParentLayout, final LinearLayout leftBarParentLayout){

        contentView = inflater.inflate(R.layout.inbox_list_layout, contentParentLayout, false);

        LinearLayout contentScrollView = (LinearLayout)contentView.findViewById(R.id.inboxScrollLayout);
        LinearLayout contentScrollLayout = (LinearLayout)contentView.findViewById(R.id.inbox_list_layout);
        contentParentLayout.addView(contentScrollView);



        for (EmailMessage m : cm.getInboxMessageList()){
            final EmailMessage actualM = m;
            messageView = inflater.inflate(R.layout.single_inbox_layout, contentScrollLayout, false);
            LinearLayout textViewLayout = (LinearLayout)messageView.findViewById(R.id.inbox_text_layout);

            TextView senderTextView = (TextView)messageView.findViewById(R.id.inbox_sender);
            senderTextView.setText(m.getAuthor());

            TextView topicTextView = (TextView)messageView.findViewById(R.id.inbox_topic);
            topicTextView.setText(m.getSubject());

            TextView contentTextView = (TextView)messageView.findViewById(R.id.inbox_content);
            contentTextView.setText(m.getContent());

            TextView dateTextView = (TextView)messageView.findViewById(R.id.inbox_date);
            dateTextView.setText(m.getSent().toString());

            textViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clearFragmentContent(leftBarParentLayout,contentParentLayout);
                    initMessageViewLayout(leftBarParentLayout, contentParentLayout, actualM,cm.getInboxMessageList());

                }
            });

            // Add the text view to the parent layout
            contentScrollLayout.addView(textViewLayout);
        }
    }



    private void initSpamContent(LayoutInflater inflater, final LinearLayout contentParentLayout, final LinearLayout leftBarParentLayout){


        contentView = inflater.inflate(R.layout.inbox_list_layout, contentParentLayout, false);

        LinearLayout contentScrollView = (LinearLayout)contentView.findViewById(R.id.inboxScrollLayout);
        LinearLayout contentScrollLayout = (LinearLayout)contentView.findViewById(R.id.inbox_list_layout);
        contentParentLayout.addView(contentScrollView);

        for (EmailMessage m : cm.getSpamMessageList()){
            final EmailMessage actualM = m;
            messageView = inflater.inflate(R.layout.single_inbox_layout, contentScrollLayout, false);
            LinearLayout textViewLayout = (LinearLayout)messageView.findViewById(R.id.inbox_text_layout);

            TextView senderTextView = (TextView)messageView.findViewById(R.id.inbox_sender);
            senderTextView.setText(m.getAuthor());

            TextView topicTextView = (TextView)messageView.findViewById(R.id.inbox_topic);
            topicTextView.setText(m.getSubject());

            TextView contentTextView = (TextView)messageView.findViewById(R.id.inbox_content);
            contentTextView.setText(m.getContent());

            TextView dateTextView = (TextView)messageView.findViewById(R.id.inbox_date);
            dateTextView.setText(m.getSent().toString());

            textViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clearFragmentContent(leftBarParentLayout,contentParentLayout);
                    initMessageViewLayout(leftBarParentLayout, contentParentLayout, actualM,cm.getSpamMessageList());

                }
            });

            // Add the text view to the parent layout
            contentScrollLayout.addView(textViewLayout);

        }
    }

    private void initTrashContent(LayoutInflater inflater, final LinearLayout contentParentLayout, final LinearLayout leftBarParentLayout){


        contentView = inflater.inflate(R.layout.inbox_list_layout, contentParentLayout, false);

        LinearLayout contentScrollView = (LinearLayout)contentView.findViewById(R.id.inboxScrollLayout);
        LinearLayout contentScrollLayout = (LinearLayout)contentView.findViewById(R.id.inbox_list_layout);
        contentParentLayout.addView(contentScrollView);

        for (EmailMessage m : cm.getTrashMessageList()){
            final EmailMessage actualM = m;
            messageView = inflater.inflate(R.layout.single_inbox_layout, contentScrollLayout, false);
            LinearLayout textViewLayout = (LinearLayout)messageView.findViewById(R.id.inbox_text_layout);

            TextView senderTextView = (TextView)messageView.findViewById(R.id.inbox_sender);
            senderTextView.setText(m.getAuthor());

            TextView topicTextView = (TextView)messageView.findViewById(R.id.inbox_topic);
            topicTextView.setText(m.getSubject());

            TextView contentTextView = (TextView)messageView.findViewById(R.id.inbox_content);
            contentTextView.setText(m.getContent());

            TextView dateTextView = (TextView)messageView.findViewById(R.id.inbox_date);
            dateTextView.setText(m.getSent().toString());

            textViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clearFragmentContent(leftBarParentLayout,contentParentLayout);
                    initMessageViewLayout(leftBarParentLayout, contentParentLayout, actualM, cm.getTrashMessageList());

                }
            });
            // Add the text view to the parent layout
            contentScrollLayout.addView(textViewLayout);
        }
    }

    private void initMessageViewLayout(final LinearLayout leftBarParentLayout, final LinearLayout contentParentLayout,EmailMessage selectedMessage, List<EmailMessage> messageList) {

        EmailMessage actualMessage = selectedMessage;
        View leftPanelView = inflater.inflate(R.layout.message_left_panel, leftBarParentLayout, false);

        LinearLayout leftPanelLayout = (LinearLayout) leftPanelView.findViewById(R.id.left_panel_layout);
        LinearLayout leftPanelScrollViewContent = (LinearLayout) leftPanelView.findViewById(R.id.scroll_view_content);
        leftBarParentLayout.addView(leftPanelLayout);
        messageViewEnabled = true;

        View returnButtonView = inflater.inflate(R.layout.message_return_button,leftBarParentLayout,false);
        ImageButton returnButton = (ImageButton)returnButtonView.findViewById(R.id.message_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialView(leftBarParentLayout,contentParentLayout);
            }
        });

        leftPanelScrollViewContent.addView(returnButton);
        for (EmailMessage m : messageList) {
            final EmailMessage actualM = m;
            messageView = inflater.inflate(R.layout.message_overview_layout, leftPanelScrollViewContent, false);
            LinearLayout textViewLayout = (LinearLayout) messageView.findViewById(R.id.message_overview_layout);

            TextView senderTextView = (TextView) messageView.findViewById(R.id.inbox_sender);
            senderTextView.setText(m.getAuthor());

            TextView topicTextView = (TextView) messageView.findViewById(R.id.inbox_topic);
            topicTextView.setText(m.getSubject());

            TextView dateTextView = (TextView) messageView.findViewById(R.id.inbox_date);
            dateTextView.setText(m.getSent().getDay() + "." + m.getSent().getMonth());

            textViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initMessageContent(contentParentLayout,actualM);
                }
            });
            // Add the text view to the parent layout
            leftPanelScrollViewContent.addView(textViewLayout);
        }

        initMessageContent(contentParentLayout,actualMessage);

    }



    private void initMessageContent(LinearLayout contentParentLayout, final EmailMessage selectedMessage){
        contentParentLayout.removeAllViews();
        View messageView = inflater.inflate(R.layout.message_layout, contentParentLayout, false);
        LinearLayout messageLayout = (LinearLayout)messageView.findViewById(R.id.message_layout);

        TextView dateView= (TextView)messageView.findViewById(R.id.date);
        dateView.setText(selectedMessage.getSent().toString());
        TextView senderView= (TextView)messageView.findViewById(R.id.sender);
        senderView.setText(selectedMessage.getAuthor());
        TextView subjectView= (TextView)messageView.findViewById(R.id.subject);
        subjectView.setText(selectedMessage.getSubject());
        TextView contentView= (TextView)messageView.findViewById(R.id.content);
        contentView.setText(selectedMessage.getContent());


        Button replyButton = (Button)messageView.findViewById(R.id.reply_button);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailMessage reply = new EmailMessage();
                reply.setTo(selectedMessage.getAuthor());
                reply.setSubject("[reply]" + selectedMessage.getSubject());
                cm.setTempMessage(reply);
                viewPager.setCurrentItem(2);
            }
        });
        Button resendButton = (Button)messageView.findViewById(R.id.resend_button);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailMessage resend = new EmailMessage();
                resend.setSubject(selectedMessage.getSubject());
                resend.setContent(selectedMessage.getContent());
                cm.setTempMessage(resend);
                viewPager.setCurrentItem(2);

            }
        });
        contentParentLayout.addView(messageLayout);
    }
}
