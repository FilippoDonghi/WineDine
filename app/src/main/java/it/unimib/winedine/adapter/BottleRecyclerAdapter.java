package it.unimib.winedine.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;


public class BottleRecyclerAdapter extends RecyclerView.Adapter<BottleRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Bottle> bottleList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;
        private final TextView textViewScore;
        private final TextView textViewPrice;
        private final ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            imageView = view.findViewById(R.id.imageView);
        }


        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextViewScore() {
            return textViewScore;
        }

        public TextView getTextViewPrice() {
            return textViewPrice;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public BottleRecyclerAdapter(int layout, List<Bottle> bottleList) {
        this.layout = layout;
        this.bottleList = bottleList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(bottleList.get(position).getTitle());
        viewHolder.getTextViewScore().setText(bottleList.get(position).getScore());
        viewHolder.getTextViewPrice().setText(bottleList.get(position).getPrice());



        context = viewHolder.getImageView().getContext();
        Glide.with(context)
                .load(bottleList.get(position).getImageUrl())
                .placeholder(new ColorDrawable(context.getColor(R.color.placeholder_gray)))
                .into(viewHolder.getImageView());
    }

    @Override
    public int getItemCount() {
        return bottleList.size();
    }
}
