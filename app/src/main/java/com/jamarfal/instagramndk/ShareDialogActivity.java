package com.jamarfal.instagramndk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

//-----------------------------------------------------------------
// class MainActivity
//-----------------------------------------------------------------
public class ShareDialogActivity extends AppCompatActivity {
    //
// elementos graficos
//
    private Button boton2;
    private Button boton3;
    private Button boton1;
    private Bitmap bitmapToUpload;
    private ImageView imageView;
    //
// gestiona los callbacks al FacebookSdk desde el método onActivityResult() de una actividad
    //
    private CallbackManager elCallbackManagerDeFacebook;
    //
// shareDialog
//
    private ShareDialog elShareDialog;
    //
// puntero a this para los callback
//
    private final Activity THIS = this;

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private void conseguirReferenciasAElementosGraficos() {
        boton1 = (Button) findViewById(R.id.button1);
        boton2 = (Button) findViewById(R.id.button2);
        boton3 = (Button) findViewById(R.id.button3);
        imageView = (ImageView) findViewById(R.id.imageview_sharedialog);
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("cuandrav.onCreate()", " .onCreate() llamado");

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        this.setContentView(R.layout.activity_share_dialog);

        conseguirReferenciasAElementosGraficos();


        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();

        this.elShareDialog = new ShareDialog(this);
        this.elShareDialog.registerCallback(this.elCallbackManagerDeFacebook, new
                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(THIS, "Sharer onSuccess()", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(THIS, "Sharer onError(): " + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bitmapToUpload = BaseApplication.get().getBitmapToShare();

        imageView.setImageBitmap(bitmapToUpload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode,
                data);
    }


    private void publicarMensajeConIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        startActivityForResult(Intent.createChooser(shareIntent, "Share"), 1234);
    }

    public void boton1_pulsado(View quien) {
        Log.d("cuandrav.boton1_pulsado", " llamado ");
        this.publicarMensajeConIntent();
    }

    private boolean puedoUtilizarShareDialogParaPublicarMensaje() {
        return puedoUtilizarShareDialogParaPublicarLink();
    }


    private boolean puedoUtilizarShareDialogParaPublicarLink() {
        return ShareDialog.canShow(ShareLinkContent.class);
    }

    private boolean puedoUtilizarShareDialogParaPublicarFoto() {
        return ShareDialog.canShow(SharePhotoContent.class);
    }

    public void boton2_pulsado(View quien) {
        Log.d("cuandrav.boton2_pulsado", " llamado ");

        this.publicarMensajeConShareDialog();
    }

    private void publicarMensajeConShareDialog() {
// https://developers.facebook.com/docs/android/share -> Using the ShareDialogActivity
        if (!puedoUtilizarShareDialogParaPublicarMensaje()) {
            Log.d("cuandrav.boton2_pul()", " ¡¡¡ No puedo utilizar share dialog !!!");
            return;
        }

        ShareLinkContent content = new ShareLinkContent.Builder().build();
        this.elShareDialog.show(content);
    }


    public void boton3_pulsado(View quien) {
        Log.d("cuandrav.boton3_pulsado", " llamado ");

        this.publicarFotoConShareDialog();
    }


    private void publicarFotoConShareDialog() {
// https://developers.facebook.com/docs/android/share -> Using the ShareDialogActivity
        if (!puedoUtilizarShareDialogParaPublicarFoto()) {
            return;
        }

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmapToUpload)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        this.elShareDialog.show(content);
    }
}