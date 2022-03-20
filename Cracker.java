// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private ArrayList<String> ansArray;


	public Cracker(){
		ansArray = new ArrayList<>();
	}
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length < 1) {
			System.out.println("Args: target length [workers]");
			return;
		}
		// args: targ len [num]
		String targ = args[0];
		int len = 0;
		int num = 1;
		if (args.length>1)
			len = Integer.parseInt(args[1]);
		if(args.length > 2)
			num = Integer.parseInt(args[2]);
		// a! 34800e15707fae815d7c90d49de44aca97e2d759
		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3



		Cracker cracker = new Cracker();
		CountDownLatch latch = new CountDownLatch(num);
		if (args.length == 1) {
			try {
				cracker.generationMode(targ, latch);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else {
			cracker.crackingMode(targ, len, num, latch);
		}

		latch.await();
		cracker.printAnswers();

	}

	private void printAnswers() {
		for(int i = 0; i < ansArray.size(); i++) System.out.println(ansArray.get(i));
	}

	public void crackingMode(String targ, int len, int num, CountDownLatch latch) {
		Threading(targ,len,num, latch);
	}

	private void Threading(String targ, int len, int num, CountDownLatch latch) {
		int firstCharIndex = 0;
		int numberOfChars = 40;

		int numberOfCharsForOneThread = 40/num;

		int from = firstCharIndex;
		int to = 0;   //not Include

		for(int i = 1; i < num; i++){
			to = from+numberOfCharsForOneThread;
			Worker worker = new Worker(from, to, targ, len, ansArray, latch);
			worker.start();
			from = to;
		}

		//last Thread
		Worker worker = new Worker(from, numberOfChars, targ, len, ansArray, latch);  //last thread
		worker.start();
	}


	public void generationMode(String targ, CountDownLatch latch) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] bytes = targ.getBytes();
		String ans = hexToString(md.digest(bytes));
		ansArray.add(ans);
		latch.countDown();
	}

	class Worker extends Thread {
		private int from;
		private int to;
		private String targ;
		private int len;
		private ArrayList ansArray;
		private CountDownLatch latch;

		public Worker(int from, int to, String targ, int len, ArrayList<String> ansArray, CountDownLatch latch){
			this.from = from;
			this.to = to;
			this.targ = targ;
			this.len = len;
			this.ansArray = ansArray;
			this.latch = latch;
		}

		@Override
		public void run() {
			String str = "";
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			for(int i = from; i < to; i++){
				recursiveSearchHelper(targ,len-1,ansArray, str+CHARS[i], md);
			}

			latch.countDown();
		}

		private void recursiveSearchHelper(String targ, int len, ArrayList ansArray, String str, MessageDigest md) {
			if(len == 0) {
				if(targ.equals(hashCodeOfString(str, md))) ansArray.add(str);
				return;
			}

			for(int i = 0; i < 40; i++) recursiveSearchHelper(targ, len-1, ansArray, str+CHARS[i],md);
		}
	};

	private String hashCodeOfString(String str, MessageDigest md) {
		byte[] bytes = str.getBytes();
		String ans = hexToString(md.digest(bytes));
		return ans;
	}
}
