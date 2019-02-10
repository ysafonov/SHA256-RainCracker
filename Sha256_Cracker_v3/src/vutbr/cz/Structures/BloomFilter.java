package vutbr.cz.Structures;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;

import vutbr.cz.Hashes.Sha256;
import vutbr.cz.Main.Constants;

public class BloomFilter implements Constants {

	private BitSet bitSet;
	private final int hashFunctionCount;
	private final MessageDigest md5Digest;
	private Vector<Bloom> thirdLevel = new Vector<>();
	ArrayList<int[]> polePozic = new ArrayList<>();

	/**
	 * Constructs an empty Bloom filter. The optimal number of hash functions
	 * (k) is estimated from the total size of the Bloom and the number of
	 * expected elements.
	 * 
	 * @param bitSetSize
	 *            defines how many bits should be used in total for the filter.
	 * @param expectedNumberOfElements
	 *            defines the maximum number of elements the filter is expected
	 *            to contain.
	 * @throws NoSuchAlgorithmException
	 */
	public BloomFilter(int bitSetSize, int expectedNumberOfElements) throws NoSuchAlgorithmException {
		bitSet = new BitSet(bitSetSize);
		/*
		 * The natural logarithm is the logarithm to the base e, where e is an
		 * irrational and transcendental constant approximately equal to
		 * 2.718281828.
		 */
		hashFunctionCount = (int) Math.round((bitSetSize / (double) expectedNumberOfElements) * Math.log(2.0));
		md5Digest = java.security.MessageDigest.getInstance("MD5");
	}

	/**
	 * Generates digests based on the contents of an array of bytes and splits
	 * the result into 4-byte int's and store them in an array. The digest
	 * function is called until the required number of int's are produced. For
	 * each call to digest a salt is prepended to the data. The salt is
	 * increased by 1 for each call.
	 *
	 * @param data
	 *            specifies input data.
	 * @param hashes
	 *            number of hashes/int's to produce.
	 * @return array of int-sized hashes
	 */
	private int[] createHashes(byte[] data) {
		int[] result = new int[hashFunctionCount];
		int k = 0;
		byte salt = 0;
		while (k < hashFunctionCount) {
			byte[] digest;
			synchronized (md5Digest) {
				md5Digest.update(salt);
				salt++;
				digest = md5Digest.digest(data);
			}
			for (int i = 0; i < digest.length / 4 && k < hashFunctionCount; i++) {
				int h = 0;
				for (int j = (i * 4); j < (i * 4) + 4; j++) {
					h <<= 8;
					h |= ((int) digest[j]) & 0xFF;
				}
				result[k] = h;
				k++;
			}
		}
		return result;
	}

	private int getBitIndex(int hash) {
		return Math.abs(hash % bitSet.size());
	}

	/**
	 * Adds an object to the Bloom filter. The output from the object's
	 * toString() method is used as input to the hash functions.
	 *
	 * @param element
	 *            is an element to register in the Bloom filter.
	 */

	public void add(byte[] bytes) {
		if (polePozic.isEmpty())
			System.out.println("PRAZDNE POLE");
		for (int hash : polePozic.get(0))
			bitSet.set(getBitIndex(hash), true);
		polePozic.clear();
	}

	public boolean contains(byte[] bytes) {
		for (int hash : createHashes(bytes)) {
			if (!bitSet.get(getBitIndex(hash))) {
				return false;
			}
		}
		return true;
	}

	public boolean containsFast(byte[] bytes) {
		polePozic.add(createHashes(bytes));
		for (int hash : polePozic.get(0)) {
			if (!bitSet.get(getBitIndex(hash))) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(BitSet tmp) {
		for (int i = 0; i < polePozic.size(); i++) {
			int[] bytes = polePozic.get(i);
			for (int hash : bytes) {
				if (!tmp.get(getBitIndex(hash))) {
					return false;
				}
			}
		}
		return true;
	}

	public void clear() {
		bitSet.clear();
	}

	public void containsLevel(byte[] bhash, byte[] predchodzi, long iterace, byte[] prvniPoslednihoBloku) throws NoSuchAlgorithmException{
		if (thirdLevel.size() == 0) {
			return;
		}
		polePozic.clear();
		BitSet tmp = null;
		byte[] tmphash = bhash.clone();
		polePozic.add(createHashes(tmphash));
		for (int i = 0; i < thirdLevel.size(); i++) {
			tmp = thirdLevel.get(i).getBloomFilter();
			if (contains(tmp)) {
				// System.out.println("----> Overeni 5 kolizi v TREE <----");
				if (polePozic.size() == 1) {
					for (int a = 0; a < numberOfTestingHashes; a++) {
						tmphash = Sha256.makeNewOneAndCutThem(tmphash);
						polePozic.add(createHashes(tmphash));
					}
					// System.out.println("******* POLE VYTVORENO ********");
				}

				if (contains(tmp)) {
					polePozic.clear();
					byte[] hash2 = prvniPoslednihoBloku.clone();
					BloomFilter tmpBloomFilter2 = new BloomFilter(SizeOfBooleanBloomFilter, blocksize);
					tmpBloomFilter2.setBitSet(tmp);

					for (int j = 0; j < blocksize; j++) {
						if (tmpBloomFilter2.contains(hash2) && tmpBloomFilter2.over5(tmpBloomFilter2, hash2)) {
							System.out.println("\n Vznikla kolize s blokem : " + i);
							thirdLevel.get(i).isHere(hash2, predchodzi, iterace + j,i);
							break;
						}
						predchodzi = hash2.clone();
						hash2 = Sha256.makeNewOneAndCutThem(hash2);
					}
				}
			}
		}
		polePozic.clear();
	}

	public void AddToThisLevel(Bloom a) {
		if (a != null) {
			try {
				thirdLevel.add(a.cloneObjectBloom());
			} catch (Exception e) {
				System.out.println("Doslo k chybe, program skoncil cinnost!");
			}

		} else {
			System.out.println("Nebylo mozne pridat NULL pointer");
		}
	}

	public BitSet getBitSet() {
		return bitSet;
	}

	public Vector<Bloom> getThirdLevel() {
		return thirdLevel;
	}

	public void setThirdLevel(Vector<Bloom> thirdLevel) {
		this.thirdLevel = thirdLevel;
	}

	public boolean over5(BloomFilter filter, byte[] bhash) {
		byte[] tmphash = null;
		int vo = 0;
		// System.out.println("----> Overeni 5 kolizi v HLAVNIM FILTRU <----");
		tmphash = bhash.clone();
		for (int i = 0; i < numberOfTestingHashes; i++) {
			tmphash = Sha256.makeNewOneAndCutThem(tmphash);
			if (!filter.contains(tmphash)) {
				return false;
			} else {
				vo++;
			}
		}
		if (vo == numberOfTestingHashes)
			return true;
		return false;
	}

	public void setBitSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}

	/*
	 * public boolean over5vBitSet(BitSet filter, byte[] bhash) { byte[] tmphash
	 * = null; int vo = 0;
	 * System.out.println("-->  Overeni kolize v TREE  <--");
	 * 
	 * if (!contains(filter, tmphash)) { return false; } else { vo++; }
	 * 
	 * if (vo == numberOfTestingHashes) return true; return false; }
	 */
}
