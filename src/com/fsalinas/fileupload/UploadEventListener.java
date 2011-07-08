package com.fsalinas.fileupload;

public interface UploadEventListener {
	void uploadProgress(Object source, long bytesTransferred);
	void uploadComplete(Object source, long bytesTransferred);
}
