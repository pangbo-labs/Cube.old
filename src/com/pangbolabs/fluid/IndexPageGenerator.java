package com.pangbolabs.fluid;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class IndexPageGenerator
{
	public static String getIndexPage( File folder, String urlPath )
	{
		StringBuffer strbuf = new StringBuffer();
		
		try
		{
		strbuf.append( "<html>\n" );
		strbuf.append( "<header>\n" );
		strbuf.append( "<title>Index of " + urlPath + "</title>\n" );
		strbuf.append( "<style>\n" );
		strbuf.append( "h1 { font-size: 16pt; }\n" );
		strbuf.append( "td, th { font-size: 10pt; }\n" );
		strbuf.append( "</style>\n" );
		strbuf.append( "</header>\n" );
		strbuf.append( "<body>\n" );
		
		strbuf.append( "<h1>Index of " + urlPath + "</h1>\n" );
		
		strbuf.append( "<table>\n" );
		strbuf.append( "<tr>\n" );
		strbuf.append( "	<th align=\"left\">Name</th>\n" );
		strbuf.append( "	<th style=\"width: 15px;\"></th>\n" );
		strbuf.append( "	<th align=\"left\">Last Modified</th>\n" );
		strbuf.append( "	<th style=\"width: 15px;\"></th>\n" );
		strbuf.append( "	<th align=\"right\">Size</th>\n" );
		strbuf.append( "</tr>\n" );
		strbuf.append( "<tr>\n" );
		strbuf.append( "	<td colspan=\"5\"><hr></td>\n" );
		strbuf.append( "</tr>\n" );
		strbuf.append( "<tr>\n" );
		
		String[] files = folder.list();
		List<File> fileList = new ArrayList<>();
		for (int i = 0; i < files.length; i ++)
			fileList.add( new File( folder, files[i] ) );
		Collections.sort( fileList, new Comparator<File>() {
			@Override
			public int compare( File f1, File f2 )
			{
				if (f1.isDirectory() == f2.isDirectory())
					return f1.getName().compareTo( f2.getName() );
				return f1.isDirectory() ? -1 : 1;
			}
		});
		//SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-M-d H:mm" );
		long totalFolders = 0;
		long totalFiles = 0;
		long totalSize = 0;
		for (int i = 0; i < fileList.size(); i ++)
		{
			File file = fileList.get( i );
			Date lastModified = new Date( file.lastModified() );
			totalSize += file.length();
			String itemName = "";
			if (file.isDirectory())
			{
				itemName = file.getName() + "/";
				totalFolders ++;
			}
			else // file
			{
				itemName = file.getName();
				totalFiles ++;
			}
			String name = "<a href=\"" + itemName + "\">" + itemName + "</a>";
			strbuf.append( "<tr>\n" );
			strbuf.append( "	<td align=\"left\">" + name + "</td>\n" );
			strbuf.append( "	<td></td>\n" );
			strbuf.append( "	<td align=\"left\">" + dateFormat.format( lastModified ) + "</td>\n" );
			strbuf.append( "	<td></td>\n" );
			strbuf.append( "	<td align=\"right\">" + (file.isDirectory() ? "-" : formatSize( file.length() )) + "</td>\n" );
			strbuf.append( "</tr>\n" );
		}
		
		strbuf.append( "</tr>\n" );
		strbuf.append( "<tr>\n" );
		strbuf.append( "	<td colspan=\"5\"><hr></td>\n" );
		strbuf.append( "</tr>\n" );
		strbuf.append( "<tr>\n" );
		strbuf.append( "	<td colspan=\"3\">Totally " + totalFolders + " folder(s) and " + totalFiles + " file(s)</td>\n" );
		strbuf.append( "	<td></td>\n" );
		strbuf.append( "	<td align=\"right\">" + formatSize( totalSize ) + "</td>\n" );
		strbuf.append( "</tr>\n" );
		strbuf.append( "</table>\n" );
		
		strbuf.append( "</body>\n" );
		strbuf.append( "</html>\n" );
		}
		catch (Exception e)
		{
			
		}
		
		return strbuf.toString();
	}

	private static final long KB = 1024;
	private static final long MB = 1024 * KB;
	private static final long GB = 1024 * MB;
	private static final long TB = 1024 * GB;
	
	private static String formatSize( long size )
	{
		if (size > TB)
			return String.format( "%d TB", size / TB );
		
		if (size > GB)
			return String.format( "%d GB", size / GB );
		
		if (size > MB)
			return String.format( "%d MB", size / MB );
		
		if (size > KB)
			return String.format( "%d KB", size / KB );
		
		return String.format( "%d B", size );
	}
	
}
