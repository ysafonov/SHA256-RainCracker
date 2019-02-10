package vutbr.cz.Main;

public interface Constants {
	
	public static final short cutHashLength = 7;                // velikos Hashu v bytes
	public static final int blocksize =50000000;                // velikost nejmensiho bloku  50000000
	public static final int SizeOfBooleanBloomFilter =286755069   ;  //velikost BitSet (BloomFilter)
	public static final int numberOfTestingHashes =6;          // min 5 - max 7 - velice ovlivni vykon
	public static final int adequacyOfAnalysis = 100000;   //min 1 - max 1000 - cim vyssi tim rychlejsi vysledek
	public static final int DeleteEvery_N_Set = 4;
	public static final byte[] Generator =  new byte[]{(byte)0x16,(byte)0x04,(byte)0x96};     
	
}
