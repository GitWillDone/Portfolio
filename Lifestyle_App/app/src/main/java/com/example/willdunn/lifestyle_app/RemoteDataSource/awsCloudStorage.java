package com.example.willdunn.lifestyle_app.RemoteDataSource;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class awsCloudStorage {

    private static final String AWS_ACCESS_KEY = getKey("/data/user/0/com.example.willdunn.lifestyle_app/files/access_key.txt");
    private static final String AWS_SECRET_KEY = getKey("/data/user/0/com.example.willdunn.lifestyle_app/files/secret_key.txt");

    private static String getKey(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String keyName = bufferedReader.readLine();
            if (keyName.contains("\n")) {
                keyName.replace("\n", "");
            } else if (keyName.contains("\r")) {
                keyName.replace("\r", "");
            }
            return keyName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ""; // Default return for a fail
    }

    //AWS data backup
    public static void uploadWithTransferUtility(final Context mainContext, String username) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(mainContext.getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        String currentDBPath = mainContext.getDatabasePath("LifestyleAppProfiles.db").getAbsolutePath();
        File dbFile = new File(currentDBPath);
        TransferObserver uploadObserver = transferUtility.upload(
                "lifestyleapplication-userfiles-mobilehub-81515982",
                "SQLite/" + username + "LifestyleAppProfiles.db",
                dbFile);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    //Toast.makeText(mainContext.getApplicationContext(),"Transfer to AWS complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                //Toast.makeText(mainContext.getApplicationContext(),"You have an error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
            //Toast.makeText(mainContext.getApplicationContext(),"Progress complete", Toast.LENGTH_LONG).show();
        }

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }










}
