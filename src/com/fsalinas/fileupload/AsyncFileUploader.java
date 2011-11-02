package com.fsalinas.fileupload;

import android.os.AsyncTask;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Vector;

public class AsyncFileUploader extends AsyncTask<File, Integer, Long> implements ProgressListener {

    private FileUploader fileUploader = null;
    private Long fileSize;
	private Vector<UploadEventListener> listeners = new Vector<UploadEventListener>();
	
	AsyncFileUploader(String urlString) throws MalformedURLException, URISyntaxException {

		fileSize = 0L;
        fileUploader = FileUploaderFactory.GetFileUploader(urlString);
        fileUploader.setProgressListener(this);
    }

	public synchronized void addUploadEventListener (UploadEventListener listener) {
		    getListeners().addElement(listener);
	}
	
	public synchronized void removeUploadEventListener (UploadEventListener listener) {
	    getListeners().removeElement(listener);
	}


	protected void onProgressUpdate(Integer... progress) {
		Enumeration<UploadEventListener> e = getListeners().elements();

		while( e.hasMoreElements() ) {
	      e.nextElement().uploadProgress(this, (long) progress[0]);
	    }
    }
	
	@Override
	public Long doInBackground(File...file )  {
		
		fileSize = file[0].length();


        return fileUploader.uploadFile(file[0]);
    }
	
	@Override
	public void transferred(long num) {
		int percentProgress = (int)( (num / (float)fileSize) * 100 );
		publishProgress(percentProgress);
	}
	
	@Override
	public void onPostExecute(Long requestBytesSent)
	{
		Enumeration<UploadEventListener> e = getListeners().elements();

		while( e.hasMoreElements() ) {
	      e.nextElement().uploadComplete(this, requestBytesSent);
	    }
	}
	
	public void cancelUpload()
	{	
		fileUploader.cancelUpload();
		this.cancel(true);
	}

    public Vector<UploadEventListener> getListeners() {
        return listeners;
    }
}