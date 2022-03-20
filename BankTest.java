import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {


    @Test
    public void testSimple1(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {};
        try {
            Bank.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "Args: transaction-file [num-workers [limit]]";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }


    //small.txt test
    @Test
    public void testSimple2(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"small.txt", "1"};
        try {
            Bank.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bank bankForTest = new Bank(1);
        String output = "";
        for(int i = 0; i < 20; i++){
            Account acc = new Account(bankForTest,i,1000);
            if(i%2 == 0) {
                acc.setBalance(999);
            } else {
                acc.setBalance(1001);
            }
            output += acc.toString();
            output += "\r\n";
        }

        assertEquals(os.toString(),output);
    }

//Transactions number:
// 0 - 3jer
// 1 - 3jer
// 2 - 3jer
// 3 - 7jer
// 4 - 5jer
// 5 - 17jer


    //bankTest3.txt test. Too much operations on 5th account
    @Test
    public void testSimple3(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"bankTest3.txt", "5"};
        try {
            Bank.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bank bankForTest = new Bank(1);
        String output = "";
        for(int i = 0; i < 6; i++){
            Account acc = new Account(bankForTest,i,1000);
            int to = 3;
            if(i == 3) to = 7;
            if(i == 4) to = 5;
            if(i == 5) to = 17;
            int balance = 1001;
            if(i%2==0) balance = 999;
            for(int j = 0; j < to; j++){
                acc.setBalance(balance);
            }

            output += acc.toString();
            output += "\r\n";
        }

        //rest accs
        for(int i = 6; i < 20; i++){
            Account acc = new Account(bankForTest,i,1000);
            output += acc.toString();
            output += "\r\n";
        }

        assertEquals(os.toString(),output);
    }

    @Test
    public void testSimple4(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"small.txt", "8"};
        try {
            Bank.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bank bankForTest = new Bank(1);
        String output = "";
        for(int i = 0; i < 20; i++){
            Account acc = new Account(bankForTest,i,1000);
            if(i%2 == 0) {
                acc.setBalance(999);
            } else {
                acc.setBalance(1001);
            }
            output += acc.toString();
            output += "\r\n";
        }

        assertEquals(os.toString(),output);
    }

}