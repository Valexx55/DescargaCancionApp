package edu.val.descargacancionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /**
     * EN ANDROID HAY 4 TIPOS DE CLASES "ESPECIALES"
     * QUE DEBEN SER DECLARADAS EN EL MANIFEST
     *
     *
     * - ACTIVIDADES
     * - SERVICIOS PROPIOS //hacen un tarea no visual
     * - RECEPTORES - RECEIVERS //"CLASES que escuchan eventos- como por ejemplo, el fin de la de descarga de un fichero"
     * - CONTENT PROVDIDER //"alamcen privado con acceso público"
     *
     * estas clases especiales reciben el nombre gernérico de COMPONENTES
     *
     */

    private ProgressDialog progressDialog;//un dialog emergente que informa de la descarga
    private static final String URL_CANCION_COCIDITO = "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/Music/a7/79/0b/mzm.qjnerkzx.aac.p.m4a";
    private boolean permiso_escritura_concedido;
    private String ruta_fichero_descarga;
    private File fichero_destino;
    private Uri uri_descarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] array_permisos = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(array_permisos, 343);
        } else {
            //si tiene version inferior a Android 6, cuenta con los permisos
            this.permiso_escritura_concedido = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            this.permiso_escritura_concedido = true;
        } else {
            this.permiso_escritura_concedido = false;
        }
    }

    public void descargar(View view) {
        if (this.permiso_escritura_concedido)
        {
            descargarFichero (URL_CANCION_COCIDITO);
        } else {
            Toast.makeText(this, "NO PUEDO INCIAR DESCARGA, CONCEDA PERMISOS DE ESCRITURA A LA APP", Toast.LENGTH_LONG).show();
        }
    }


    private void dibujarProgressDialogo ()
    {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Descargando archivo ...");
        this.progressDialog.setIcon(R.mipmap.ic_launcher);
        this.progressDialog.setTitle ("Accediendo a itunes");
        this.progressDialog.setIndeterminate(true);//me muestre un circulo de espera indefinida un animación sin avance
        this.progressDialog.setCancelable(false);//no podemos anular la descarga desde el cuadro de dialogo
        this.progressDialog.show();


    }

    private DownloadManager.Request generarPeticionDescarga (String url)
    {
        DownloadManager.Request request = null;
        Uri ruta_uri_recurso = null;
        //TODO vamos a construir la petición para el Gestor de descargas
            //QUÉ QUIERO DESCARGARME

            //DÓNDE ESTÁ
            //TIPO DE ARCHIVO

            ruta_uri_recurso = Uri.parse(url);//convierto para que el string sea una dirección valida (tildes, espacios, caracteres especiales)
            request = new DownloadManager.Request(ruta_uri_recurso);
            request.setDescription("MÚSICA");
            request.setTitle("Descarga muestra mp3");
            request.setMimeType("audio/mp3"); //"Tipo de archivo que descargo "la extensión"

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//hace visible la descarga al finalizar permance

        //DÓNDE ME LO VA A GUADAR - este area de memoria es el directorio privado asociado a mi app
            this.ruta_fichero_descarga = getExternalFilesDir(null).getPath()+"/cancion.mp3";
            this.fichero_destino = new File(this.ruta_fichero_descarga);
            this.uri_descarga = Uri.fromFile(this.fichero_destino);

            request.setDestinationUri(uri_descarga);


        return request;
    }

    //en este método, lanzamos el proceso de descargar
    private void descargarFichero (String url_cancion)
    {
        dibujarProgressDialogo();
        DescargaCompletaReceiver receiver = null;
        IntentFilter filter = null;

        //ESTOY PROGRAMANDO QUE EL RECEIVER DescargaCompletaReceiver
        //ESTÉ "ESCUCHANDO" el evento QUE DIGA QUE SE HA TERMINADO LA DESCARGA
        receiver = new DescargaCompletaReceiver(this);
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter);//asociamos el receptor a ese mensaje (que será emitido por el DownloadManager)

        //"la request sería como el PEDIDO " al DownloadMANAGER
        DownloadManager.Request request = null; //este objeto es el que tiene toda la información sobre la descarga
        request = generarPeticionDescarga(url_cancion);

        //PIDO QUE DESCARGUE
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long id_descarga = downloadManager.enqueue(request);//"encolar" - descarga esto!
        receiver.setId_descarga(id_descarga);//al pedir al download que descarge, me da el ID de la descarga - ticket

        /*
        Timer timer = new Timer();

         timer.schedule(new TimerTask() {
             @Override
             public void run() {
                 MainActivity.this.progressDialog.dismiss();
             }
         }, 2000);
        */
    }

    public void reproducirVisible(View view) {

        try {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            //TODO MAÑANA FILE PROVIDER y RERPDUCIR EL ARCHIVO

            //En vez de usar la uri privada, vamos a usar una uri del file provider
            //Aquí pasamos de una uri privada a una uri pública
            Uri uri_publica = FileProvider.getUriForFile(this, "edu.val.descargacancionapp.fileprovider", fichero_destino);
            Log.d("ETIQUETA_LOG", "uri_publica = " + uri_publica);
            Log.d("ETIQUETA_LOG", "uri_privada = " + uri_descarga);

            intent.setData(uri_publica);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//así permitmos a la app externa, leer de mi ruta aunque no tenga permisos de linux

            //intent.setData(uri_descarga);
            //intent.setType("audio/*");

            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.d("ETIQUETA_LOG", "Al menos una app puede abrir un mp3");
                startActivity(intent);
            } else {
                Log.d("ETIQUETA_LOG", "NINGUNA APP PUEDE ABRIR un mp3");
            }



        } catch (Exception e)
        {
            Log.e("ETIQUETA_LOG", "FALLO AL REPRODUCIR EL ARCHIVO", e);
        }


    }

    public void actualizarVentanaTrasDescarga(boolean descarga_ok) {

        this.progressDialog.dismiss();//oculto el diálogo de descarga

        if (descarga_ok)
        {
            //le permito reproducir la canción descargada
            Log.d("ETIQUETA_LOG", "descarga finalizada OK");
            ImageButton imageView = findViewById(R.id.img_repoducir);
            imageView.setBackgroundColor(getResources().getColor(R.color.purple_500));
            imageView.setClickable(true); //una vez que descargue todo bien, cambio el color del botón y lo hago que se pueda dar click
        } else {

            Log.d("ETIQUETA_LOG", "descarga finalizada KO");
            Toast toast = Toast.makeText(this, "Ha habido un problema en la descarga. Archivo no disponible", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}