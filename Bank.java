// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
	public static final int ACCOUNTS = 20;	 // number of accounts
	private BlockingQueue<Transaction> queue;
	private ArrayList<Account> accounts;
	private final Transaction nullTrans = new Transaction(-1,0,0);
	private int numWorkers;

	public Bank(int numWorkers){
		this.numWorkers = numWorkers;
	}
	
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public void readFile(String file) {
			try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
				int from = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int to = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;
				
				// Use the from/to/amount

				Transaction newTransaction = new Transaction(from,to,amount);
				queue.put(newTransaction);

				// YOUR CODE HERE
			}

			for (int i = 0; i < numWorkers; i++) queue.put(nullTrans);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) {
		readFile(file);
	}

	private void fillAccounts() {
		accounts = new ArrayList<>();
		for (int i = 0; i < ACCOUNTS; i++) {
			accounts.add(new Account(this, i,1000));
		}
	}


	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) throws InterruptedException {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			return;
		}

		String file = args[0];

		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}

		BlockingQueue<Transaction> queue = new ArrayBlockingQueue(numWorkers);
		Bank bank = new Bank(numWorkers);
		bank.setQueue(queue);
		CountDownLatch latch = new CountDownLatch(numWorkers);
		bank.fillAccounts();
		bank.runWorkersThreads(numWorkers,latch,queue,bank.accounts,bank.nullTrans);
		bank.processFile(file,numWorkers);

		latch.await();

		bank.accountsToString();
	}

	private void setQueue(BlockingQueue<Transaction> queue) {
		this.queue = queue;
	}

	private static void runWorkersThreads(int numWorkers, CountDownLatch latch, BlockingQueue<Transaction> queue, ArrayList<Account> accounts, Transaction nullTrans) {
		Lock lock = new ReentrantLock();

		for(int i = 0; i < numWorkers; i++) {
			Worker worker = new Worker(queue, lock, accounts, nullTrans, latch);
			worker.start();
		}
	}

	private void accountsToString(){
		for(int i = 0; i < ACCOUNTS; i++){
			System.out.println(accounts.get(i));
		}
	}

	public static class Worker extends Thread {
		BlockingQueue<Transaction> queue;
		Lock lock;
		ArrayList<Account> accounts;
		Transaction nullTrans;
		CountDownLatch latch;

		public Worker(BlockingQueue queue, Lock lock, ArrayList<Account> accounts, Transaction nullTrans, CountDownLatch latch){
			this.queue = queue;
			this.lock = lock;
			this.accounts = accounts;
			this.nullTrans = nullTrans;
			this.latch = latch;
		}

		@Override
		public void run() {
			while (true) {
				Transaction newTransaction = null;
				try {
					newTransaction = queue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(newTransaction == nullTrans) break;

				//needs to be atomic
				lock.lock();
				accounts.get(newTransaction.getFrom()).setBalance(accounts.get(newTransaction.getFrom()).getBalance() - newTransaction.getAmount());
				accounts.get(newTransaction.getTo()).setBalance(accounts.get(newTransaction.getTo()).getBalance() + newTransaction.getAmount());
				lock.unlock();
			}

			latch.countDown();
		}
	};
}

