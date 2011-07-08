package com.fsalinas.fileupload;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class FileUploaderFactoryTest extends AndroidTestCase {

    private static final String TEST_URI = "http://www.google.com";

    public void testGetFileUploader() {
        FileUploader fileUploader = null;

        try {
            fileUploader = FileUploaderFactory.GetFileUploader(TEST_URI);
        } catch (MalformedURLException e) {
            Assert.fail("Invalid upload URI");
        } catch (URISyntaxException e) {
            Assert.fail("Invalid upload URI");
        }

        Assert.assertNotNull(fileUploader);
    }
}
