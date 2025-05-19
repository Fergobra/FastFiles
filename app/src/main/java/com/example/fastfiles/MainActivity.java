package com.example.fastfiles;

import androidx.appcompat.app.AppCompatActivity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    FileUploadServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);

        server = new FileUploadServer(this);
        try {
            server.start();
            String ip = getIP();
            tv.setText("Abre en tu navegador: http://" + ip + ":8080");
            Toast.makeText(this, "Servidor activo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            tv.setText("Error al iniciar servidor");
        }
    }

    private String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) server.stop();
    }
}
