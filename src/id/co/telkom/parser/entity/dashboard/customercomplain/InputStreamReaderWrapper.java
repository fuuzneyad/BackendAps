package id.co.telkom.parser.entity.dashboard.customercomplain;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

public class InputStreamReaderWrapper extends InputStreamReader{

	public InputStreamReaderWrapper(InputStream paramInputStream) {
		super(paramInputStream);
	}
	
	@Override
	public int read(CharBuffer paramCharBuffer)
    throws IOException
  {
    int i = paramCharBuffer.remaining();
    char[] arrayOfChar = new char[i];
    int j = read(arrayOfChar, 0, i);
    if (j > 0){
      j = j >0 && j<32 ? 0 : j;
      paramCharBuffer.put(arrayOfChar, 0, j);
    }
    return j;
  }

}
