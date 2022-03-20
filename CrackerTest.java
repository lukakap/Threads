import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CrackerTest {


    @Test
    public void test1(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "Args: target length [workers]";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }


    @Test
    public void test2(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"molly"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "4181eecbd7a755d19fdf73887c54837cbecf63fd";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }

    @Test
    public void test3(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"4181eecbd7a755d19fdf73887c54837cbecf63fd", "5", "8"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "molly";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }

    @Test
    public void test4(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"886ffd41c568469795a19f52486bdde64f5f5bcc", "5", "13"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "flomo";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }



    @Test
    public void test5(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"886ffd41c568469795a19f52486bdde64f5f5bcc", "5", "40"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "flomo";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }

    // a! 34800e15707fae815d7c90d49de44aca97e2d759
    // xyz 66b27417d37e024c46526c2f6d358a754fc552f3


    @Test
    public void test6(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"a!"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "34800e15707fae815d7c90d49de44aca97e2d759";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }

    @Test
    public void test7(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"66b27417d37e024c46526c2f6d358a754fc552f3","3"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "xyz";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }

    @Test
    public void test8(){
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        String[] args1 = {"66b27417d37e024c46526c2f6d358a754fc552f3","3","5"};
        try {
            Cracker.main(args1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "xyz";
        output += "\r\n";

        assertEquals(os.toString(),output);
    }
}