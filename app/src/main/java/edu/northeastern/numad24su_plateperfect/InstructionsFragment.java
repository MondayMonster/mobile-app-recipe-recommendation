package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InstructionsFragment extends Fragment {

    private static final String ARG_RECIPE_NAME = "recipe_name";
    private String recipeName;
    private DatabaseReference databaseReference;
    private LinearLayout instructionsLayout;
    private List<Instruction> pendingInstructions = new ArrayList<>();


    public InstructionsFragment() {
        // Required empty public constructor
    }

    public static InstructionsFragment newInstance(String recipeName) {
        InstructionsFragment fragment = new InstructionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_NAME, recipeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(ARG_RECIPE_NAME);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("instuctions");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructions, container, false);
        instructionsLayout = view.findViewById(R.id.instructions_layout);
        Log.d("InstructionsFragment", "Recipe Name: " + recipeName);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch and display instructions
        fetchInstructions();
    }


    private void fetchInstructions() {
        databaseReference.orderByChild("recipe").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingInstructions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String instruction = snapshot.child("instruction").getValue(String.class);
                    Long step = snapshot.child("step").getValue(Long.class);
                    if (instruction != null && step != null) {
                        pendingInstructions.add(new Instruction(step, instruction));
                    }
                }
                if (isAdded()) {
                    addPendingInstructions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void addPendingInstructions() {
        for (Instruction instruction : pendingInstructions) {
            addInstructionToLayout(instruction.step, instruction.text);
        }
        pendingInstructions.clear();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!pendingInstructions.isEmpty()) {
            addPendingInstructions();
        }
    }

    private static class Instruction {
        Long step;
        String text;

        Instruction(Long step, String text) {
            this.step = step;
            this.text = text;
        }
    }

    private void addInstructionToLayout(Long step, String instruction) {
        View instructionView = getLayoutInflater().inflate(R.layout.item_instruction, instructionsLayout, false);
        TextView instructionStep = instructionView.findViewById(R.id.instruction_step);
        TextView instructionText = instructionView.findViewById(R.id.instruction_text);

        instructionStep.setText("Step " + step);
        instructionText.setText(instruction);

        instructionsLayout.addView(instructionView);
    }


}
