package com.example.birthdayapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class birthday extends AppCompatActivity {

    ImageView imageView;
    TextView mText;
    ImageButton sharebtn;
    RelativeLayout rl;
    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static final String[] permissionstorage = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        // Taking storage permission to save the screenshot in device
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_GRANTED);

        mText = findViewById(R.id.text);
        String value = getIntent().getStringExtra("birthday name");
        mText.setText("Happy Birthday\n" + value);
        imageView = findViewById(R.id.card);
        rl = findViewById(R.id.rl);
        sharebtn = findViewById(R.id.sharebtn);

        verifystoragepermissions(this);

        sharebtn.setOnClickListener(v -> {
            // Making button invisible so that it don't appear in the screenshot
            sharebtn.setVisibility(View.INVISIBLE);

            takeScreenshot(rl);

            // Making button visible again after taking screenshot
            sharebtn.setVisibility(View.VISIBLE);
        });
    }

    public void takeScreenshot(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            OutputStream os;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Wish " + System.currentTimeMillis() + ".jpg");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Birthday Wisher");
                Uri imageUrl = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                os = resolver.openOutputStream(Objects.requireNonNull(imageUrl));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                Objects.requireNonNull(os);

                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, imageUrl);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/jpeg");

                startActivity(Intent.createChooser(intent, "Share via"));

            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                String dirPath = Environment.getExternalStorageDirectory() + "/DCIM/Screenshots";
                File file = new File(dirPath);
                if (!file.exists())
                    file.mkdir();

                String filePath = dirPath + "/" + "Wish " + System.currentTimeMillis() + ".jpeg";

                File imageurl = new File(filePath);

                FileOutputStream fos = new FileOutputStream(imageurl);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(this, "Image save to\n" + dirPath, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", imageurl);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/jpeg");

                startActivity(Intent.createChooser(intent, "Share via"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void verifystoragepermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe);
        }
    }
}