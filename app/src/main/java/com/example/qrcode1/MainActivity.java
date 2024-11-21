package com.example.qrcode1;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.Manifest;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText etName, etAddress;
    private Button buttonScan;


    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Check and request camera permissions
        checkCameraPermission();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        buttonScan = findViewById(R.id.buttonScan);

        // Set up QR code scan button
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan a QR Code");
                integrator.setCameraId(0); // Use the device's back camera
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });

        // Open website on clicking the address EditText
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etAddress.getText().toString();
                if (!url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(MainActivity.this, "No URL found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handle QR code scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    // Parse the QR code data as JSON
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    String title = jsonObject.getString("title");
                    String website = jsonObject.getString("website");

                    // Set data to EditTexts
                    etName.setText(title);
                    etAddress.setText(website);
                } catch (Exception e) {
                    // Handle invalid QR code content
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No QR Code Scanned", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkCameraPermission() {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            Toast.makeText(this, "Camera permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
}