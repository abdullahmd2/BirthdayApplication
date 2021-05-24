package com.example.birthdayapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.normalizeMimeType;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class birthday extends AppCompatActivity {

    ImageView imageView;
    TextView mText;
    ImageButton sharebtn;
    RelativeLayout rl;
    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static String[] permissionstorage = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        // Taking storage permission to save the screenshot in device
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_GRANTED);

        mText = findViewById(R.id.text);
        String value = getIntent().getStringExtra("birthday name");
        mText.setText("Happy Birthday\n" + value);
        imageView = (ImageView) findViewById(R.id.card);
        rl = (RelativeLayout) findViewById(R.id.rl);
        sharebtn = (ImageButton) findViewById(R.id.sharebtn);

        verifystoragepermissions(this);

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Making button invisible so that it don't appear in the screenshot
                sharebtn.setVisibility(View.INVISIBLE);

//                View vi = rl;
                takeScreenshot(rl, "Capture", birthday.this);

                // Making button visible again after taking screenshot
                sharebtn.setVisibility(View.VISIBLE);
            }
        });
    }

    public File takeScreenshot(View view, String fileName, Context context) {
        try {
            String dirPath = Environment.getExternalStorageDirectory() + "/DCIM/Screenshots";
            File file = new File(dirPath);
            if (!file.exists())
                file.mkdir();

            String filePath = dirPath + "/" + fileName + " - " + Calendar.getInstance().getTime().toString() + ".jpeg";

            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageurl = new File(filePath);
            FileOutputStream fos = new FileOutputStream(imageurl);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", imageurl);
            intent.setDataAndType(uri, "image/jpeg");

            PackageManager pm = getPackageManager();
            if (intent.resolveActivity(pm) != null)
                startActivity(Intent.createChooser(intent,"Open via"));
            else
                Log.d("Error", "Cannot start intent");

            return imageurl;
        } catch (Exception e) {
            Log.d("Error", "There is an error" + e);
        }

        return null;
    }

    public static void verifystoragepermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe);
        }
    }
}