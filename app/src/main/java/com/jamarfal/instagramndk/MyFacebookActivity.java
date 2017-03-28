package com.jamarfal.instagramndk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MyFacebookActivity extends AppCompatActivity {
    //------------------------------------------------------------
    // elementos gráficos
    // ------------------------------------------------------------
    private TextView elTextoDeBienvenida;
    private Button botonHacerLogin;
    private Button botonLogOut;
    private Button botonEnviarFoto;
    private TextView textoConElMensaje;
    private Button botonCompartir;
    private ImageView imageToSend;
    private Bitmap bitmapToSend;
    //
    // boton oficial de Facebook para login/logout
    LoginButton loginButtonOficial;

    // gestiona los callbacks al FacebookSdk desde el método onActivityResult() deuna actividad
    private CallbackManager elCallbackManagerDeFacebook;

    //--------------------------------------------------------------
    // puntero a this para los callback
    //--------------------------------------------------------------
    private final Activity THIS = this;

    //-------------------------------------------------------------
    // onCreate ()
    //-------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) { //
        // inicializar super
        super.onCreate(savedInstanceState); //
        Log.d("cuandrav.onCreate()", " .onCreate() llamado");

        // cosas de Facebook

        // inicializar FacebookSDK
        // 2017: no hace falta, se puede borrar:http:stackoverflow.com/questions/41904350/facebooksdk-sdkinitializegetapplicationcontext-deprecated
        // FacebookSdk.sdkInitialize(this.getApplicationContext());

        //
        // pongo el contenido visual de la actividad (hacer antes que findViewById () // y después de inicializar FacebookSDK)
        //
        this.setContentView(R.layout.activity_my_facebook);

        //String currentImageUri = getIntent().getExtras().getString("BMP");

//        String filename = getIntent().getStringExtra("image");
//        try {
//            FileInputStream is = this.openFileInput(filename);
//            bitmapToSend = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        bitmapToSend = BaseApplication.get().getBitmapToShare();


        // botón oficial de "login en Facebook"
        // obtengo referencia
        loginButtonOficial = (LoginButton) findViewById(R.id.login_button);

        imageToSend = (ImageView) findViewById(R.id.imageview_facebook_send);
        imageToSend.setImageBitmap(bitmapToSend);

        // declaro los permisos que debe pedir al ser pulsado
        // ver lista en: https://developers.facebook.com/docs/facebook-login/permissions
        loginButtonOficial.setPublishPermissions("publish_actions");
        //loginButtonOficial.setReadPermissions("public_profile"); // si pones uno,no puedes poner el otro

        // crear callback manager de Facebook
        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();

        // registro un callback para saber cómo ha ido el login
        LoginManager.getInstance().registerCallback(this.elCallbackManagerDeFacebook,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) { // App code
                        Toast.makeText(THIS, "Login onSuccess()", Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(THIS, "Login onCancel()",
                                Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }

                    @Override
                    public void onError(FacebookException exception) { // App code
                        Toast.makeText(THIS, "Login onError(): " + exception.getMessage(), Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }
                });
        // otras cosas

        // obtengo referencias a mis otros widgets en el layout
        elTextoDeBienvenida = (TextView) findViewById(R.id.elTextoDeBienvenida);
        botonHacerLogin = (Button) findViewById(R.id.boton_hacerLogin);
        botonLogOut = (Button) findViewById(R.id.boton_LogOut);
        botonEnviarFoto = (Button) findViewById(R.id.boton_EnviarFoto);
        textoConElMensaje = (TextView) findViewById(R.id.txt_mensajeFB);
        botonCompartir = (Button) findViewById(R.id.boton_EnviarAFB);

        this.actualizarVentanita();

        Log.d("cuandrav.onCreate", "final .onCreate() ");
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        // se llama cuando otra actividad que hemos arrancado termina y nos devuelveel control
        // tal vez, devolviéndonos algun resultado (resultCode, data)
        Log.d("cuandrav.onActivityResu", "llamado");


        // avisar a super
        super.onActivityResult(requestCode, resultCode, data);

        // avisar a Facebook (a su callback manager) por si le afecta
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode, data);
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    private void actualizarVentanita() {
        Log.d("cuandrav.actualizarVent", "empiezo");

        // obtengo el access token para ver si hay sesión
        AccessToken accessToken = this.obtenerAccessToken();
        if (accessToken == null) {
            Log.d("cuandrav.actualizarVent", "no hay sesion, deshabilito"); //
            // sesion con facebook cerrada
            this.botonHacerLogin.setEnabled(true);
            this.botonLogOut.setEnabled(false);
            this.textoConElMensaje.setEnabled(false);
            this.botonCompartir.setEnabled(false);
            this.botonEnviarFoto.setEnabled(false);
            this.elTextoDeBienvenida.setText("haz login");
            return;
        }
        // sí hay sesión

        Log.d("cuandrav.actualizarVent", "hay sesion habilito");
        this.botonHacerLogin.setEnabled(false);
        this.botonLogOut.setEnabled(true);
        this.textoConElMensaje.setEnabled(true);

        this.botonCompartir.setEnabled(true);
        this.botonEnviarFoto.setEnabled(true);

        // averiguo los datos básicos del usuario acreditado
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            this.textoConElMensaje.setText(profile.getName());
        }
        // otra forma de averiguar los datos básicos:
        // hago una petición con "graph api" para obtener datos del usuario acreditado
        this.obtenerPublicProfileConRequest_async(
                // como es asíncrono he de dar un callback
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject datosJSON, GraphResponse response) {
                        //
                        // muestro los datos
                        //
                        String nombre = "nombre desconocido";

                        try {
                            nombre = datosJSON.getString("name");
                        } catch (org.json.JSONException ex) {
                            Log.d("cuandrav.actualizarVent", "callback de obtenerPublicProfileConRequest_async: excepcion: " + ex.getMessage());
                        } catch (NullPointerException ex) {
                            Log.d("cuandrav.actualizarVent", "callback de obtenerPublicProfileConRequest_async:excepcion: " + ex.getMessage());
                        }
                        elTextoDeBienvenida.setText("bienvenido 2017: " + nombre);
                    }
                });
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    private boolean sePuedePublicar() {
//
// compruebo la red
//
        if (!this.hayRed()) {
            Toast.makeText(this, "¿no hay red? No puedo publicar",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        // compruebo permisos
        if (!this.tengoPermisoParaPublicar()) {
            Toast.makeText(this, "¿no tengo permisos para publicar? Los pido.",
                    Toast.LENGTH_LONG).show();

            // pedirPermisoParaPublicar();
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
            return false;
        }

        return true;
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    private AccessToken obtenerAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    private boolean tengoPermisoParaPublicar() {
        AccessToken accessToken = this.obtenerAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");

    }

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private boolean hayRed() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
// http://stackoverflow.com/questions/15091591/post-on-facebook-wall-without-showing-dialogon-android
// comprobar que estamos conetactos a internet, antes de hacer el login con
// facebook. Si no: da problemas.
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void enviarTextoAFacebook_async(final String textoQueEnviar) {
//
// si no se puede publicar no hago nada
//
        if (!sePuedePublicar()) {
            return;
        }
//
// hago la petición a través del API Graph
//
        Bundle params = new Bundle();
        params.putString("message", textoQueEnviar);
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        Toast.makeText(THIS, "Publicación realizada: " +
                                textoQueEnviar, Toast.LENGTH_LONG).show();
                    }
                }
        );
        request.executeAsync();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void enviarFotoAFacebook_async(Bitmap image, String comentario) {
        Log.d("cuandrav.envFotoFBasync", "llamado");
        if (image == null) {
            Toast.makeText(this, "Enviar foto: la imagen está vacía.",
                    Toast.LENGTH_LONG).show();
            Log.d("cuandrav.envFotoFBasync", "acabo porque la imagen es null");
            return;
        }
//
// si no se puede publicar no hago nada
//
        if (!sePuedePublicar()) {
            return;
        }
//
// convierto el bitmap a array de bytes
//
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//image.recycle ();
        final byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
        }
//
// hago la petición a traves del Graph API
//
        Bundle params = new Bundle();
        params.putByteArray("source", byteArray); // bytes de la imagen
        params.putString("caption", comentario); // comentario
// si se quisiera publicar una imagen de internet: params.putString("url","{image-url}");
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/photos?debug=all",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        Toast.makeText(THIS, "" + byteArray.length + " Foto enviada:" + response.toString(), Toast.LENGTH_LONG).show();
//textoConElMensaje.setText(response.toString());
                    }
                }
        );
        request.executeAsync();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void boton_Login_pulsado(View quien) {
//
// compruebo la red
//
        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red? No puedo abrir sesión",
                    Toast.LENGTH_LONG).show();
        }
//
// login
//
        LoginManager.getInstance().logInWithPublishPermissions(this,
                Arrays.asList("publish_actions"));
//
// actualizar
//
        this.actualizarVentanita();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void boton_Logout_pulsado(View quien) {
//
// compruebo la red
//
        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red? No puedo cerrar sesión",
                    Toast.LENGTH_LONG).show();
        }
//
// logout
//
        LoginManager.getInstance().logOut();
//
// actualizar
//
        this.actualizarVentanita();
    } // ()

    // ------------------------------------------------------------
// ------------------------------------------------------------
    private void obtenerPublicProfileConRequest_async
    (GraphRequest.GraphJSONObjectCallback callback) {

        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red?", Toast.LENGTH_LONG).show();
        }
//
// obtengo access token y compruebo que hay sesión
//
        AccessToken accessToken = obtenerAccessToken();
        if (accessToken == null) {
            Toast.makeText(THIS, "no hay sesión con Facebook",
                    Toast.LENGTH_LONG).show();
            return;
        }
//
// monto la petición: /me
//
        GraphRequest request = GraphRequest.newMeRequest(accessToken, callback);
        Bundle params = new Bundle();
        params.putString("fields", "id, name");
        request.setParameters(params);
//
// la ejecuto (asíncronamente)
//
        request.executeAsync();
    }

    // ------------------------------------------------------------
// ------------------------------------------------------------
    public void boton_enviarTextoAFB_pulsado(View quien) {
//
// cojo el mensaje que ha escrito el usuario
//
        String mensaje = "msg:" + this.textoConElMensaje.getText() + " :"
                + System.currentTimeMillis();
//
// borro lo escrito
//
        this.textoConElMensaje.setText("");
//
// cierro el soft-teclado
//
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.textoConElMensaje.getWindowToken(), 0);
//
// llamo al método que publica
//
        enviarTextoAFacebook_async(mensaje);
    }

    public void boton_EnviarFoto_pulsado(View view) {
        enviarFotoAFacebook_async(bitmapToSend, "ola ola");
    }


    private void setPic(String path) {
        // Get the dimensions of the View
        int targetW = imageToSend.getWidth();
        int targetH = imageToSend.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imageToSend.setImageBitmap(bitmap);
    }
} // class
