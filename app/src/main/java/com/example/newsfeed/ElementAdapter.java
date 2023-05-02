package com.example.newsfeed;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder> {
    private final List<Element> listFull;
    private List<Element> ElementList;
    private OnItemClickListener mOnItemClickListener;

    public ElementAdapter(List<Element> ElementList) {
        this.ElementList = ElementList;
           listFull=new ArrayList<>(ElementList);
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        Element Element = ElementList.get(position);
        holder.titleTextView.setText(Element.getTitle());
        holder.descriptionTextView.setText(Element.getDescription());
        holder.authorTextView.setText("Author : "+Element.getAuthor());
        holder.publishedAtTextView.setText("Published at : "+Element.getPublishedAt());

        //if(Element.getUrlToImg()!=null)
       // Log.d("ffefrnnn",Element.getUrlToImg());
       // Glide.with(holder.element_image.getContext()).load(Element.getUrlToImg()).into(holder.element_image);
        Glide.with(holder.element_image.getContext())
                .load(Element.getUrlToImg())
                .timeout(10000)
                .placeholder(R.drawable.loading) // Image de chargement à afficher
                .error(R.drawable.news) // Image par défaut à afficher en cas d'erreur
                .into(holder.element_image);

        // Set the OnClickListener on the CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(Element);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ElementList.size();
    }


    public static class ElementViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView authorTextView;
        public TextView publishedAtTextView;

        ImageView element_image;

        public ElementViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            publishedAtTextView = itemView.findViewById(R.id.publishedAtTextView);
            element_image=itemView.findViewById(R.id.element_image);
        }
    }
    public List<Element> getElementList() {
        return ElementList;
    }


    public void setElementList(List<Element> ElementList) {
        this.ElementList = ElementList;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public void filter(String text,ArrayList<Element> fullElemntAdapter) {
        List<Element> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            filteredList.addAll(fullElemntAdapter);
        } else {
            String searchText = text.toLowerCase();

            for (Element element : fullElemntAdapter) {
                if (element.getTitle().toLowerCase().contains(searchText) || element.getDescription().toLowerCase().contains(searchText)
                        ) {
                    filteredList.add(element);
                }
            }
        }
        ElementList.clear();
        ElementList.addAll(filteredList);
        notifyDataSetChanged();
    }

    /*public void sortData(String sortBy,ArrayList<Element> fullElemntAdapter) {
        switch (sortBy.toLowerCase()) {
            case "name":
                Collections.sort(ElementList, new Comparator<Element>() {
                    @Override
                    public int compare(Element o1, Element o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case "rating":
                Collections.sort(ElementList, new Comparator<Element>() {
                    @Override
                    public int compare(Element o1, Element o2) {
                        return o2.getRating().compareTo(o1.getRating());
                    }
                });
                break;
            case "none":
                ElementList.clear();
                ElementList.addAll(fullElemntAdapter);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy argument: " + sortBy);
        }
        notifyDataSetChanged();
    }*/

}
