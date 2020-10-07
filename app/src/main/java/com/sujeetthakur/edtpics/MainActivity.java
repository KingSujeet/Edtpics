package com.sujeetthakur.edtpics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;

import java.io.File;
import java.util.Date;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageView camera, gallery;
    final int  REQUEST_CODE_PICKER = 100;
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;


    private AdView mAdView;

    InterstitialAd interstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        interstitialAd= new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ani);

                camera.startAnimation(animFadein);

                LongImageCameraActivity.launch( MainActivity.this );

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ani);

                gallery.startAnimation(animFadein);

                openGallery();

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE && data != null)
        {
            String imageFileName = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);

           // Toast.makeText(getApplicationContext(),imageFileName,Toast.LENGTH_LONG).show();

            EditImage(imageFileName);

//            try {
//
//                Uri imageUri = data.getData();
//
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                Cursor cursor = getContentResolver().query(imageUri,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//
//                EditImage(picturePath);
//            }catch (Exception e){
//
//
//            }


        }


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICKER){

            //String pathh = data.getStringExtra("data");

            Uri imageUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            EditImage(picturePath);



        }

        if (requestCode == PHOTO_EDITOR_REQUEST_CODE) { // same code you used while starting
//            String newFilePath = data.getStringExtra(EditImageActivity.);
//            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

            if (isImageEdit) {
                //Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
            } else {
                newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);

            }

            startActivity(new Intent(MainActivity.this, OutputImageActivity.class).putExtra("imagePath", newFilePath));

            //ImageView imageView = new ImageView(this);
            //imageView.setImageURI(Uri.parse(newFilePath));

           
            //gallery.setImageURI(Uri.parse(newFilePath));

        }
    }

    private void EditImage(String pathh) {

        try {
            File outputFile = FileUtils.genEditFile();
            Intent intent = new ImageEditorIntentBuilder(MainActivity.this, pathh, outputFile.getAbsolutePath())
                    .withAddText() // Add the features you need
                    .withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withBeautyFeature()
                    .withStickerFeature()
                    .forcePortrait(true)// Add this to force portrait mode (It's set to false by default)
                    .setSupportActionBarVisibility(false) // To hide app's default action bar
                    .build();

            EditImageActivity.start(MainActivity.this, intent, PHOTO_EDITOR_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("Demo App", e.getMessage());

            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
            // This could throw if either `sourcePath` or `outputPath` is blank or Null
        }

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE_PICKER);
    }


    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        }else{
            super.onBackPressed();
        }

    }


}