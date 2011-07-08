package com.fsalinas.fileupload;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HttpFileUploader implements FileUploader{

    private URI uri;
    private ProgressListener progressListener;
    
    public HttpFileUploader(URI uri) {
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

    private HttpPost httpPost;

    @Override
    public Long uploadFile(File fileToUpload) {

        validateFileToUpload(fileToUpload);

        Long bytesUploaded = 0L;
        if ( fileToUpload.length() == 0 ) {
            return bytesUploaded;
        }

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        httpPost = new HttpPost(this.uri);
        CountingMultipartEntity mpEntity = new CountingMultipartEntity(this.progressListener);

        ContentBody cbFile = new FileBody(fileToUpload);
        mpEntity.addPart("file", cbFile);

        httpPost.setEntity(mpEntity);

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (response != null) {
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                try {
                    resEntity.consumeContent();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        httpClient.getConnectionManager().shutdown();

        if (response != null) {
            bytesUploaded = response.getEntity().getContentLength();
        }

        return bytesUploaded;
    }

    private void validateFileToUpload(File fileToUpload) {
        if ( fileToUpload == null ) {
            throw new IllegalArgumentException("Parameter fileToUpload is required");
        }

        if ( ! fileToUpload.exists() ) {
            throw new IllegalArgumentException("File " + fileToUpload.getName() + " not found");
        }

        if ( ! fileToUpload.isFile()) {
            throw new IllegalArgumentException(fileToUpload.getName() + " is not a file");
        }
    }

    @Override
    public void cancelUpload() {
        httpPost.abort();

    }
}
