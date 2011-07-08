package com.fsalinas.fileupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class LockBoxUpload extends Activity implements UploadEventListener{

    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_PIC_REQUEST = 2;

    //private static final String UPLOAD_URL = "http://10.0.10.51/FileUploadPrototype/Default.aspx";
	private static final String UPLOAD_URL = "http://192.168.5.104/FileUploadPrototype/Default.aspx";
    private Uri selectedImageUri;
    private AsyncFileUploader afu;
    private ImageHelper imageHelper;
    private UploadControl uploadControl;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionchoice);
        imageHelper = new ImageHelper(this);


        try {
            afu = new AsyncFileUploader(UPLOAD_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        afu.addUploadEventListener(this);

        //
        // TODO: Create Uploader controls
    }

	public void onUploadClick(View v) {
		//
		// Launch gallery chooser
		Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,
                "Select Content To Upload"), SELECT_PICTURE);		
	}
	
	public void onCameraClick(View v) {
		
		selectedImageUri = imageHelper.getCapturedImageUri();
		//create new Intent
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
		
	}
	
	public void onCancelClick(View v) {
		afu.cancelUpload();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	            selectedImageUri = data.getData();
    			uploadFile(selectedImageUri);	            
	        }
	        else if ( requestCode == CAMERA_PIC_REQUEST) {
	        	selectedImageUri = imageHelper.getCapturedImageUri();
    			uploadFile(selectedImageUri);
	        }
	    }
	}
	
    private void uploadFile(Uri fileUri) {

        String fileName = imageHelper.getPath(fileUri);

        if (fileName != null && fileName.length() > 0) {

            setContentView(R.layout.upload);

            ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.ProgressBar01);
            ImageView icon = (ImageView) this.findViewById(R.id.imgUpload1);
            ImageButton cancelButton = (ImageButton) this.findViewById(R.id.btnCancel1);

            uploadControl = new UploadControl(progressBar, icon, cancelButton);

            Bitmap thumbNail = imageHelper.getThumbnail(fileName);

            //
            // TODO: Use default image.
            if ( thumbNail != null )
                uploadControl.setThumbnail(thumbNail);

            uploadControl.setVisibility(View.VISIBLE);

            try {
                afu.execute(new File(fileName));
            } catch (Exception e) {
                // TODO Show notice: upload failed
                e.printStackTrace();
            }

        }
    }


    public void uploadProgress(Object source, long bytesTransferred) {
        uploadControl.updateProgress((int) bytesTransferred);
    }

    public void uploadComplete(Object source, long bytesTransferred) {
        uploadControl.setVisibility(View.INVISIBLE);
        afu.removeUploadEventListener(this);
        setContentView(R.layout.actionchoice);
    }
}