package com.daniel.alarmas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnProgramarAlarma;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnProgramarAlarma = findViewById(R.id.btnProgramarAlarma);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        btnProgramarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndScheduleAlarm();
            }
        });

        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            mostrarDialogoBienvenida();
        }
    }

    private void checkAndScheduleAlarm() {
        // Verificar permisos para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }
        programarAlarma();
    }

    private void programarAlarma() {
        long tiempoAlarma = SystemClock.elapsedRealtime() + 10000; // 10 segundos

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Configuración diferente para Android 8+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    tiempoAlarma,
                    pendingIntent);
        } else {
            alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    tiempoAlarma,
                    pendingIntent);
        }

        Toast.makeText(this, "Alarma programada para 10 segundos", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoBienvenida() {
        new AlertDialog.Builder(this)
                .setTitle("Saludo")
                .setMessage("Hola, bienvenido")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                programarAlarma();
            } else {
                Toast.makeText(this, "Se necesitan permisos para notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}