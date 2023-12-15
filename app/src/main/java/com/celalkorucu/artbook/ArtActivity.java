package com.celalkorucu.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.celalkorucu.artbook.databinding.ActivityArtBinding;
import com.celalkorucu.artbook.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArtActivity extends AppCompatActivity {


    Bitmap selectedImage ;
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
SQLiteDatabase database ;

    private ActivityArtBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
        database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new")){

            binding.artName.setText("");
            binding.artistName.setText("");
            binding.year.setText("");
            binding.button.setVisibility(View.VISIBLE);

            Bitmap selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selectimage);
            binding.imageView2.setImageBitmap(selectedImage);
        }else{

            int artId = intent.getIntExtra("artId",0);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT*FROM arts WHERE id=?", new String[] {String.valueOf(artId)});

                int artNameIx = cursor.getColumnIndex("artName");
                int artistNameIx = cursor.getColumnIndex("artistName");
                int yearIx  = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    binding.artName.setText(cursor.getString(artNameIx));
                    binding.artistName.setText(cursor.getString(artistNameIx));
                    binding.year.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView2.setImageBitmap(bitmap);
                }

                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        try {
            Cursor cursor = database.rawQuery("SELECT*FROM arts",null);

            int artNameIx = cursor.getColumnIndex("artName");
            int artistNameIx = cursor.getColumnIndex("artistName");
            int yearIx = cursor.getColumnIndex("year");
            int imageIx = cursor.getColumnIndex("image");

            cursor.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void save(View view){

        String artName = binding.artName.getText().toString();
        String artistName = binding.artistName.getText().toString();

        String year = binding.year.getText().toString();

        Bitmap smallImage = makesmallImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY , artName VARCHAR , artistName VARCHAR, year VARCHAR ,image BLOB)");

            String query = "INSERT INTO arts (artName,artistName,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(query);
            sqLiteStatement.bindString(1,artName);
            sqLiteStatement.bindString(2,artistName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(ArtActivity.this,MainActivity.class);
        //Bütün aktiviteleri kapatıyor
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //finished

    }

    public Bitmap makesmallImage(Bitmap image , int maxSize){

        int wight = image.getWidth();
        int height = image.getHeight();

        double bitmapRatio = (double) wight/ (double) height;

        if(bitmapRatio >1){
            wight=maxSize;
            height = (int)(wight/bitmapRatio);

        }
        else{
            height = maxSize ;
            wight = (int) (height*bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,wight,height,true);
    }

    public void selectImage(View view) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("izin verilmemiş");
                //İzin Gerekçesi
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {

                    Snackbar.make(view, "Permisson needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permisson", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {

                    //Permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                //toGallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentActivityResultLauncher.launch(intentToGallery);
            }
            //Permission Not (İzin verilmedi)

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //İzin Gerekçesi
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permisson needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permisson", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    //Permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                //toGallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentActivityResultLauncher.launch(intentToGallery);
            }

        }


    }

    public void registerLauncher () {
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();

                        if (Build.VERSION.SDK_INT >= 28) {
                            ImageDecoder.Source source = ImageDecoder.createSource(ArtActivity.this.getContentResolver(), imageData);
                            try {
                                selectedImage = ImageDecoder.decodeBitmap(source);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            binding.imageView2.setImageBitmap(selectedImage);
                        } else {
                            try {
                                selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(), imageData);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            binding.imageView2.setImageBitmap(selectedImage);
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {

                    //Permission Granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentActivityResultLauncher.launch(intentToGallery);
                } else {

                    //Permission Denied
                    Toast.makeText(ArtActivity.this, "Permission needed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public  void ImageEdited(int height , int wight){
        double ratio = (double) wight/height;

      if(height>300 && wight>200){
          if(ratio>1){
              wight = 200 ;
              height = (int) (wight/ratio) ;
          }
          else{
              height=300 ;
              wight = (int)(height*ratio);
          }
      }
      else if(wight>200){
          wight = 200 ;
          height = (int) (wight/ratio) ;
      }
      else{
          height=300 ;
          wight = (int)(height*ratio);
      }

      binding.imageView2.setMaxHeight(height);
      binding.imageView2.setMaxWidth(wight);
    }
}