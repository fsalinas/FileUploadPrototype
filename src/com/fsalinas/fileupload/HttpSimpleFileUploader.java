package com.fsalinas.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * Package: com.fsalinas.fileupload
 * User: fsalinasna
 * Date: 11/1/11
 * Time: 10:01 PM
 */
public class HttpSimpleFileUploader implements FileUploader {

    //
    // private fields
    private URI uri;
    private ProgressListener progressListener;
    private HttpMultipartClient httpMultipartClient;

    public HttpSimpleFileUploader(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public void setURI(URI uri) {
        this.uri = uri;
    }

    @Override
    public ProgressListener getProgressListener() {
        return this.progressListener;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Long uploadFile(File fileToUpload)  {

        validateFileToUpload(fileToUpload);

        Long bytesUploaded = 0L;
        if ( fileToUpload.length() == 0 ) {
            return bytesUploaded;
        }

        httpMultipartClient = new HttpMultipartClient(this.uri);

        FileInputStream fileInputStream;

        try
        {
            fileInputStream = new FileInputStream(fileToUpload);
        }
        catch (FileNotFoundException fnfEx)
        {
             return bytesUploaded;
        }
        httpMultipartClient.setRequestMethod("POST");
        httpMultipartClient.addFile(fileToUpload.getName(), fileInputStream, (int) fileToUpload.length());
        httpMultipartClient.setProgressListener(this.progressListener);

        try {
            httpMultipartClient.send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileToUpload.length();
    }

    @Override
    public void cancelUpload() {
        if ( httpMultipartClient != null)
        {
            try {
                httpMultipartClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateFileToUpload(File fileToUpload) {
        if (fileToUpload == null) {
            throw new IllegalArgumentException("Parameter fileToUpload is required");
        }

        if (!fileToUpload.exists()) {
            throw new IllegalArgumentException("File " + fileToUpload.getName() + " not found");
        }

        if (!fileToUpload.isFile()) {
            throw new IllegalArgumentException(fileToUpload.getName() + " is not a file");
        }
    }
}
