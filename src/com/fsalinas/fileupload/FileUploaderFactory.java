package com.fsalinas.fileupload;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUploaderFactory {

    public static FileUploader GetFileUploader(String uri) throws MalformedURLException, URISyntaxException {

        //
        // Create URI
        URI uploadURI = new URL(uri).toURI();

        FileUploader fileUploader = null;
        switch (GetConfiguredType())
        {
            case HttpUploader:
                fileUploader = new HttpFileUploader(uploadURI);
                break;
            case HttpSimpleUploader:
                fileUploader = new HttpSimpleFileUploader(uploadURI);
                break;
            case AWSUploader:
                fileUploader = null;
                break;
        }

        return fileUploader;
    }

    private static FileUploaderType GetConfiguredType() {
        //
        // TODO: Read from Configuration Singleton
        return FileUploaderType.HttpSimpleUploader;
    }
}
