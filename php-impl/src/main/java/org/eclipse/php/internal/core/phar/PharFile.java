/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zhao - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.internal.core.phar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.virtualFileSystem.archive.ArchiveFile;
import org.eclipse.php.internal.core.phar.streams.CBZip2InputStreamForPhar;
import org.eclipse.php.internal.core.phar.streams.GZIPInputStreamForPhar;
import org.eclipse.php.internal.core.phar.streams.PharEntryBufferedRandomInputStream;
import consulo.virtualFileSystem.archive.ArchiveEntry;

public class PharFile implements ArchiveFile
{
	private final File file;
	private int currentIndex;
	private int manifestLength;
	private int fileNumber;
	private int stubLength;
	private String version;
	private boolean hasSignature = false;
	private boolean hasZlibcompression = false;
	private boolean hasBzipcompression = false;
	private String alias;
	private String metadata;
	private List<PharEntry> pharEntryList = new ArrayList<PharEntry>();
	private Map<String, PharEntry> pharEntryMap = new HashMap<String, PharEntry>();
	private List<Integer> bytesAfterStub;
	private BufferedInputStream bis;

	public PharFile(File file) throws IOException
	{
		this.file = file;

		init();
	}

	protected void init() throws IOException
	{
		bis = new BufferedInputStream(new FileInputStream(file));
		try
		{
			getStub();
			getManifest();
			getEntries();
		}
		finally
		{
			bis.close();
		}
	}

	protected void getEntries() throws IOException
	{
		byte[] buffer;

		for(int j = 0; j < fileNumber; j++)
		{
			buffer = new byte[4];
			PharEntry pharEntry = new PharEntry();
			read(bis, buffer);
			int fileNameLength = getInt(buffer);

			// file Name
			String fileName = null;
			if(fileNameLength > 0)
			{
				buffer = new byte[fileNameLength];
				read(bis, buffer);
				fileName = getString(buffer);
				pharEntry.setName(fileName);
				buffer = new byte[4];
			}

			read(bis, buffer);
			pharEntry.setSizeByte(buffer);

			pharEntry.setSize(getInt(buffer));

			read(bis, buffer);
			pharEntry.setTime(getInt(buffer));

			read(bis, buffer);
			pharEntry.setCsize(getInt(buffer));

			read(bis, buffer);
			pharEntry.setCrcByte(buffer);

			read(bis, buffer);
			pharEntry.setBitMappedFlag(buffer);

			read(bis, buffer);
			int metaFileLength = getInt(buffer);

			// file Name
			String metaFileData = null;
			if(metaFileLength > 0)
			{
				buffer = new byte[metaFileLength];
				read(bis, buffer);
				metaFileData = getString(buffer);
				pharEntry.setMetadata(metaFileData);

			}

			pharEntryList.add(pharEntry);
			pharEntryMap.put(pharEntry.getName(), pharEntry);
		}
		for(int j = 0; j < pharEntryList.size(); j++)
		{

			PharEntry pharEntry = pharEntryList.get(j);
			if(j == 0)
			{
				pharEntry.setPosition(currentIndex);
			}
			else
			{
				pharEntry.setPosition(pharEntryList.get(j - 1).getEnd());
			}

		}

		PharEntry stubEntry = new PharEntry();
		stubEntry.setName(PharConstants.STUB_PATH);
		// pharEntry.setSizeByte(buffer);
		stubEntry.setSize(stubLength);
		stubEntry.setCsize(stubLength);
		// pharEntry.setCrcByte(buffer);
		stubEntry.setBitMappedFlag(PharConstants.Default_Entry_Bitmap);
		stubEntry.setPosition(0);
		pharEntryList.add(stubEntry);
		pharEntryMap.put(stubEntry.getName(), stubEntry);

		if(hasSignature)
		{
			PharEntry signatureEntry = new PharEntry();
			signatureEntry.setName(PharConstants.SIGNATURE_PATH);
			signatureEntry.setBitMappedFlag(PharConstants.Default_Entry_Bitmap);

			int signatureLength = bis.available();
			if(fileNumber == 0)
			{// no file,so the manifest's end is the
				// signature's begin
				signatureEntry.setPosition(currentIndex);

			}
			else
			{
				signatureEntry.setPosition(pharEntryList.get(fileNumber - 1).getEnd());
				PharUtil.skip(bis, pharEntryList.get(fileNumber - 1).getEnd() - currentIndex);
				signatureLength = bis.available();
			}
			if(signatureLength <= 4)
			{
				signatureEntry = null;
				return;
			}
			signatureEntry.setSize(signatureLength);
			signatureEntry.setCsize(signatureLength);
			if(signatureLength < 24)
			{
				throw new IOException("Phar Signature Corrupted");
			}
			else
			{

				bis.skip(signatureLength - 8);
				buffer = new byte[4];
				read(bis, buffer);
				boolean found = false;
				for(Iterator<Digest> iterator = Digest.DIGEST_MAP.values().iterator(); iterator.hasNext(); )
				{
					Digest digest = iterator.next();
					if(PharUtil.byteArrayEquals(digest.getBitMap(), buffer))
					{
						if(digest.getDigest().digest().length != signatureLength - 8 || !PharUtil.checkSignature(file, digest, signatureEntry.getPosition()))
						{
							throw new IOException("Phar Signature Corrupted");
						}
						else
						{
							found = true;
							break;
						}

					}
				}
				if(!found)
				{
					throw new IOException("Phar Signature unsupported");
				}
				read(bis, buffer);
				if(!PharUtil.byteArrayEquals(PharConstants.GBMB, buffer))
				{
					throw new IOException("Phar Signature end");
				}
			}
			pharEntryList.add(signatureEntry);
			pharEntryMap.put(signatureEntry.getName(), signatureEntry);
		}

	}

	protected void getManifest() throws IOException
	{
		int lineSeparatorLength = 0;
		bytesAfterStub.add(Integer.valueOf(read(bis)));
		bytesAfterStub.add(Integer.valueOf(read(bis)));
		if(bytesAfterStub.get(0).intValue() == PharConstants.R)
		{
			lineSeparatorLength++;
			if(bytesAfterStub.get(1).intValue() == PharConstants.N)
			{
				lineSeparatorLength++;
			}

		}
		else if(bytesAfterStub.get(0).intValue() == PharConstants.N)
		{
			lineSeparatorLength++;
			if(bytesAfterStub.get(1).intValue() == PharConstants.R)
			{
				lineSeparatorLength++;
			}
		}
		bytesAfterStub = bytesAfterStub.subList(lineSeparatorLength, bytesAfterStub.size());
		stubLength = currentIndex - bytesAfterStub.size();
		// read(bis);
		// read(bis);
		byte[] buffer = new byte[4];
		for(int i = 0; i < buffer.length; i++)
		{
			if(i < bytesAfterStub.size())
			{
				buffer[i] = ((Integer) bytesAfterStub.get(i)).byteValue();
			}
			else
			{
				buffer[i] = (byte) read(bis);
				check(buffer[i]);
			}
		}

		manifestLength = getInt(buffer);

		read(bis, buffer);
		fileNumber = getInt(buffer);

		buffer = new byte[2];
		read(bis, buffer);
		version = PharUtil.getVersion(buffer);

		buffer = new byte[4];
		read(bis, buffer);
		if((buffer[2] & 1) != 0)
		{
			hasSignature = true;
		}
		if((buffer[1] & 16) != 0)
		{
			hasZlibcompression = true;
		}
		if((buffer[1] & 32) != 0)
		{
			hasBzipcompression = true;
		}

		read(bis, buffer);
		int aliaslength = getInt(buffer);
		if(aliaslength > 0)
		{
			buffer = new byte[aliaslength];
			read(bis, buffer);
			alias = getString(buffer);
			buffer = new byte[4];
		}

		read(bis, buffer);
		int metadatalength = getInt(buffer);
		if(metadatalength > 0)
		{
			buffer = new byte[metadatalength];
			read(bis, buffer);
			metadata = getString(buffer);
			// buffer = new byte[4];
		}

	}

	protected void getStub() throws IOException
	{
		boolean stubHasBeenFound = false;
		int n = -1;
		// this is record for whether read a byte from the stream or not
		int currentByte = -1;
		bytesAfterStub = new ArrayList<Integer>();
		// if currentByte is equal to char '_',we will not read the next byte
		while(!stubHasBeenFound && (currentByte == PharConstants.Underline || (n = read(bis)) != -1))
		{
			if(n == PharConstants.Underline)
			{
				boolean match = false;
				int j = 1;
				for(; j < PharConstants.STUB_ENDS.length && n != -1; j++)
				{
					if((n = read(bis)) == PharConstants.STUB_ENDS[j])
					{
						if(j == PharConstants.STUB_ENDS.length - 1)
						{
							match = true;
						}
					}
					else
					{
						break;
					}
				}
				stubHasBeenFound = match;
				if(match)
				{
					// i = i + ENDS.length;

					j = 0;
					match = false;
					for(; j < PharConstants.STUB_TAIL.length && n != -1; j++)
					{
						n = read(bis);
						bytesAfterStub.add(Integer.valueOf(n));
						if(n == PharConstants.STUB_TAIL[j])
						{
							if(j == PharConstants.STUB_TAIL.length - 1)
							{
								match = true;
							}
						}
						else
						{
							break;
						}
					}
					if(match)
					{
						bytesAfterStub.clear();
					}
				}
			}
		}
		if(!stubHasBeenFound)
		{
			PharUtil.throwPharException("Phar no stub no end");
		}
	}

	public static String getString(byte[] subBytes)
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < subBytes.length; i++)
		{
			sb.append((char) subBytes[i]);
		}
		return sb.toString();
	}

	private int read(BufferedInputStream bis, byte[] buffer) throws IOException
	{
		int result = bis.read(buffer);
		if(result != buffer.length)
		{
			PharUtil.throwPharException("Phar corrupted");
		}
		currentIndex = currentIndex + result;
		return result;
	}

	private void check(byte b) throws IOException
	{
		if(b == -1)
		{
			PharUtil.throwPharException("Phar corrupted");
		}
	}

	public static int getInt(byte[] subBytes)
	{
		int result = 0;
		if(subBytes.length > 0)
		{
			result = PharUtil.getPositive(subBytes[subBytes.length - 1]);
			for(int i = 0; i < subBytes.length - 1; i++)
			{
				result = result * 256 + PharUtil.getPositive(subBytes[subBytes.length - 2 - i]);
			}
		}
		return result;
	}

	private int read(BufferedInputStream bis) throws IOException
	{
		currentIndex++;
		int result = bis.read();
		return result;
	}

	@Override
	public PharEntry getEntry(String name)
	{
		return pharEntryMap.get(name);
	}

	@Nonnull
	@Override
	public Iterator<? extends ArchiveEntry> entries()
	{
		return pharEntryList.iterator();
	}

	@Override
	public int getSize()
	{
		return pharEntryList.size();
	}

	@Override
	public void close() throws IOException
	{
	}

	@Nullable
	@Override
	public InputStream getInputStream(ArchiveEntry archiveEntry) throws IOException
	{
		if(archiveEntry instanceof PharEntry)
		{
			return getInputStream0((PharEntry) archiveEntry);
		}
		return null;
	}

	private InputStream getInputStream0(PharEntry pharEntry) throws IOException
	{
		InputStream result = null;
		InputStream is = new PharEntryBufferedRandomInputStream(file, pharEntry);
		if(pharEntry.isCompressed())
		{
			int ctype = pharEntry.getCompressedType();
			if(PharConstants.BZ2_COMPRESSED == ctype)
			{
				is = new CBZip2InputStreamForPhar(is);
			}
			else if(PharConstants.GZ_COMPRESSED == ctype)
			{
				is = new GZIPInputStreamForPhar(is);
			}
		}
		result = new BufferedInputStream(is);
		return result;
	}

	public String getName()
	{
		return file.getPath();
	}

	public List<PharEntry> getPharEntryList()
	{
		return pharEntryList;
	}

	public File getFile()
	{
		return file;
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex)
	{
		this.currentIndex = currentIndex;
	}

	public int getManifestLength()
	{
		return manifestLength;
	}

	public void setManifestLength(int manifestLength)
	{
		this.manifestLength = manifestLength;
	}

	public int getFileNumber()
	{
		return fileNumber;
	}

	public void setFileNumber(int fileNumber)
	{
		this.fileNumber = fileNumber;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public boolean isHasSignature()
	{
		return hasSignature;
	}

	public void setHasSignature(boolean hasSignature)
	{
		this.hasSignature = hasSignature;
	}

	public boolean isHasZlibcompression()
	{
		return hasZlibcompression;
	}

	public void setHasZlibcompression(boolean hasZlibcompression)
	{
		this.hasZlibcompression = hasZlibcompression;
	}

	public boolean isHasBzipcompression()
	{
		return hasBzipcompression;
	}

	public void setHasBzipcompression(boolean hasBzipcompression)
	{
		this.hasBzipcompression = hasBzipcompression;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public String getMetadata()
	{
		return metadata;
	}

	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	public Map<String, PharEntry> getPharEntryMap()
	{
		return pharEntryMap;
	}

	public void setPharEntryMap(Map<String, PharEntry> pharEntryMap)
	{
		this.pharEntryMap = pharEntryMap;
	}
}