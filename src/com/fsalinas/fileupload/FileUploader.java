package com.fsalinas.fileupload;

import java.io.File;
import java.net.URI;

public interface FileUploader {

    public URI getURI();
    public void setURI(URI uri);

    public ProgressListener getProgressListener();
    public void setProgressListener(ProgressListener progressListener);

    public Long uploadFile(File fileToUpload);
    public void cancelUpload();
}
