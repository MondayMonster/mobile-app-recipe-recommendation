package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class aParentAdapterClass extends RecyclerView.Adapter<aParentAdapterClass.ViewHolder> {

    List<rvParentModelClass> rvParentModelClassList;
    Context context;

    public aParentAdapterClass(List<rvParentModelClass> rvParentModelClassList, Context context) {
        this.rvParentModelClassList = rvParentModelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public aParentAdapterClass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rv_parent_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull aParentAdapterClass.ViewHolder holder, int position) {
        //holder.ivChildImage.setImageResource(rvParentModelClassList.get(position).image);
        //holder.ivChildImage.setOnClickListener(); //ToDO direct to recipe page
        holder.foodCategoryTitle.setText(rvParentModelClassList.get(position).foodCategory);
        aChildAdapterClass aChildAdapter;
        aChildAdapter=new aChildAdapterClass(rvParentModelClassList.get(position).rvChildModelClassList, context);
        holder.rvChild.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
        holder.rvChild.setAdapter(aChildAdapter);
        aChildAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return rvParentModelClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvChild;
        TextView foodCategoryTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvChild=itemView.findViewById(R.id.rv_child);
            foodCategoryTitle=itemView.findViewById(R.id.food_category_title);
        }
    }
}
