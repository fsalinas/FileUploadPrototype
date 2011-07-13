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
    private HttpPost httpPost;
    private DefaultHttpClient httpClient;

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

    public DefaultHttpClient getHttpClient() {

        if ( httpClient == null )
            httpClient = new DefaultHttpClient();

        return httpClient;
    }

    public void setHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpFileUploader(URI uri) {
        this.uri = uri;
    }


    @Override
    public Long uploadFile(File fileToUpload) {

        validateFileToUpload(fileToUpload);

        Long bytesUploaded = 0L;
        if ( fileToUpload.length() == 0 ) {
            return bytesUploaded;
        }

        getHttpClient().getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        httpPost = new HttpPost(this.uri);

        CountingMultipartEntity mpEntity = new CountingMultipartEntity(this.progressListener);
        mpEntity.addPart("file", GetContentBody(fileToUpload));

        httpPost.setEntity(mpEntity);

        HttpResponse response = null;
        try {
            response = getHttpClient().execute(httpPost);
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

        getHttpClient().getConnectionManager().shutdown();

        if (response != null) {
            bytesUploaded = response.getEntity().getContentLength();
        }

        return bytesUploaded;
    }

    private ContentBody GetContentBody(File file) {
        return new FileBody(file);
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
