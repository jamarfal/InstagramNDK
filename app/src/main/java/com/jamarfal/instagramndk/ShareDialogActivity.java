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
    private TextView textoEntrada1;
    private TextView textoSalida1;
    private Bitmap bitmapToUpload;
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
        textoEntrada1 = (TextView) findViewById(R.id.textoEntrada1);
        textoSalida1 = (TextView) findViewById(R.id.textoSalida1);
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //
// inicializar super
//
        super.onCreate(savedInstanceState);
//
        Log.d("cuandrav.onCreate()", " .onCreate() llamado");
//
//
// inicializar FacebookSDK
//
        FacebookSdk.sdkInitialize(this.getApplicationContext());
//
// pongo el contenido visual de la actividad (hacer antes que findViewById ()
// y después de inicializar FacebookSDK)
//
        this.setContentView(R.layout.activity_share_dialog);
        conseguirReferenciasAElementosGraficos();
//
// crear callback manager de Facebook
//
        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();
//
// crear objeto share dialog
//
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

        bitmapToUpload = BaseApplication.get().getBitmapToShare();
    }

    // ------------------------------------------------------------
// ------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode,
                data);
    }

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private void publicarMensajeConIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        startActivityForResult(Intent.createChooser(shareIntent, "Share"), 1234);
//requestId);
    } //

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void boton1_pulsado(View quien) {
        Log.d("cuandrav.boton1_pulsado", " llamado ");
        textoSalida1.setText("boton1_pulsado");
        this.publicarMensajeConIntent();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarMensaje() {
        return puedoUtilizarShareDialogParaPublicarLink();
    }

    // ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarLink() {
        return ShareDialog.canShow(ShareLinkContent.class);
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarFoto() {
        return ShareDialog.canShow(SharePhotoContent.class);
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void boton2_pulsado(View quien) {
        Log.d("cuandrav.boton2_pulsado", " llamado ");
        textoSalida1.setText("boton2_pulsado");
//
// llamar al metodo para publicar
//
        this.publicarMensajeConShareDialog();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private void publicarMensajeConShareDialog() {
// https://developers.facebook.com/docs/android/share -> Using the ShareDialogActivity
        if (!puedoUtilizarShareDialogParaPublicarMensaje()) {
            Log.d("cuandrav.boton2_pul()", " ¡¡¡ No puedo utilizar share dialog !!!");
            return;
        }
        //
// llamar a share dialog
// aunque utilizamos ShareLinkContent, al no poner link
// publica un mensaje
//
        ShareLinkContent content = new ShareLinkContent.Builder().build();
        this.elShareDialog.show(content);
    } // ()

    // ------------------------------------------------------------
    public void boton3_pulsado(View quien) {
        Log.d("cuandrav.boton3_pulsado", " llamado ");
        textoSalida1.setText("boton3_pulsado");
//
// llamar al metodo para publicar foto
//
        this.publicarFotoConShareDialog();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
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