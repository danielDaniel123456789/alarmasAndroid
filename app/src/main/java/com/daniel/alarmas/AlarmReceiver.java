package com.daniel.alarmas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.Manifest.permission.POST_NOTIFICATIONS;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarmas_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mostrar diálogo de bienvenida
        showWelcomeDialog(context);

        // Mostrar notificación (si hay permisos)
        createNotificationChannel(context);
        showNotification(context);
    }

    private void showWelcomeDialog(Context context) {
        try {
            Intent dialogIntent = new Intent(context, MainActivity.class);
            dialogIntent.putExtra("SHOW_DIALOG", true);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dialogIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Error al mostrar diálogo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification(Context context) {
        try {
            // Verificar permisos para Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permiso de notificaciones no concedido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // ✅ Intent para abrir el navegador con la URL deseada
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://danieldaniel123456789.github.io/clasesIngles/historias/index.html"));

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Construir la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("¡Saludo!")
                    .setContentText("Haz clic para ver la historia")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            // Mostrar la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        } catch (Exception e) {
            Toast.makeText(context, "Error al mostrar notificación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                CharSequence name = "Canal de Alarmas";
                String description = "Notificaciones de alarmas programadas";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            } catch (Exception e) {
                Toast.makeText(context, "Error al crear canal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}