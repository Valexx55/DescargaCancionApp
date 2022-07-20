package edu.val.descargacancionapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

//esta clase es la que va a escuchar el fin de la descarga del fichero
public class DescargaCompletaReceiver extends BroadcastReceiver {


    private Context context; //para saber desde qué actividad se ha lanzado la descarga
    private long id_descarga; //así voy a saber cuál es el id de la descarga

    public DescargaCompletaReceiver()
    {

    }

    public DescargaCompletaReceiver (Context context)
    {
        this.context = context;//así, luego podré "meter mano" a la actividad una vez acabada la descarga
    }

    public void setId_descarga(long id_descarga) {
        this.id_descarga = id_descarga;
    }

    @Override //este método, se invoca por el sistema (recibe un callback) al completar la descarga que está "escuchando"
    public void onReceive(Context context, Intent intent) {

        Log.d("ETIQUETA_LOG", "descarga finalizada...");

        //para acceder al servicio de descargar, necesito hacerlo desde el contexto
        //uso, el context, creado en el constructor (que referencia a la pantalla inicial)
        //para poder llamarlo
        //obtengo el servicio de descargas
        DownloadManager downloadManager = (DownloadManager) this.context.getSystemService(Context.DOWNLOAD_SERVICE);
        //preparo la consulta
        DownloadManager.Query query = new  DownloadManager.Query(); //vamos a consultar cuál ha sido el resultado de la descarga
        query.setFilterById(this.id_descarga);//vamos a poder conusltar cómo ha ido nuestra descarga. En realidad, el DownloadMananger, ofrece una especie de ContentProvider
        //hago la consulta
        Cursor cursor = downloadManager.query(query); //dame los datos de la descarga con este id
        cursor.moveToFirst();//avanzo el cursor al primer registro leido
        int num_columno_estado = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int estado_descarga = cursor.getInt(num_columno_estado);

        MainActivity mainActivity = (MainActivity) this.context;
        switch (estado_descarga)
        {
            case DownloadManager.STATUS_SUCCESSFUL:
                Log.d("ETIQUETA_LOG", "La descarga con ID " + this.id_descarga + " Acabó bien");
                //TODO actualizar la Actividad desde donde llamamos
                mainActivity.actualizarVentanaTrasDescarga(true);
                break;

            case DownloadManager.STATUS_FAILED:
                Log.d("ETIQUETA_LOG", "La descarga con ID " + this.id_descarga + " Acabó mal");
                //TODO actualizar la Actividad desde donde llamamos
                mainActivity.actualizarVentanaTrasDescarga(false);

                break;
        }

       //TODO hay que "desenchufar" este receiver - hacerlo que deje de escuchar.-.
    }


}