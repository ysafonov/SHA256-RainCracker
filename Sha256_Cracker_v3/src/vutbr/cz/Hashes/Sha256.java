package vutbr.cz.Hashes;

import java.security.MessageDigest;
import java.util.Arrays;

import vutbr.cz.Main.Constants;

public final class Sha256 implements Constants{

	public static MessageDigest md;
	public static byte[] makeNewOneAndCutThem(byte[] base) {
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(base);
			return Arrays.copyOf(md.digest(), cutHashLength);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] makeNewOne(byte[] base) {
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(base);
			return md.digest();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] getSha256HashInBytes(String base) {
		try {

		    md = MessageDigest.getInstance("SHA-256");
			md.update(base.getBytes());
			return md.digest();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte b : bytes)
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

}
