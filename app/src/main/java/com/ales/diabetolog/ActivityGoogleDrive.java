package com.ales.diabetolog;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ActivityGoogleDrive extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

    private ApplicationDiabetoLog applicationDiabetoLog;
    private boolean isExport = true;
    private GoogleApiClient googleApiClient;
    private DriveContents driveContents;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive);
        String firstTimeOpening = getIntent().getExtras().get("FirstTimeOpening").toString();
        if(firstTimeOpening.equals("true")){
            findViewById(R.id.btn_export).setEnabled(false);
        }

        activity=this;
        applicationDiabetoLog=(ApplicationDiabetoLog)getApplication();
        googleApiClient= new GoogleApiClient.Builder(this)

                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(applicationDiabetoLog.isNetworkAvailable()){
                    isExport=true;
                    if(googleApiClient.isConnected()) {
                        googleApiClient.disconnect();
                    }
                    googleApiClient.connect();
                    findViewById(R.id.btn_export).setEnabled(false);
                    findViewById(R.id.btn_import).setEnabled(false);
                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Немає підключення до Інтернету!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(applicationDiabetoLog.isNetworkAvailable()){
                    isExport=false;
                    if(googleApiClient.isConnected()) {
                        googleApiClient.disconnect();
                    }
                    googleApiClient.connect();
                    findViewById(R.id.btn_export).setEnabled(false);
                    findViewById(R.id.btn_import).setEnabled(false);
                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Немає підключення до Інтернету!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabetoLog.save();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    googleApiClient.connect();
                }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e("Error-GoogleDrive", "Error while trying to create new file contents");
                    if(googleApiClient.isConnected()) {
                        googleApiClient.disconnect();
                    }
                    return;
                }
                driveContents = result.getDriveContents();
                Drive.DriveApi.getRootFolder(googleApiClient).listChildren(googleApiClient).setResultCallback(resultCallback);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Error-GoogleDrive","Connection has been suspended!");
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Error-GoogleDrive","Connection has failed!");
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this,RESOLVE_CONNECTION_REQUEST_CODE);
            }catch (IntentSender.SendIntentException e){
                Log.e("Error-GoogleDrive","Connection resolution has failed!");
                if(googleApiClient.isConnected()) {
                    googleApiClient.disconnect();
                }
            }
        }
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> resultCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error-GoogleDrive","Problem while retrieving files");
                        googleApiClient.disconnect();
                        return;
                    }
                    Log.i("Info-GoogleDrive","Successfully got folder data!");
                    int countOfFiles = result.getMetadataBuffer().getCount();
                    if(isExport) {
                        Log.i("Info-GoogleDrive","Exporting data...");
                        if(countOfFiles!=0) {
                            Log.i("Info-GoogleDrive","The AppFolder already contains a file or multiple files.");
                            for (int i = 0; i < countOfFiles; i++) {
                                if(result.getMetadataBuffer().get(i).getTitle().equals("backup_DiabetoLog.txt")){
                                    Log.i("Info-GoogleDrive","Deleting file:"+result
                                            .getMetadataBuffer()
                                            .get(i)
                                            .getTitle()
                                    );
                                    DriveFile driveFile = DriveId.decodeFromString(
                                            result
                                                    .getMetadataBuffer()
                                                    .get(i).getDriveId()
                                                    .toString()
                                    ).asDriveFile();
                                    driveFile.delete(googleApiClient);
                                }
                            }
                        }
                        String title = "backup_DiabetoLog.txt";
                        OutputStream outputstream= driveContents.getOutputStream();
                        MetadataChangeSet backupFile = new MetadataChangeSet.Builder()
                                .setTitle(title)
                                .setMimeType("text/plain")
                                .build();
                        Writer writer= new OutputStreamWriter(outputstream);
                        try {
                            writer.write(applicationDiabetoLog.getExportData());
                            writer.close();
                        }catch (IOException e){
                            Log.e("Error-GoogleDrive","Error while writing to file!");
                        }
                        Drive.DriveApi.getRootFolder(googleApiClient)
                                .createFile(googleApiClient, backupFile, driveContents)
                                .setResultCallback(fileCallback);
                        result.getMetadataBuffer().release();
                    }else{
                        Log.i("Info-GoogleDrive","Importing data..."+countOfFiles);
                        DriveFile driveFile=null;
                        for (int i = 0; i < countOfFiles; i++) {
                            if(result.getMetadataBuffer().get(i).getTitle().equals("backup_DiabetoLog.txt")){
                                Metadata metadata = result.getMetadataBuffer().get(i);
                                Log.i("Info-GoogleDrive","Файл знайдено"+metadata.getTitle()+" "+metadata.getDriveId()+" "+metadata.isTrashed());
                                Log.i("Info-GoogleDrive","Importing .." + metadata.getTitle());
                                driveFile = DriveId.decodeFromString(
                                        result
                                                .getMetadataBuffer()
                                                .get(i).getDriveId()
                                                .toString()
                                ).asDriveFile();
                            }
                        }
                        if(driveFile!=null){
                            Log.i("Info-GoogleDrive","Файл пустий");
                            driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY,null).setResultCallback(
                                    new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                                            if (!result.getStatus().isSuccess()) {
                                                Log.e("Error-GoogleDrive","Problem while retrieving file contents for import");
                                                googleApiClient.disconnect();
                                                return;
                                            }
                                            Log.i("Info-GoogleDrive","Calling save function...");
                                            driveContents = result.getDriveContents();
                                            boolean importSuccess = applicationDiabetoLog.saveImportData(
                                                    new BufferedReader(
                                                            new InputStreamReader(
                                                                    driveContents.getInputStream()
                                                            )
                                                    )
                                            );
                                            if(importSuccess){
                                                activity.finish();
                                            }else{
                                                applicationDiabetoLog.IMPORT_FAILED=true;
                                                activity.finish();
                                            }
                                        }
                                    }
                            );
                        }else{
                            applicationDiabetoLog.IMPORT_FAILED=true;
                            applicationDiabetoLog.NO_FILES_FOUND=true;
                            activity.finish();
                        }
                        result.getMetadataBuffer().release();
                        driveContents.discard(googleApiClient);
                    }
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error-GoogleDrive","Error while trying to create the file");
                        applicationDiabetoLog.EXPORT_FAILED=true;
                        googleApiClient.disconnect();
                        return;
                    }
                    Log.i("Info-GoogleDrive","File was succesfully created in the app folder on Google Drive.");
                    applicationDiabetoLog.EXPORT=true;
                    googleApiClient.disconnect();
                    activity.finish();
                }
            };
}
