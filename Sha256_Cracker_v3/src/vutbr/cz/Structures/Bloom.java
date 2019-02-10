package vutbr.cz.Structures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import vutbr.cz.Hashes.Sha256;
import vutbr.cz.Main.Constants;

public class Bloom implements Constants {

	private int countOfHashes;
	private byte[] firstHash;
	private byte[] lastHash;
	private BitSet bloomFilter;
	FileWriter fw = null;
	BufferedWriter out = null;
	FileWriter writermain;
	File filemain;

	public Bloom(byte[] firstHash, int countOfHashes) {
		this.firstHash = firstHash;
		this.countOfHashes = countOfHashes;
	}

	public Bloom cloneObjectBloom() throws OutOfMemoryError {
		Bloom tmp = new Bloom(this.firstHash.clone(), this.countOfHashes);
		tmp.setLastHash(this.lastHash.clone());
		BitSet clone = (BitSet) bloomFilter.clone(); // muzes pohrat a zkusit
														// clone
		tmp.setBloomFilter(clone);
		return tmp;
	}

	public boolean isHere(byte[] hash, byte[] predchodzi, long iterace, int blok) {
		byte[] tmp = this.firstHash.clone();
		byte[] tmp2 = null;
		if (Arrays.equals(hash, tmp)) {
			System.out.println("\n KOLIZE BYLA NALEZENA S PRVNIM ELEMENTEM !!! DALSI PODLE PORADI -> ");
			return true;
		} else {
			for (int i = 1; i < countOfHashes; i++) {

				tmp = Sha256.makeNewOneAndCutThem(tmp);

				if (Arrays.equals(hash, tmp)) {

					try {
						filemain = new File("RESULT.txt");
						filemain.createNewFile();
						writermain = new FileWriter(filemain);
						out = new BufferedWriter(writermain);
						writermain = new FileWriter(filemain);
						out = new BufferedWriter(writermain);
						out.write(new String("RESULT OF THE HASH CRACKER: "));
						out.newLine();
						out.write("\nKOLIZE BYLA NALEZENA!!!");
						out.newLine();
						out.write("POCET BITU : " + cutHashLength * 8);
						out.newLine();
						out.write("GENERATOR KOLA : ");
						for (int j = 0; j < Generator.length; j++)
							out.write(Generator[j] + " ");
						out.newLine();
						out.write("\nPOCET ITERACI HASH 1: " + (blok * blocksize + i));
						out.newLine();
						out.write("POCET ITERACI HASH 2: " + iterace);
						out.newLine();
						out.write("VELIKOST KRUHU: " + (iterace - (blok * blocksize + i)));
						out.newLine();
						out.write("KOLIZNI HASH :" + Sha256.bytesToHex(hash));
						out.newLine();
						out.write("HASH 1 -> KOLIZNI HASH : " + Sha256.bytesToHex(tmp2) + " -> "
								+ Sha256.bytesToHex(Sha256.makeNewOne(tmp2)));
						out.newLine();
						out.write("HASH 2 -> KOLIZNI HASH : " + Sha256.bytesToHex(predchodzi) + " -> "
								+ Sha256.bytesToHex(Sha256.makeNewOne(predchodzi)));
						out.newLine();
					}

					catch (IOException e) {
						System.out.println("Soubor nelze vytvorit");
						return false;
					} finally {
						try {
							out.close();
							writermain.close();
							System.out.println("KOLIZE BYLA NALEZENA!!!");
							System.out.println("POCET BITU : " + cutHashLength * 8);
							System.out.println("GENERATOR KOLA : ");
							for (int j = 0; j < Generator.length; j++)
								System.out.print(Generator[j] + " ");
							System.out.println("\nPOCET ITERACI HASH 1: " + (blok * blocksize + i));
							System.out.println("POCET ITERACI HASH 2: " + iterace);
							System.out.println("VELIKOST KRUHU: " + (iterace - (blok * blocksize + i)));
							System.out.println("KOLIZNI HASH :" + Sha256.bytesToHex(hash));
							System.out.println("HASH 1 -> KOLIZNI HASH : " + Sha256.bytesToHex(tmp2) + " -> "
									+ Sha256.bytesToHex(Sha256.makeNewOne(tmp2)));
							System.out.println("HASH 2 -> KOLIZNI HASH : " + Sha256.bytesToHex(predchodzi) + " -> "
									+ Sha256.bytesToHex(Sha256.makeNewOne(predchodzi)));
							System.exit(0);
							return true;
						} catch (IOException e) {
							System.out.println("Doslo se k chybe pri zavreni souboru");
						}
					}

				}
				tmp2 = tmp.clone();
			}

		}
		System.out.println("CHYBNA INFORMACE ! OMLOUVAM SE! HASH NENI ! POKRACUJI ! ");
		return false;
	}

	public int getCountOfHashes() {
		return countOfHashes;
	}

	public int getCountOfParts() {
		return bloomFilter.size();
	}

	public byte[] getFirstHash() {
		return firstHash;
	}

	public void setFirstHash(byte[] firstHash) {
		this.firstHash = firstHash;
	}

	public byte[] getLastHash() {
		return lastHash;
	}

	public void setLastHash(byte[] lastHash) {
		this.lastHash = lastHash;
	}

	public void setBloomFilter(BitSet arrayList) {
		this.bloomFilter = arrayList;
	}

	public BitSet getBloomFilter() {
		return bloomFilter;
	}

	public void setCountOfHashes(int countOfHashes) {
		this.countOfHashes = countOfHashes;
	}

}
