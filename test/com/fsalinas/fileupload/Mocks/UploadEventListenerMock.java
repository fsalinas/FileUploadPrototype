package com.fsalinas.fileupload.Mocks;

import com.fsalinas.fileupload.AsyncFileUploader;
import com.fsalinas.fileupload.UploadEventListener;

/**
 * Created by IntelliJ IDEA.
 * User: fsalinas
 * Date: 5/27/11
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadEventListenerMock implements UploadEventListener {

    public long getProgress() {
        return progress;
    }

    private long progress = 0L;


    @Override
    public void uploadProgress(Object source, long bytesTransferred) {
        progress = bytesTransferred;
    }

    @Override
    public void uploadComplete(Object source, long bytesTransferred) {
        AsyncFileUploader htfu = (AsyncFileUploader) source;
         htfu.removeUploadEventListener(this);
    }
}
