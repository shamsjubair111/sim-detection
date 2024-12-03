package com.example.simnumberdetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if necessary permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
            getSimNumbers();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void getSimNumbers() {
        // Get TelephonyManager instance for default SIM
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // Get SubscriptionManager instance for dual SIMs
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);

        if (telephonyManager != null && subscriptionManager != null) {
            // For single SIM (default SIM)
            String defaultSimNumber = telephonyManager.getLine1Number(); // Get the number of the default SIM
            if (defaultSimNumber != null && !defaultSimNumber.isEmpty()) {
                Toast.makeText(this, "Default SIM Number: " + defaultSimNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Default SIM Number not available", Toast.LENGTH_SHORT).show();
            }

            // For multiple SIMs (dual SIM scenario)
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                // Loop through all active SIMs
                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                    int simSlotIndex = subscriptionInfo.getSimSlotIndex();
                    String simNumber = subscriptionInfo.getNumber(); // Get the number of the SIM

                    if (simNumber != null && !simNumber.isEmpty()) {
                        // Show the number for each SIM
                        Toast.makeText(this, "SIM " + (simSlotIndex + 1) + " Number: " + simNumber, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "SIM " + (simSlotIndex + 1) + " Number is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "No active SIM cards found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to get Telephony or Subscription Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of runtime permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSimNumbers(); // Fetch SIM numbers if permission is granted
            } else {
                Toast.makeText(this, "Permission denied. Cannot access SIM numbers.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
