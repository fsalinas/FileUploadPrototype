/**
 * 
 */
package com.fsalinas.fileupload;

import java.util.EventObject;

/**
 * @author fsalinas
 *
 * For passing data and target object when a
 * file upload is complete
 */
public class UploadEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7954613053908199776L;
	private Long bytesTransfered;
	
	public Long getBytesTransfered() {
		return bytesTransfered;
	}

	public void setBytesTransfered(Long bytesTransfered) {
		this.bytesTransfered = bytesTransfered;
	}

	public UploadEvent(Object source, Long bytesTransferred) {
		super(source);
		setBytesTransfered(bytesTransferred);
	}

}
