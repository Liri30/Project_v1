package com.learning.manuelliriano.project_v1;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {
    private Context context;
    ImageView imageView_myimage;
    private Listener listener;

    public void setListadoProyectos(List<ImageUploadInfo> listadoImage) {
        this.listadoImage = listadoImage;
    }

    private List<ImageUploadInfo> listadoImage;


    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> listadoImage,Listener listener) {
        this.listadoImage = listadoImage;
        this.context = context;
        this.listener=listener;

    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_layout, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        FirebaseStorage firebaseStorageInstacia = FirebaseStorage.getInstance();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(listadoImage.get(i));
            }
        });
        TextView comentario = holder.itemView.findViewById(R.id.my_description);
        comentario.setText(listadoImage.get(i).getComentario());
        imageView_myimage = holder.itemView.findViewById(R.id.my_image);
        Log.wtf("IMAGEN URL",listadoImage.get(i).getImageName());

        firebaseStorageInstacia.getReference().child("images/"+listadoImage.get(i).getImageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri.toString()).into(imageView_myimage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    @Override
    public int getItemCount() {
        return listadoImage.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        public Holder(@NonNull View itemView) {
            super(itemView);
        }


    }

    public interface Listener{
        void onClick(ImageUploadInfo imageUploadInfo);
    }

}
