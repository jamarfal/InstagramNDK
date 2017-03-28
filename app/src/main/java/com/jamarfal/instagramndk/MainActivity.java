package com.jamarfal.instagramndk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("image_processing");
    }

    private static Uri uriFile;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Bitmap bitmapOriginal = null;
    private Bitmap bitmapGrey = null;
    private Bitmap bitmapSepia = null;
    private Bitmap bitmapFrame = null;
    private Bitmap bitmapSobel = null;
    private Button toFacebookButton;
    private Button toSharedialogButton;
    //public static Bitmap bitmapToShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageview_camera);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(onClickListenerCamera);

        toFacebookButton = (Button) findViewById(R.id.button_facebook);
        toFacebookButton.setOnClickListener(toFacebookCLickListener);

        toSharedialogButton = (Button) findViewById(R.id.button_sharedialog);
        toSharedialogButton.setOnClickListener(toShareDialogClickListener);

        bitmapOriginal = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case R.id.action_grey_photo:
                convertGrey();
                return true;
            case R.id.action_sepia_photo:
                convertSepia();
                return true;
            case R.id.action_frame_photo:
                addFrame();
                return true;
            case R.id.action_sobel_photo:
                convertSobel();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setPic();
                }
                break;
        }
    }

    private View.OnClickListener onClickListenerCamera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePicture();
        }
    };

    private View.OnClickListener toFacebookCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                //Write file
//                String filename = "bitmap.png";
//                FileOutputStream stream = MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
//                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                //stream.close();
                //bitmap.recycle();

                //Pop intent
                Intent in1 = new Intent(MainActivity.this, MyFacebookActivity.class);
                //in1.putExtra("image", filename);

                startActivity(in1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener toShareDialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent toShareDialogIntent = new Intent(MainActivity.this, ShareDialogActivity.class);
            startActivity(toShareDialogIntent);
        }
    };


    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uriFile = Uri.fromFile(getOutputMediaFile());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFile);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uriFile.getEncodedPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uriFile.getEncodedPath(), bmOptions);
        bitmapOriginal = bitmap;
        BaseApplication.get().setBitmapToShare(bitmapOriginal);
        mImageView.setImageBitmap(bitmap);
    }

    private void convertGrey() {
        bitmapGrey = Bitmap.createBitmap(bitmapOriginal.getWidth(),
                bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        convertImageToGrey(bitmapOriginal, bitmapGrey);
        BaseApplication.get().setBitmapToShare(bitmapGrey);
        mImageView.setImageBitmap(bitmapGrey);
    }


    private void convertSepia() {
        bitmapSepia = Bitmap.createBitmap(bitmapOriginal.getWidth(),
                bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        convertImageToSepia(bitmapOriginal, bitmapSepia);
        BaseApplication.get().setBitmapToShare(bitmapSepia);

        mImageView.setImageBitmap(bitmapSepia);
    }

    private void addFrame() {
        bitmapFrame = Bitmap.createBitmap(bitmapOriginal.getWidth(),
                bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        addFrameToImage(bitmapOriginal, bitmapFrame);
        BaseApplication.get().setBitmapToShare(bitmapFrame);
        mImageView.setImageBitmap(bitmapFrame);
    }

    private void convertSobel() {
        bitmapGrey = Bitmap.createBitmap(bitmapOriginal.getWidth(),
                bitmapOriginal.getHeight(), Bitmap.Config.ALPHA_8);
        bitmapSobel = Bitmap.createBitmap(bitmapOriginal.getWidth(),
                bitmapOriginal.getHeight(), Bitmap.Config.ALPHA_8);
        convertImageToGreyScale(bitmapOriginal, bitmapGrey);
        convertImageToSobel(bitmapGrey, bitmapSobel);
        BaseApplication.get().setBitmapToShare(bitmapSobel);
        mImageView.setImageBitmap(bitmapSobel);

    }


    //region Native functions
    public native void convertImageToGreyScale(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void convertImageToGrey(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void convertImageToSepia(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void addFrameToImage(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void convertImageToSobel(Bitmap bitmapIn, Bitmap bitmapOut);
    //endregion
}