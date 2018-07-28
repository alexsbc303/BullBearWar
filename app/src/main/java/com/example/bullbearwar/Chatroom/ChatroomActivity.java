package com.example.bullbearwar.Chatroom;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bullbearwar.Firebase.FirebaseMaster;
import com.example.bullbearwar.Firebase.Login_Fragment;
import com.example.bullbearwar.Firebase.TradingMasterActivity;
import com.example.bullbearwar.Firebase.Utils;
import com.example.bullbearwar.Navigation.NavigationActivity;
import com.example.bullbearwar.News.NewsActivity;
import com.example.bullbearwar.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.currentUser;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatroomActivity extends android.support.v4.app.Fragment {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_chatroom;


    //Add Emojicon
    EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViewsInLayout();
        }

        View mView = inflater.inflate(R.layout.activity_chatroom, container, false);

        activity_chatroom = (RelativeLayout) mView.findViewById(R.id.activity_chatroom);

        //Add Emoji
        emojiButton = (ImageView) mView.findViewById(R.id.emoji_button);
        submitButton = (ImageView) mView.findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText) mView.findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getActivity().getApplicationContext(), activity_chatroom, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("chatRoomRecord") .push().setValue(new ChatMessage(emojiconEditText.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                emojiconEditText.setText("");
                emojiconEditText.requestFocus();
            }
        });

        //Check if not sign-in then navigate Signin page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

//            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("logInSource","chartroom");
            editor.commit();

            FirebaseMaster login = new FirebaseMaster();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, login).commit();
        } else {

                //Snackbar.make(activity_chatroom,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
                //Load content
                ListView listOfMessage = (ListView) mView.findViewById(R.id.list_of_message);
                adapter = new FirebaseListAdapter<ChatMessage>(this.getActivity(), ChatMessage.class, R.layout.chatitem, FirebaseDatabase.getInstance().getReference().child("chatRoomRecord")) {
                    @Override
                    protected void populateView(View view, ChatMessage model, int position) {

                        //Get references to the views of list_item.xml
                        TextView messageText, messageUser, messageTime;
                        messageText = (EmojiconTextView) view.findViewById(R.id.message_text);
                        messageUser = (TextView) view.findViewById(R.id.message_user);
                        messageTime = (TextView) view.findViewById(R.id.message_time);

                        messageText.setText(model.getMessagetext());
                        messageUser.setText(model.getMessageuser());


                            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessagetime()));

                    }
                };
                Log.e("adapter",String.valueOf( adapter.getCount()));
                listOfMessage.setAdapter(adapter);
//                listOfMessage.removeViewAt(listOfMessag);

        }

        //actionbar title
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Chatroom");

        //Signout Chat
        setHasOptionsMenu(true);

        return mView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_chatroom, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            });
            restartApp();

        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chatroom_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Snackbar.make(activity_chatroom, "Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                //Load content
                ListView listOfMessage = (ListView) getView().findViewById(R.id.list_of_message);
                adapter = new FirebaseListAdapter<ChatMessage>(this.getActivity(), ChatMessage.class, R.layout.chatitem, FirebaseDatabase.getInstance().getReference().child("chatRoomRecord")) {
                    @Override
                    protected void populateView(View view, ChatMessage model, int position) {

                        //Get references to the views of list_item.xml
                        TextView messageText, messageUser, messageTime;
                        messageText = (EmojiconTextView) view.findViewById(R.id.message_text);
                        messageUser = (TextView) view.findViewById(R.id.message_user);
                        messageTime = (TextView) view.findViewById(R.id.message_time);

                        messageText.setText(model.getMessagetext());
                        messageUser.setText(model.getMessageuser());

                            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessagetime()));



                    }
                };
                listOfMessage.setAdapter(adapter);
            }
            setHasOptionsMenu(true);
        } else {
            Snackbar.make(activity_chatroom, "We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    public void restartApp() {
        Intent intent = new Intent(getActivity().getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
    }
}



