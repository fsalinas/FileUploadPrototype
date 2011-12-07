package com.fsalinas.fileupload;

import android.util.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Upload a file to server chunk by chunk, using socket connection instead of
 * standard android library because of infamous pre froyo android bug 3164
 * http://code.google.com/p/android/issues/detail?id=3164#c6
 *
 * Class could be redefined in order to implement nice features, like notify
 * of upload progress and other goodies
 *
 *
 * -- Original credits: keianhzo
 * http://groups.google.com/group/android-developers/msg/4ddd2e502f195e3a
 *
 * -- How to use it:
 * http://stackoverflow.com/questions/2105364/android-i-want-to-show-file-upload-progress-to-the-user
 * HttpMultipartClient httpMultipartClient = new HttpMultipartClient("v2.bluppr.nl", "/webservice/placeorder", 80);
 * FileInputStream fis = new FileInputStream(path + fileName);
 * httpMultipartClient.addFile(fileName, fis, fis.available());
 * httpMultipartClient.setRequestMethod("POST");
 * httpMultipartClient.send();
 *
 *
 * -- Server side code:
 * <?php
 * $target_path = "uploads/";
 * $target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
 * if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
 *     echo "The file ".  basename( $_FILES['uploadedfile']['name'])." has been uploaded " .$_POST["order"]. " post";
 * } else{
 *     echo "There was an error uploading the file, please try again!";
 * }
 * ?>
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class HttpMultipartClient {

    public ProgressListener getProgressListener() {
        return this.progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }


    public class Parameter {
		private String name;
		private String value;

		public Parameter(String n, String v) {
			name = n;
			value = v;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getName() + ": " + getValue() + END;
		}
	}

	private static final String TAG = "HttpMultipartClient";
	private static final String END = "\r\n";
	private static final int CONNECTION_TIMEOUT = 10000;
	private final String boundary = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
	private final String lastBoundary = END + "--" + boundary + "--" + END;


	private Socket socket;
	private String host;
	private int port;
	private String path;
	private String method;

	private List<Parameter> headers;
	private List<Parameter> cookies;
	private List<Parameter> fields;

	private String fileName;
	private InputStream fileStream;
	private int fileSize;
	private int responseCode;
	private String responseMessage;
	private String responseBody;
	private List<Parameter> responseHeaders;
	private StringBuilder headersBuffer;
	private StringBuilder bodyBuffer;
	private long length = 0;
    private ProgressListener progressListener;
    private Boolean isCancelled = false;

	public HttpMultipartClient(URI uri)
			throws IllegalArgumentException {
		if (uri == null )
			throw new IllegalArgumentException("Invalid uri");
		else {
			this.host = uri.getHost();
			this.path = uri.getPath();
			this.port = uri.getPort();
			if ( this.port == -1 )
				this.port = 80;
			socket = new Socket();
			headers = new ArrayList<Parameter>();
			cookies = new ArrayList<Parameter>();
			fields = new ArrayList<Parameter>();
			responseHeaders = new ArrayList<Parameter>();
			responseBody = null;
		}
	}

	public void disconnect() throws IOException {
		socket.close();
        isCancelled = true;
	}

	public void addHeader(String name, String value)
			throws IllegalArgumentException {
		if (name == null || value == null)
			throw new IllegalArgumentException("Header invalid: name=" + name
					+ ", value=" + value);
		else {
			headers.add(new Parameter(name, value));
			if (Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "Adding header [" + name + ": " + value + "]");
		}
	}

	public void addCookie(String name, String value)
			throws IllegalArgumentException {
		if (name == null || value == null)
			throw new IllegalArgumentException("Cookie invalid: name=" + name
					+ ", value=" + value);
		else {
			cookies.add(new Parameter(name, value));
			if (Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "Adding cookie [" + name + ": " + value + "]");
		}
	}

	public void addField(String name, String value)
			throws IllegalArgumentException {
		if (name == null || value == null)
			throw new IllegalArgumentException("Field invalid: name=" + name
					+ ", value=" + value);
		else {
			fields.add(new Parameter(name, value));
			if (Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "Adding field [" + name + ": " + value + "]");
		}
	}

	public void addFile(String fn, InputStream is, int fs) {
		if (is == null)
			throw new IllegalArgumentException("Invalid null input stream");
		else {
			fileName = fn;
			fileStream = is;
			fileSize = fs;
			if (Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "Adding file [filename: " + fileName + "]");
		}
	}

	private void prepare() {
		preHeaders();
		prepareBody();
		postHeaders();
	}

    private void preHeaders() {
        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Pre headers");

        headersBuffer = new StringBuilder();
        headersBuffer.append(method + " " + path + " HTTP/1.1" + END);
        headersBuffer.append("User-Agent: FileSocialClient 1.0" + END);
        headersBuffer.append("Host: " + host + END);
        headersBuffer.append("Content-Type: multipart/form-data; boundary="
                + boundary + END);

        if (!headers.isEmpty()) {
            for (Parameter param : headers) {
                headersBuffer.append(param.getName());
                headersBuffer.append(": ");
                headersBuffer.append(param.getValue());
                headersBuffer.append(END);

                if (Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, "Header added: " + param);
            }
        }

        if (!cookies.isEmpty()) {
            headersBuffer.append("Cookie: ");
            for (Iterator<Parameter> it = cookies.iterator(); it.hasNext();) {
                Parameter param = it.next();

                headersBuffer.append(param.getName());
                headersBuffer.append("=");
                headersBuffer.append(param.getValue());

                if (it.hasNext())
                    headersBuffer.append("; ");

                if (Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, "Cookie added: " + param);
            }
            headersBuffer.append(END);
        }

        headersBuffer.append("Content-Length: ");
    }

    private void postHeaders() {
        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Post headers");

        length = fileSize + lastBoundary.length() + bodyBuffer.length();
        headersBuffer.append(length);
        headersBuffer.append(END + END);
    }

    private void prepareBody() {
        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Preparing body");

        bodyBuffer = new StringBuilder();

        if (!fields.isEmpty()) {
            for (Parameter param : fields) {
                bodyBuffer.append("--");
                bodyBuffer.append(boundary);
                bodyBuffer.append(END);
                bodyBuffer.append("Content-Disposition: form-data; name=\"");
                bodyBuffer.append(param.getName());
                bodyBuffer.append("\"");
                bodyBuffer.append(END);
                bodyBuffer.append(END);
                bodyBuffer.append(param.getValue());
                bodyBuffer.append(END);

                if (Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, "Field added: " + param);
            }
        }

        if (fileStream != null) {
            bodyBuffer.append("--");
            bodyBuffer.append(boundary);
            bodyBuffer.append(END);
            bodyBuffer.append("Content-Disposition: form-data; name=\"");
            bodyBuffer.append("file");
            bodyBuffer.append("\"; filename=\"");
            bodyBuffer.append(fileName);
            bodyBuffer.append("\"");
            bodyBuffer.append(END);
            bodyBuffer.append(END);
        }
    }


	public void send() throws IOException {
		prepare();
		BufferedReader reader = null;
		try {
			// We send the Http Request
			socket.connect(new InetSocketAddress(host, port));
			socket.setSoTimeout(CONNECTION_TIMEOUT);
			int bytesSent = 0;
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(headersBuffer);
			out.print(bodyBuffer);
			bytesSent += headersBuffer.length() + bodyBuffer.length();
			byte[] bytes = new byte[1024 * 65];
			int size;
			while ((size = fileStream.read(bytes)) > 0) {
				bytesSent += size;
				out.write(bytes, 0, size);
				out.flush();
                this.progressListener.transferred((long)bytesSent);
			}
			out.print(lastBoundary);
			out.flush();
			// We read the response from the server
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = reader.readLine();
			String[] responseLine = line.trim().split(" ", 3);
			responseCode = new Integer(responseLine[1]);
			responseMessage = responseLine[2];
			boolean headersEnd = false;
			while ((line = reader.readLine()) != null && !headersEnd) {
				if (line.length() == 0)
					headersEnd = true;
				else {
					String[] headerLine = line.trim().split(":", 2);
					responseHeaders.add(new Parameter(headerLine[0],
							headerLine[1]));
				}
			}
			StringBuilder payload = new StringBuilder();
			boolean bodyEnd = false;
			while ((line = reader.readLine()) != null && !bodyEnd) {
				if (line.length() == 0)
					bodyEnd = true;
				else
					payload.append(line.trim());
			}
			responseBody = payload.toString();
		} finally {
			try {
				fileStream.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				if ( ! isCancelled)
				    e.printStackTrace();
			}
		}
	}

	public void setRequestMethod(String m) {
		method = m;
	}

	public void setPath(String p) {
		path = p;
	}

	public String getRequestMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public List<Parameter> getResponseHeaders() {
		return responseHeaders;
	}

	public String getResponseBody() {
		return responseBody;
	}
}
