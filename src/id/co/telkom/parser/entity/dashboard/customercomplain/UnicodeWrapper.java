package id.co.telkom.parser.entity.dashboard.customercomplain;

import java.io.IOException;
import java.io.InputStream;

public class UnicodeWrapper extends InputStream {
	private final InputStream is;
	
	public UnicodeWrapper(InputStream is){
		super();
		this.is=is;
	}
	
	@Override
	public int read() throws IOException {
		int i = is.read();
		return i;
	}
	
	@Override
	public int read(byte[] paramArrayOfByte)
    throws IOException
    {
  	 return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
	
 	@Override
 	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
    {
	    if (paramArrayOfByte == null)
	      throw new NullPointerException();
	    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1))
	      throw new IndexOutOfBoundsException();
	    if (paramInt2 == 0)
	      return 0;
	    int i = read();
	    if(i==10||i==14||i==38)//here
			return 32;
	    if (i == -1)
	      return -1;
	    paramArrayOfByte[paramInt1] = (byte)i;
	    int j = 1;
	    try
	    {
	      while (j < paramInt2)
	      {
	        i = read();
	        if (i == -1)
	          break;
	        paramArrayOfByte[(paramInt1 + j)] = (byte)i;
	        ++j;
	      }
	    }
	    catch (IOException localIOException)
	    {
	    }
	    return j;
  }
}
