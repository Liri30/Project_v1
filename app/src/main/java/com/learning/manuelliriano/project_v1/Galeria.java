package com.learning.manuelliriano.project_v1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Galeria extends AppCompatActivity implements RecyclerViewAdapter.Listener {

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;
    private EditText edit_comentario;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private LocationManager locationManager;

    //    private Location location;
    //para identificar el request de los permisos
    private static final int REQUEST_LOCATION_PERMISSION = 1000;

    private final int PICK_IMAGE_REQUEST = 71;

    RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference imagenes;
    private FirebaseDatabase firebaseDatebaseInstacia;

    List<ImageUploadInfo> listadoImagenes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            openActivity(new Intent(this, MainActivity.class));
        }
        //Initialize Views
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imgView);
        edit_comentario = findViewById(R.id.edit_comentario);

        //FIREBASE
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        //obtener el location manager desde el sistema
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (!hasPermission()) this.requestForPermission();
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });



        initializeUI();
        readData();



        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),listadoImagenes,this);
        RecyclerView rv= findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(recyclerViewAdapter);
    }
    private void readData(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                    listadoImagenes.add(imageUploadInfo);
                }

                recyclerViewAdapter.setListadoProyectos(listadoImagenes);
                recyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        imagenes.addValueEventListener(postListener);
    }

    @Override
    public void onClick(ImageUploadInfo imageUploadInfo) {
        Intent intent = new Intent(Galeria.this, ImagenActivity.class);
        intent.putExtra("idImagen",imageUploadInfo.getId());
        startActivity(intent);
    }
    private void openActivity(Intent intent){
        startActivity(intent);
    }

    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openActivity(intent);
        finish();
        openActivity(new Intent(this, MainActivity.class));
    }

    private void initializeUI() {
        firebaseDatebaseInstacia = FirebaseDatabase.getInstance();
        imagenes = firebaseDatebaseInstacia.getReference("ImageUpload");

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String idImage = "";
            idImage = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/" + idImage);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Galeria.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Galeria.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });



//            String ubicacion = location.getLatitude()+", "+ location.getLongitude();
            String comentario = edit_comentario.getText().toString();
            ImageUploadInfo usuario =  new ImageUploadInfo(firebaseAuth.getUid(),idImage,comentario,"Santo Domingo", firebaseAuth.getCurrentUser().toString());

            firebaseDatabase.getReference("ImageUpload").child(usuario.getId()).setValue(usuario);
            Toast.makeText(this, "Santo Domingo", Toast.LENGTH_SHORT).show();
        }
    }

    //solicitar permisos para usar la ubicacion
    private void requestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    //verificar si tengo permisos
    private boolean hasPermission() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //mostrar el location en el textview
    private void showLocation(Location location){
        // textView.setText(String.format("%s,%s", location.getLatitude(), location.getLongitude()));
    }

    //mostrar la ultima ubicacion
    @SuppressLint("MissingPermission")
    public void showLastKnowLocation(View view) {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        showLocation(location);
    }

    //actualizar loction
  /*  @SuppressLint("MissingPermission")
    public void updateLocation(View view){
        long minTime      = 1000;
        float minDistance = 0.f;
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                this
        );
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // asumimos que el usuario acepto los permisso
        // de querer manejar esto mejor ver la siguiente documentacion
        // https://developer.android.com/guide/topics/permissions/overview
        showLastKnowLocation(null);
    }

    //BEGIN METHODS LISTENER LOCATION
    //================================================================================
 /*   @Override
    public void onLocationChanged(Location location) {
        showLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.wtf(TAG, provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.wtf(TAG, provider);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.wtf(TAG, provider);

    }*/
    //END METHODS LISTENER LOCATION
    //================================================================================

}
