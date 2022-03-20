import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebFrame extends JFrame {
    private JFrame frame;
    private TableModel model;
    private JTable table;
    private JButton singleFetch;
    private JButton concurrentFetch;
    private JTextField textField;
    private JLabel runningValue;
    private JLabel completedValue;
    private JLabel elapsedValue;
    private JProgressBar progressBar;
    private JButton stop;
    private ArrayList<String> list;
    private String filename = "links.txt";
    private int inRunningState;
    private long startTime;
    private long endTime;
    private Lock lockForStartingWorker;
    private int progressBarChange;
    private Thread launcher;
    private ArrayList<Thread> runningWorkers;
    private int completed;

    public WebFrame(){
        init();
        frame = new JFrame();
        model = new DefaultTableModel(new String[] { "url", "status"},list.size());
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));
        frame.getContentPane().add(scrollpane);

        JPanel bottomPanel = new JPanel();
        frame.getContentPane().add(bottomPanel,BorderLayout.SOUTH);
        bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.Y_AXIS));

        singleFetch = new JButton("Single Thread Fetch");
        concurrentFetch = new JButton("Concurrent Fetch");
        textField = new JTextField();

        runningValue = new JLabel("Running: 0");
        completedValue = new JLabel("Completed: 0");
        elapsedValue = new JLabel("Elapsed: 0");
        progressBar = new JProgressBar();
        stop = new JButton("Stop");
        progressBarChange = progressBar.getMaximum()/list.size();

        bottomPanel.add(Box.createRigidArea(new Dimension(20,0)));
        bottomPanel.add(singleFetch);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        bottomPanel.add(concurrentFetch);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        bottomPanel.add(textField);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        textField.setMaximumSize(new Dimension(50,20));
        bottomPanel.add(runningValue);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        bottomPanel.add(completedValue);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        bottomPanel.add(elapsedValue);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        bottomPanel.add(progressBar);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,10)));
        bottomPanel.add(stop);
        bottomPanel.add(Box.createRigidArea(new Dimension(0,5)));
        stop.setEnabled(false);

        fillList();

        singleFetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeButtonPositionsWhenProcessStarts();
                launcher(1);
            }
        });

        concurrentFetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeButtonPositionsWhenProcessStarts();
                int num = 1;
                if(!textField.getText().equals("")) num =Integer.parseInt(textField.getText());
                launcher(num);
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeButtonPositionsWhenProcessStops();
                if(launcher != null) launcher.interrupt();

            }
        });


        frame.setSize(600,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void changeButtonPositionsWhenProcessStops() {
        stop.setEnabled(false);
        singleFetch.setEnabled(true);
        concurrentFetch.setEnabled(true);
    }

    private void changeButtonPositionsWhenProcessStarts() {
        completed = -1;
        completedValue.setText("Completed: 0");
        endTime = 0;
        startTime = 0;
        ChangeElpasedLabel();
        cleanSecondColumn();
        stop.setEnabled(true);
        singleFetch.setEnabled(false);
        concurrentFetch.setEnabled(false);
    }

    private void cleanSecondColumn() {
        for(int i = 0; i < list.size(); i++) {
            table.setValueAt("",i,1);
        }
    }

    private void fillList() {
        for(int i = 0 ; i < list.size(); i++){
            table.setValueAt(list.get(i),i,0);
        }
    }

    private void init() {
        list = new ArrayList<>();
        runningWorkers = new ArrayList<>();
        lockForStartingWorker = new ReentrantLock();
        inRunningState = 0;
        readFile(filename);
        launcher = null;
    }


    private void launcher(int numberOfWorkers){
        Semaphore semaphore = new Semaphore(numberOfWorkers);
        WebFrame thisWebFrame = this;
        startTime = System.currentTimeMillis();
        int inRunning = 1; //Launch

        launcher = new Thread(new Runnable() {
            @Override
            public void run() {
                newThreadInRunningState(launcher);
                for(int i = 0 ; i < list.size(); i++) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        interruptThreads();
                        break;
                    }
                    if(launcher.isInterrupted()) {
                        interruptThreads();
                        break;
                    }
                    lockForStartingWorker.lock();
                    WebWorker worker = new WebWorker(list.get(i),i,thisWebFrame, semaphore);
                    worker.start();
                    lockForStartingWorker.unlock();
                }
                threadEndRunning(launcher);
            }

        });
        launcher.start();
    }

    private void interruptThreads() {
        for(int i = 0; i < runningWorkers.size(); i++){
            if(!runningWorkers.get(i).isInterrupted()) runningWorkers.get(i).interrupt();
        }
    }
// interrupted lock shi shignit.
// worker startis dros lock
//

    public void readFile(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public synchronized void newThreadInRunningState(Thread thread){
        runningWorkers.add(thread);
        inRunningState++;
        ChangeRunningStateLable();
    }

    private  void ChangeRunningStateLable() {
        runningValue.setText("Running: " + inRunningState);
    }

    public synchronized void threadEndRunning(Thread thread){
        inRunningState--;
        progressBar.setValue(progressBar.getValue() + progressBarChange);
        runningWorkers.remove(thread);
        ChangeRunningStateLable();
        completed++;
        ChangeCompletedLabel();
        if(inRunningState == 0) {
            endTime = System.currentTimeMillis();
            ChangeElpasedLabel();
            changeButtonPositionsWhenProcessStops();
        }
    }

    private void ChangeElpasedLabel() {
        elapsedValue.setText("Elapsed: " + String.valueOf((endTime-startTime)/1000) + " Sec");
    }

    private void ChangeCompletedLabel() {
        completedValue.setText("Completed: " + completed);
    }

    public void alterTable(String str, int row){
        table.setValueAt(str,row,1);
    }

    public static void main(String[] args) {
        WebFrame wf = new WebFrame();
    }


}
