package com.fsalinas.fileupload;

import android.test.AndroidTestCase;
import com.fsalinas.fileupload.Mocks.UploadEventListenerMock;
import junit.framework.Assert;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: fsalinas
 * Date: 5/27/11
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncFileUploaderTest extends AndroidTestCase {
    private static final String TESTURL = "http://localhost/" ;
    private UploadEventListener mockUploadEventListener;

    public void setUp() throws Exception {
        mockUploadEventListener = new UploadEventListenerMock();
    }

    public void tearDown() throws Exception {
        mockUploadEventListener = null;
    }

    public void testAddUploadEventListener() throws Exception {
        AsyncFileUploader fileUploader = new AsyncFileUploader(TESTURL);

        fileUploader.addUploadEventListener(mockUploadEventListener);

        Assert.assertEquals(1, fileUploader.getListeners().size());
        Assert.assertEquals(mockUploadEventListener, fileUploader.getListeners().get(0));
    }

    public void testRemoveUploadEventListener() throws Exception {
        AsyncFileUploader fileUploader = new AsyncFileUploader(TESTURL);

        fileUploader.addUploadEventListener(mockUploadEventListener);

        Assert.assertEquals(1, fileUploader.getListeners().size());
        Assert.assertEquals(mockUploadEventListener, fileUploader.getListeners().get(0));

        fileUploader.removeUploadEventListener(mockUploadEventListener);
        Assert.assertEquals(0, fileUploader.getListeners().size());

    }

    public void testExecute() throws Exception {
        File testFile = mock(File.class);

        Mockito.when(testFile.length()).thenReturn(10000L);


    }

    public void testCancelUpload() throws Exception {

    }
}
