package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsActivityAdapter extends RecyclerView.Adapter<ChatsActivityAdapter.ChatViewHolder> {
    Context context;
    ArrayList<User> firebaseRTBDUserModelArrayListResponseModelList;
    IMessageDisplayListener iMessageDisplayListener;


    public ChatsActivityAdapter(Context context, ArrayList<User> firebaseRTBDUserModelArrayListResponseModelList, IMessageDisplayListener iMessageDisplayListener) {
        this.context = context;
        this.firebaseRTBDUserModelArrayListResponseModelList = firebaseRTBDUserModelArrayListResponseModelList;
        this.iMessageDisplayListener = iMessageDisplayListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_chat_row, parent, false);

        return new ChatViewHolder(view,iMessageDisplayListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User model = firebaseRTBDUserModelArrayListResponseModelList.get(position);
        holder.usernameTextVw.setText(model.getUsername());
    }

    @Override
    public int getItemCount() {
        return firebaseRTBDUserModelArrayListResponseModelList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {


        TextView usernameTextVw;

        public ChatViewHolder(@NonNull View itemView, IMessageDisplayListener iMessageDisplayListener) {
            super(itemView);
            usernameTextVw = itemView.findViewById(R.id.textViewUsername);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iMessageDisplayListener!=null){
                        int pos = getAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION){
                            iMessageDisplayListener.onUserClicked(pos, usernameTextVw.getText().toString());
                        }

                    }
                }
            });
        }
    }
}
