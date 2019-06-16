package com.learning.manuelliriano.project_v1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ImagenActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatebaseInstacia;
    DatabaseReference imagenSeleccionada;
    FirebaseStorage firebaseStorageInstacia;
    ImageView my_image;
    TextView descripcion;

    ImageUploadInfo imageUploadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

        my_image = (ImageView)findViewById(R.id.my_image);
        descripcion = (TextView)findViewById(R.id.comentario);
        firebaseDatebaseInstacia = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        String idImagen = intent.getStringExtra("idImagen");
        firebaseStorageInstacia = FirebaseStorage.getInstance();
        imagenSeleccionada = firebaseDatebaseInstacia.getReference("ImageUpload").child(idImagen);

        readData();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Oferta de trabajo!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, imageUploadInfo.getComentario());
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openActivity(Intent intent){
        startActivity(intent);
    }
    private void readData(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageUploadInfo imageUploadInfo_1 = dataSnapshot.getValue(ImageUploadInfo.class);
                imageUploadInfo=imageUploadInfo_1;
                descripcion.setText(imageUploadInfo.getComentario());

                firebaseStorageInstacia.getReference().child("images/"+imageUploadInfo.getImageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.get().load(uri.toString()).into(my_image);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        imagenSeleccionada.addValueEventListener(postListener);
    }
}
