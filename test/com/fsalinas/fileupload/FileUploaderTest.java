package com.fsalinas.fileupload;

import android.test.AndroidTestCase;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploaderTest extends AndroidTestCase {

    private final static long MOCK_FILE_SIZE = 10000L;

    private File mockFile;

    public void setUp() throws Exception {
        //
        // Create File Mock

/*        mockFile = mock(File.class);
        when(mockFile.canRead()).thenReturn(true);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.length()).thenReturn(MOCK_FILE_SIZE);*/

    }

    public void tearDown() throws Exception {
        //
        // Delete File Mock
    }

    public Long testUploadFile(File fileToUpload) {
        return 0L;
    }

    public void testCancelUpload() {

    }
}
