package com.fsalinas.fileupload;

import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class UploadControl {
	
	private ProgressBar progressBar;
	private ImageView fileThumbnail;
	private ImageButton cancelButton;
	
	/**
	 * @param progressBar the progressBar to set
	 */
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * @return the progressBar
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @param fileThumbnail the fileThumbnail to set
	 */
	public void setFileThumbnail(ImageView fileThumbnail) {
		this.fileThumbnail = fileThumbnail;
	}

    public UploadControl(ProgressBar progressBar, ImageView thumbnail, ImageButton cancel)
	{
		setProgressBar(progressBar);
		setFileThumbnail(thumbnail);
		cancelButton = cancel;
	}
	
	public void setThumbnail(Bitmap image) {
		fileThumbnail.setImageBitmap(image);
	}
	
	public void setVisibility(int visibility) {
		progressBar.setVisibility(visibility);
		fileThumbnail.setVisibility(visibility);
		cancelButton.setVisibility(visibility);
	}

    public void updateProgress(int bytesTransferred) {
        getProgressBar().setProgress(bytesTransferred);
    }
}
