package usage.personal.wallpapertile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.net.URISyntaxException;

public class MainActivityold extends AppCompatActivity {
    public static final String KEY_FOR_PATH = "CFP";
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MainActivity";
    TextView filePath;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath = findViewById(R.id.textView);
        button = findViewById(R.id.chooseButton);
        filePath.setText(readString(getApplicationContext(), KEY_FOR_PATH));
    }

    public void clearPath(View view) {
        removeKEY(getApplicationContext(), KEY_FOR_PATH);
        filePath.setText("");
    }

    public void showFileChooser(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + (uri != null ? uri.toString() : null));
                    String fu = null;
                    try {
                        fu = getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "fu: " + fu);

                    assert fu != null;
                    fu = fu.substring(0, fu.lastIndexOf(File.separator));
                    Log.d(TAG, "fu directory: " + fu);

                    filePath.setText(fu);
                    writeString(getApplicationContext(), KEY_FOR_PATH, fu);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void writeString(Context context, final String KEY, String property) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY, property);
        editor.apply();
    }

    public static String readString(Context context, final String KEY) {
        if (context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY, null) == null) {
            return "All files";
        } else {
            return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY, null);
        }
    }

    public static void removeKEY(Context context, final String KEY) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(KEY);
        editor.apply();
    }

    @Nullable
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    Log.d(TAG, "Cursor: " + cursor.getString(column_index));
                    return cursor.getString(column_index);
                }
                cursor.close();
            } catch (Exception e) {
                Log.e(TAG, "Error occured in getPath() method.", e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.d(TAG, "Uri: " + uri.getPath());
            return uri.getPath();
        }
        return null;
    }
}
