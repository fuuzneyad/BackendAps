package id.co.telkom.parser.common.charparser;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends java.io.InputStream {
	private final InputStream is;
	private int c = -1;
	private int index = 0;
	private int[] startByte = {0,0,0,0,0};
	private int[] endByte = {0,0,0,0,0};
	private int[] lastChar = {-1,-1,-1,-1,-1};
	private int[] prefixSpace = {-1,-1,-1,-1,-1};
	private int firstNewLine = -1;
	private boolean startSpace = true;
	private boolean isNewline = false;
	private boolean firstCheck = true;
	private byte[][] buffer = new byte[5][1024];

	public InputStreamWrapper(InputStream is) {
		super();
		this.is = is;
		buffer[0][0] = '<';
		buffer[0][1] = ' ';
		buffer[1][0] = '<';
		buffer[1][1] = ' ';
		buffer[2][0] = '<';
		buffer[2][1] = ' ';
		buffer[3][0] = '<';
		buffer[3][1] = ' ';
		buffer[4][0] = '<';
		buffer[4][1] = ' ';
		try {
			c = is.read();
			for(int i=0; i<buffer.length; i++)
				readToBuffer(i);
		} catch (IOException e) {
			c = -1;
		}
	}

	@Override
	public int available() throws IOException {
		return is.available() + endByte[0]-startByte[0] + endByte[1]-startByte[1] +
				endByte[2]-startByte[2] + endByte[3] - startByte[3] + endByte[4] - startByte[4];
	}

	@Override
	public void close() throws IOException {
		is.close();
		super.close();
	}

	@Override
	public synchronized void mark(int arg0) {
		is.mark(arg0);
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		int i = arg1;
		for(; i<arg0.length&&i-arg1<arg2; i++) {
			arg0[i] = (byte)read();
			if(arg0[i]==-1)
				break;
		}
		if(i>arg1)
			return i-arg1;
		else
			return -1;
	}

	@Override
	public int read(byte[] arg0) throws IOException {
		int i = 0;
		for(;i<arg0.length; i++) {
			arg0[i] = (byte)read();
			if(arg0[i]==-1)
				break;
		}
		if(i==0)
			return -1;
		else
			return i;
	}

	@Override
	public int read() throws IOException {
		if(startByte[index]>=endByte[index]) {
			readToBuffer(index);
			if(index==buffer.length-1)
				index = 0;
			else
				index++;
		}
		byte retVal = buffer[index][startByte[index]];
		startByte[index]++;
		return retVal;
	}

	@Override
	public synchronized void reset() throws IOException {
		startByte[0] = 0;
		endByte[0] = 0;
		startByte[1] = 0;
		endByte[1] = 0;
		startByte[2] = 0;
		endByte[2] = 0;
		startByte[3] = 0;
		endByte[3] = 0;
		startByte[4] = 0;
		endByte[4] = 0;
		isNewline = false;
		is.reset();
		try {
			c = is.read();
			for(int i=0; i<buffer.length; i++)
				readToBuffer(i);
		} catch (IOException e) {
			c = -1;
		}
	}

	@Override
	public long skip(long arg0) throws IOException {
		long i = 0;
		int j = 0;
		while(arg0>i) {
			j=endByte[index]-startByte[index];
			if(arg0-i>j) {
				if(c!=-1) {
					i += j+1;
					startByte[index] = endByte[index];
					read();
				} else {
					int k = read();
					if(k==-1)
						break;
					i++;
				}
			} else {
				if(c!=-1) {
					startByte[index] += arg0-i;
					i = arg0;
				} else {
					int k = read();
					if(k==-1)
						break;
					i++;
				}
			}
		}
		return i;
	}

	private synchronized void readToBuffer(int idx) throws IOException {
		startByte[idx] = 2;
		prefixSpace[idx] = -1;
		firstNewLine = -1;
		lastChar[idx] = 0;
		startSpace = true;
		isNewline = false;
		endByte[idx] = 2;
		while(endByte[idx]<1024) {
			if(c=='\r' || c=='\n') {
				isNewline = true;
				if(firstNewLine==-1) {
					if(buffer[idx][endByte[idx]-1]==' ')
						endByte[idx]--;
					firstNewLine = endByte[idx];
				}
				buffer[idx][endByte[idx]] = (byte)c;
				endByte[idx]++;
			} else if(isNewline) {
				break;
			} else {
				if(c==' ') {
					if(startSpace)
						prefixSpace[idx] = endByte[idx];
				} else {
					startSpace = false;
					lastChar[idx] = endByte[idx];
				}
				buffer[idx][endByte[idx]] = (byte)c;
				endByte[idx]++;
			}
			c = is.read();
		}
		if(prefixSpace[idx]==firstNewLine && prefixSpace[idx]>-1)
			startByte[idx] = prefixSpace[idx];
		else if(buffer[idx][lastChar[idx]]==';') {
			if(prefixSpace[idx]!=-1) {
				if(buffer[idx][prefixSpace[idx]+1]=='<') {
					startByte[idx] = prefixSpace[idx]+1;
				} else {
					startByte[idx] = 0;
				}
			} else {
				if(buffer[idx][startByte[idx]]!='<')
					startByte[idx] = 0;
			}
		}
		if(checkIgnoredLine(prefixSpace[idx]==-1?startByte[idx]:prefixSpace[idx]+1, endByte[idx], buffer[idx], "MSC OBSERVATION REPORT") && firstCheck) {
			firstCheck = false;
			int checkIdx = idx - 1;
			int lastIdx = idx;
			if(checkIdx<0)
				checkIdx = buffer.length - 1;
			if(checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\r\n") || checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\n")) {
				idx = checkIdx;
				checkIdx--;
				if(checkIdx<0)
					checkIdx = buffer.length - 1;
				if(checkIgnoredLine(prefixSpace[checkIdx]==-1?startByte[checkIdx]:prefixSpace[checkIdx]+1, endByte[checkIdx], buffer[checkIdx], "MSCi")) {
					idx = checkIdx;
					checkIdx--;
					if(checkIdx<0)
						checkIdx = buffer.length - 1;
					if(checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\r\n") || checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\n")) {
						idx = checkIdx;
						checkIdx--;
						if(checkIdx<0)
							checkIdx = buffer.length - 1;
						if(checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\r\n") || checkIgnoredLine(startByte[checkIdx], endByte[checkIdx], buffer[checkIdx], "\n")) {
							idx = checkIdx;
						}
					}
				}
			}
			boolean loopToken = true;
			do {
				readToBuffer(idx);
				loopToken = c!=-1 && !checkIgnoredLine(prefixSpace[idx]==-1?startByte[idx]:prefixSpace[idx]+1, endByte[idx], buffer[idx], "END OF REPORT");
				if(buffer[idx][lastChar[idx]]==';')
					break;
				if(!loopToken) {
					readToBuffer(idx);
					if(checkIgnoredLine(startByte[idx], endByte[idx], buffer[idx], "\r\n") || checkIgnoredLine(startByte[idx], endByte[idx], buffer[idx], "\n")) {
						readToBuffer(idx);
						if(checkIgnoredLine(startByte[idx], endByte[idx], buffer[idx], "\r\n") || checkIgnoredLine(startByte[idx], endByte[idx], buffer[idx], "\n")) {
							readToBuffer(idx);
						}
					}
				}
			} while(loopToken);
			if(idx>lastIdx)
				lastIdx += buffer.length;
			idx++;
			for(int counter=idx; idx<=lastIdx; idx++, counter++) {
				if(counter>=buffer.length)
					counter = 0;
				readToBuffer(counter);
			}
			firstCheck = true;
		}
	}

	private boolean checkIgnoredLine(int start, int end, byte[] buff, String comparator) {
		boolean retVal = true;
		byte[] comp = comparator.getBytes();
		if(end-start<comp.length)
			retVal = false;
		else 
			for(int i=start,j=0; i<end && j<comp.length; i++,j++) {
				if(buff[i]!=comp[j]) {
					retVal = false;
					break;
				}
			}
		return retVal;
	}
}
