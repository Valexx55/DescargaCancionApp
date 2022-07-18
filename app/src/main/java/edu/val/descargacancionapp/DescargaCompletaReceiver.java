package edu.val.descargacancionapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//esta clase es la que va a escuchar el fin de la descarga del fichero
public class DescargaCompletaReceiver extends BroadcastReceiver {

    @Override //este método, se invoca por el sistema (recibe un callback) al completar la descarga que está "escuchando"
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}