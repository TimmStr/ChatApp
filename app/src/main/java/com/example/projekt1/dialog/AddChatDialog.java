package com.example.projekt1.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.projekt1.R;
import com.example.projekt1.models.Session;
import com.example.projekt1.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AddChatDialog extends AppCompatDialogFragment{

    // dialog elements
    EditText chatTitleEditText;
    Button addUserButton;
    Spinner spinner;

    // to pass data back to activity
    ChatDialogListener chatDialogListener;
    ArraySet<String> users = new ArraySet<>();

    // set session
    Session session;

    // Setup Firebase-Database
    FirebaseDatabase root =  FirebaseDatabase.getInstance();
    DatabaseReference userRef = root.getReference("User");

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_chat_dialog, null);

        // init session
        session = new Session(requireActivity().getApplicationContext());

        // init Dialog elements
        chatTitleEditText = view.findViewById(R.id.editTextAddUser);
        spinner = view.findViewById(R.id.spinner2);
        addUserButton = view.findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem() == null || spinner.getSelectedItem().toString().isEmpty()) {
                    Toast.makeText(requireActivity().getApplicationContext(), "No selection", Toast.LENGTH_SHORT).show();
                    return;
                }
                users.add(spinner.getSelectedItem().toString());
                Toast.makeText(requireActivity().getApplicationContext(), "User added", Toast.LENGTH_SHORT).show();
            }
        });

        // fetch users from
        this.userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String[] arr = new String[(int) snapshot.getChildrenCount()];
                int index = 0;
                for(DataSnapshot userDs : snapshot.getChildren()){
                    User user = userDs.getValue(User.class);
                    arr[index] = user.getUserName();
                    index++;
                }

                //build dropdown from all users
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_item , arr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // user-hashset to array
        // String[] arr = session.getUsers().toArray(new String[0]);

        // build Dialog
        builder.setView(view)
                .setTitle("Add Chat - Dialog")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chatTitle = chatTitleEditText.getText().toString();
                        // initial user
                        users.add(session.getUserName());
                        chatDialogListener.applyData(chatTitle, users);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            chatDialogListener = (ChatDialogListener) context;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface ChatDialogListener{
        public void applyData(String chatTitle, ArraySet<String> users);
    }
}

