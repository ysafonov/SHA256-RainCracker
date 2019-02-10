package vutbr.cz.Main;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import vutbr.cz.Hashes.Sha256;
import vutbr.cz.Structures.Bloom;
import vutbr.cz.Structures.BloomFilter;

public class Main implements Constants {
	public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException, IOException {
		long startTime = System.currentTimeMillis();
		int counter = 0;
		long counterMain = 0;
		byte[] bhash = Generator.clone();
		BloomFilter filter = new BloomFilter(SizeOfBooleanBloomFilter, blocksize);
		Bloom tmp;
		long totalRAM = (long) Runtime.getRuntime().maxMemory();
		System.out.println("Maximum dedicated memory of program in bytes: " + totalRAM);
		long hraniceRAM = (long) (totalRAM * 0.75);
		System.out.println(
				"Program will start deleting hashes when: " + hraniceRAM + " bytes are used out of " + totalRAM);
		System.out.println("Currently using: " + Runtime.getRuntime().totalMemory());

		System.out.print("OK! Generator Bytes: ");
		for (int i = 0; i < bhash.length; i++) {
			System.out.print(bhash[i] + " ");
		}

		System.out.println();
		byte[] predchozi = null;
		bhash = Sha256.makeNewOneAndCutThem(bhash);
		tmp = new Bloom(bhash, blocksize);
		System.out.println("Good luck!");

		for (;;) {

			bhash = Sha256.makeNewOneAndCutThem(bhash);
			counter++;
			if (counter % adequacyOfAnalysis == 0)
				filter.containsLevel(bhash, predchozi, counterMain - counter, tmp.getFirstHash());
			if (filter.containsFast(bhash) && filter.over5(filter, bhash)) {
				System.out.println("Mozna kolize uvnitr bloku!");
				tmp.setLastHash(bhash);
				tmp.setCountOfHashes(counter);
				tmp.isHere(bhash, predchozi, counterMain,filter.getThirdLevel().size());

			}
			filter.add(bhash);
			counterMain++;
			predchozi = bhash.clone();

			if (counter >= blocksize) {

				tmp.setLastHash(bhash);
				System.out
						.println("* A set of the bloom filter " + filter.getThirdLevel().size() + " was created; Size: "
								+ tmp.getCountOfHashes() + "; First Hash: " + Sha256.bytesToHex(tmp.getFirstHash())
								+ " ; Last Hash: " + Sha256.bytesToHex(tmp.getLastHash()) + " ;");
				System.out
						.println("* Program je aktivni : " + (System.currentTimeMillis() - startTime) / 1000 + " [s] ");
				tmp.setBloomFilter(filter.getBitSet());
				System.out.println("Currently using: " + Runtime.getRuntime().totalMemory());
				filter.AddToThisLevel(tmp);
				filter.clear();
				tmp.setFirstHash(Sha256.makeNewOneAndCutThem(bhash));
				counter = 1;
			}

			if (Runtime.getRuntime().totalMemory() >= hraniceRAM && filter.getThirdLevel().size() > 100) {
				System.out.println("\nPreplneni pameti ! Probiha odstaneni kazdeho : " + DeleteEvery_N_Set + " bloku!");
				System.out.println("Poradi odstanenych bloku : ");
				for (int i = 0; i < filter.getThirdLevel().size() - 1; i++) {
					if (i % DeleteEvery_N_Set == 0) {
						filter.getThirdLevel().remove(i);
						System.out.print(i + " ");
					}
				}
				Runtime.getRuntime().gc();
				Thread.sleep(1000);
				System.out.println("Cisteni probehlo uspesne! Pokracuji! Size : " + filter.getThirdLevel().size());
			}

		}
	}
}
