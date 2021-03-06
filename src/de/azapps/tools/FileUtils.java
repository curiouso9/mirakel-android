/*******************************************************************************
 * Mirakel is an Android App for managing your ToDo-Lists
 * 
 * Copyright (c) 2013 Anatolij Zelenin, Georg Semmler.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.azapps.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class FileUtils {
	private static final String TAG = "FileUtils";

	public static String getPath(Context context, Uri uri)
			throws URISyntaxException {
		if (uri == null)
			return null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Copy File
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException {
		if(!src.canRead()||!dst.canWrite()){
			Log.e(TAG,"cannot copy file");
			return;
		}
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/**
	 * Unzip a File and Copy it to a location
	 * 
	 * @param zipFile
	 * @param location
	 */
	public static void unzip(File zipFile, File location)
			throws FileNotFoundException, IOException {

		FileInputStream fin = new FileInputStream(zipFile);
		ZipInputStream zin = new ZipInputStream(fin);
		ZipEntry ze = null;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				dirChecker(location, ze.getName());
			} else {
				FileOutputStream fout = new FileOutputStream(new File(location,
						ze.getName()));
				for (int c = zin.read(); c != -1; c = zin.read()) {
					fout.write(c);
				}

				zin.closeEntry();
				fout.close();
			}

		}
		zin.close();

	}

	private static void dirChecker(File location, String dir) {
		File f = new File(location, dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

	public static void writeToFile(File f, String s) {
		try {
			if (f.exists())
				f.delete();
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(s);
			out.close();
		} catch (IOException e) {
			Log.e(TAG, "cannot write to file");
			Log.e(TAG, Log.getStackTraceString(e));
		}

	}

}
