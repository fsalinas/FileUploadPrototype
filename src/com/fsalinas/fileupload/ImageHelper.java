package com.fsalinas.fileupload;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: fsalinas
 * Date: 5/24/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageHelper {

    private Activity _activity;
    public ImageHelper(Activity activity) {
       this._activity = activity;
    }


    public Bitmap getThumbnail(String fileName)	{
		String [] projection = {
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA
		};


		//
		// try getting an image thumb-nail
		Cursor cursor = _activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.DATA + "= '" + fileName + "' ", null, null);

		Bitmap thumbNail = null;

		if ( cursor.moveToFirst() ) {
			thumbNail = MediaStore.Images.Thumbnails.getThumbnail(_activity.getContentResolver(),
					Integer.valueOf(cursor.getString(0)),
					MediaStore.Images.Thumbnails.MICRO_KIND, null);
		}
		else
		{
			//
			// try getting a video thumb-nail
			String [] vproj = {
					MediaStore.Video.Media._ID,
					MediaStore.Video.Media.DATA
			};

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;

			cursor = _activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, vproj, MediaStore.Video.Media.DATA + "= '" + fileName + "' ", null, null);

			if ( cursor.moveToFirst() )
			{
				thumbNail = MediaStore.Video.Thumbnails.getThumbnail(_activity.getContentResolver(),
					Integer.valueOf(cursor.getString(0)),
					MediaStore.Video.Thumbnails.MICRO_KIND, options);
			}

		}

		return thumbNail;
	}

	public Uri getCapturedImageUri() {
		//create parameters for Intent with filename
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "new-photo-name.jpg");
		values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
		return _activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	public File convertImageUriToFile(Uri imageUri, Activity activity)  {
		Cursor cursor = null;
		try {
		    String [] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
		    cursor = activity.managedQuery( imageUri,
		            proj, // Which columns to return
		            null,       // WHERE clause; which rows to return (all rows)
		            null,       // WHERE clause selection arguments (none)
		            null); // Order-by clause (ascending by name)
		    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    //int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
		    if (cursor.moveToFirst()) {
		        //String orientation =  cursor.getString(orientation_ColumnIndex);
		        return new File(cursor.getString(file_ColumnIndex));
		    }
		    return null;
		} finally {
		    if (cursor != null) {
		        cursor.close();
		    }
		}
	}

	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = _activity.managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}
