package usage.personal.wallpapertile;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.service.quicksettings.TileService;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class WallpaperChanger extends TileService {
    public static final String KEY_FOR_PATH = "CFP";
    private static final String TAG = "WallpaperChanger";
    private static String myPath;

    @Override
    public void onClick() {
        super.onClick();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkPermission()) {
                    final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    String[] projection = new String[]{MediaStore.Images.Media.DATA, null, null, null};
                    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    //// TODO: 11/09/2017 : 
                    Log.i(TAG, "path: " + images.toString());
                    Cursor cur = getContentResolver().query(images, projection, null, null, null);
                    final ArrayList<String> imagesPath = new ArrayList<>();
                    assert cur != null;
                    if (cur.moveToFirst()) {
                        int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                        myPath = MainActivity.readString(getApplicationContext(), KEY_FOR_PATH);
                        String temp;
                        if (!Objects.equals(myPath, "All files")) {
                            do {
                                temp = cur.getString(dataColumn);
                                if(temp.startsWith(myPath)){
                                    Log.i(TAG, "if " + temp);
                                    imagesPath.add(temp);
                                }
                            } while (cur.moveToNext());
                        } else {
                            do {
                                temp = cur.getString(dataColumn);
                                Log.i(TAG, "else: " + temp);
                                imagesPath.add(temp);
                            } while (cur.moveToNext());
                        }
                    }
                    cur.close();
                    final Random random = new Random();
                    final int count = imagesPath.size();
                    if (count > 0) {
                        int number = random.nextInt(count);
                        String path = imagesPath.get(number);
                        Log.i(TAG, "randomImage: " + path);
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                        try {
                            myWallpaperManager.setBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "randomImage: IO Exception.", e);
                        }
                    } else {
                        Log.i(TAG, "There is no image.");
                    }
                }
                getQsTile().updateTile();
            }
        }).run();
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_WALLPAPER)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + "usage.personal.wallpapertile"));
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentIntent(contentIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.wallpaper_green)
                            .setContentTitle("Permission request")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("You need to give us the permission to reach your photos. Settings > Apps > Auto Wallpaper > Permissions."))
                            .setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
            return false;
        } else return true;
    }
}
