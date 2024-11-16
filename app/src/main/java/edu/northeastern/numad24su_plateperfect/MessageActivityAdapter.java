package edu.northeastern.numad24su_plateperfect;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageActivityAdapter extends RecyclerView.Adapter<MessageActivityAdapter.MessageViewHolder> {

    Context context;
    ArrayList<ChatMessage> messagedRecipes;
    private final String currentUser;

    public MessageActivityAdapter(Context context, ArrayList<ChatMessage> messagedRecipes,
                                  String currentUser) {
        this.context = context;
        this.messagedRecipes = messagedRecipes;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.chatmsg_recycler_row, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage msg = messagedRecipes.get(position);
        String message = "Hey! Try this new Recipe " + msg.getMessage();
        if (msg.getSender().equals(currentUser)) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatRecipeName.setText(message);
            holder.rightChatRecipeName.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("recipeName", msg.getMessage());
                startActivity(context, intent, null);
            });
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatRecipeName.setText(message);
            holder.leftChatRecipeName.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("recipeName", msg.getMessage());
                startActivity(context, intent, null);
            });
        }

    }

    @Override
    public int getItemCount() {
        return messagedRecipes.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatRecipeName;
        TextView rightChatRecipeName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatRecipeName = itemView.findViewById(R.id.left_chat_textview);
            rightChatRecipeName = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
