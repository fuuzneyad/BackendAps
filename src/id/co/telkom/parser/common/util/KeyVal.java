package id.co.telkom.parser.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

//import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class KeyVal {
    private static byte[][] params ;
    private static final Set<String> blacklist = new TreeSet<String>();
//    private static StandardPBEStringEncryptor spbe = new StandardPBEStringEncryptor();
    
    static {
        blacklist.add("00000046310631069bd0");
        blacklist.add("00000067543d543d1c40");
    }

    private static byte PKV_GetKeyByte(final int seed, final byte a, final byte b, final byte c) {
        final int a1 = a % 25;
        final int b1 = b % 3;
        if (a1 % 2 == 0) {
            return (byte) (((seed >> a1) & 0x000000FF) ^ ((seed >> b1) | c));
        } else {
            return (byte) (((seed >> a1) & 0x000000FF) ^ ((seed >> b1) & c));
        }
    }

    private static String PKV_GetChecksum(final String s) {
        int left = 0x0056;
        int right = 0x00AF;
        for (byte b : s.getBytes()) {
            right += b;
            if (right > 0x00FF) {
                right -= 0x00FF;
            }
            left += right;
            if (left > 0x00FF) {
                left -= 0x00FF;
            }
        }
        int sum = (left << 8) + right;
        return intToHex(sum, 4);
    }


    private static boolean PKV_CheckKeyChecksum(final String key) {
        final String comp = key.replaceAll("-", "").toLowerCase(Locale.US);
        if (comp.length() != 20) {
            return false; 
        }

        final String checksum = comp.substring(16);
        return checksum.equals(PKV_GetChecksum(comp.substring(0, 16)));
    }
    
    
    public static Status PKV_CheckKey(final String key, final String par) throws KeyException {
    	setPar(par);
        if (!PKV_CheckKeyChecksum(key)) {
        	throw new id.co.telkom.parser.common.util.KeyException("Invalid Key..");
        }

        final String comp = key.replaceAll("-", "").toLowerCase(Locale.UK);

        for (String bl : blacklist) {
            if (comp.startsWith(bl)) {
				throw new id.co.telkom.parser.common.util.KeyException("Blacklisted..");
            }
        }


        final int seed;
        try {
            seed = Integer.valueOf(comp.substring(0, 8), 16);
        } catch (NumberFormatException e) {
        	throw new id.co.telkom.parser.common.util.KeyException("Not Valid String key..");
        }

        final String kb0 = comp.substring(8, 10);
        final byte b0 = PKV_GetKeyByte(seed, params[0][0], params[0][1], params[0][2]);
        if (!kb0.equals(intToHex(b0, 2))) {
        	throw new id.co.telkom.parser.common.util.KeyException("Not Valid String key..");
        }

        final String kb1 = comp.substring(10, 12);
        final byte b1 = PKV_GetKeyByte(seed, params[1][0], params[1][1], params[1][2]);
        if (!kb1.equals(intToHex(b1, 2))) {
        	throw new id.co.telkom.parser.common.util.KeyException("Not Valid String key..");
        }

        final String kb2 = comp.substring(12, 14);
        final byte b2 = PKV_GetKeyByte(seed, params[2][0], params[2][1], params[2][2]);
        if (!kb2.equals(intToHex(b2, 2))) {
        	throw new id.co.telkom.parser.common.util.KeyException("Not Valid String key..");
        }

        final String kb3 = comp.substring(14, 16);
        final byte b3 = PKV_GetKeyByte(seed, params[3][0], params[3][1], params[3][2]);
        if (!kb3.equals(intToHex(b3, 2))) {
        	throw new id.co.telkom.parser.common.util.KeyException("Not Valid String key..");
        }

        return Status.KEY_GOOD;
    }

    protected static String intToHex(final Number n, final int chars) {
        return String.format("%0" + chars + "x", n);
    }

    public enum Status {
        KEY_GOOD, KEY_INVALID, KEY_BLACKLISTED, KEY_PHONY
    }
    
    private static void setPar(String par){
    	par=encrypt(par);
    	params=new byte[4][par.length()];
    	char[]cc = par.toCharArray();
    	for(int i=0; i<=3;i++){
    		if(i%2==0){
    			for(int x=par.length()-1;x>0;x--)
    				params[i][x]=(byte)cc[x];
    		}else{
    			for(int x=0;x<par.length()-1;x++)
    				params[i][x]=(byte)cc[x];
    		}
    	}
    }
    
    private static String encrypt(String source) {
    	String md5 = null;
    	try {
    	    MessageDigest mdEnc = MessageDigest.getInstance("MD5");
    	    mdEnc.update(source.getBytes(), 0, source.length());
    	    md5 = new BigInteger(1, mdEnc.digest()).toString(16); 
    	} catch (Exception ex) {
    	    return null;
    	}
    	return md5;
    	}
}