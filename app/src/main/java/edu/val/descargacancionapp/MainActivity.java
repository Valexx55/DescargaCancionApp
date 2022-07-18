package edu.val.descargacancionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.permiso_escritura_concedido = false;
        String[] array_permisos = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(array_permisos, 343);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            this.permiso_escritura_concedido = true;
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

    //en este método, lanzamos el proceso de descargar
    private void descargarFichero (String url_cancion)
    {
        dibujarProgressDialogo();

        Timer timer = new Timer();

         timer.schedule(new TimerTask() {
             @Override
             public void run() {
                 MainActivity.this.progressDialog.dismiss();
             }
         }, 2000);

    }

    public void reproducirVisible(View view) {
    }
}