import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;

public class WebWorker extends Thread {
    private String forDownload;
    private int rowNum;
    private WebFrame webFrame;
    private String downloadResult;
    private Semaphore semaphore;
    private String lineForTable;

    public WebWorker(String url, int rowNum, WebFrame webFrame, Semaphore semaphore){
        forDownload = url;
        this.rowNum = rowNum;
        this.webFrame = webFrame;
        this.semaphore = semaphore;
        downloadResult = "";
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        webFrame.newThreadInRunningState(this);
        download();
        long endTime = System.currentTimeMillis();
        long timing = endTime - startTime;
        String time = getCurrentTime();
        lineForTable = "  " + time + "   " + timing + "   " + downloadResult;
        if(downloadResult.equals("err") || downloadResult.equals("Interrupted")) lineForTable = downloadResult;


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webFrame.alterTable(lineForTable,rowNum);
            }
        });

        semaphore.release();
        webFrame.threadEndRunning(this);
    }

    public static String getCurrentTime() {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


    private void download() {
        InputStream input = null;
        StringBuilder contents = null;
        try {
            URL url = new URL(forDownload);
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            if(isInterrupted()) downloadResult = "Interrupted";

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                if(isInterrupted()) {
                    downloadResult = "Interrupted";
                    break;
                }
                if(downloadResult.equals("Interrupted")) break;
                contents.append(array, 0, len);
                Thread.sleep(100);
            }

            if(!isInterrupted()) downloadResult =String.valueOf(contents.length());
            // Successful download if we get here

        }
        // Otherwise control jumps to a catch...
        catch (MalformedURLException ignored) {
            downloadResult = "err";
        } catch (InterruptedException exception) {
            // YOUR CODE HERE
            downloadResult = "Interrupted";
        } catch (IOException ignored) {
            downloadResult = "err";
        }
        // "finally" clause, to close the input stream
        // in any case
        finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
            }
        }
    }



    /*
  This is the core web/download i/o code...
 	 download() {
		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			
			// Successful download if we get here
			
		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {}
		catch(InterruptedException exception) {
			// YOUR CODE HERE
			// deal with interruption
		}
		catch(IOException ignored) {}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}

*/
	
}
