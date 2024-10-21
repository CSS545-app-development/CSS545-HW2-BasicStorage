package com.example.storage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_NAME = "my_image.jpg"; // Change this to your actual image filename
    private static final String PREFS_NAME = "UserSettings"; // SharedPreferences file name
    private ImageView imageView;
    private Button loadImageButton, saveSettingsButton, loadSettingsButton;
    private EditText settingsEditText; // EditText to take user input for settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        loadImageButton = findViewById(R.id.loadImageButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        loadSettingsButton = findViewById(R.id.loadSettingsButton);
        settingsEditText = findViewById(R.id.settingsEditText); // Initialize EditText

        // Request permission to write to external storage (if needed)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Set click listener for the load image button
        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyImageFromAssetsToGallery();
                loadImageFromGallery();
            }
        });

        // Set click listener for the save settings button
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserSettings(settingsEditText.getText().toString());
            }
        });

        // Set click listener for the load settings button
        loadSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserSettings();
            }
        });
    }

    private void copyImageFromAssetsToGallery() {
        // Create a file in the Pictures directory
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, IMAGE_NAME);

        try (InputStream in = getAssets().open(IMAGE_NAME);
             OutputStream out = new FileOutputStream(imageFile)) {

            // Copy the image from assets to the image file in external storage
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            // Make the image available in the gallery
            MediaScannerConnection.scanFile(this, new String[]{imageFile.toString()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromGallery() {
        // Load the image from the gallery and display it in the ImageView
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, IMAGE_NAME);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);
    }

    private void saveUserSettings(String settings) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_setting_key", settings); // Save the setting with a key
        editor.apply(); // Commit the changes
    }

    private void loadUserSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String settings = sharedPreferences.getString("user_setting_key", ""); // Default value is empty string
        settingsEditText.setText(settings); // Set the EditText with the loaded settings
    }
}