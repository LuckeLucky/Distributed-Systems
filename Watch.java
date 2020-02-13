import java.util.concurrent.ThreadLocalRandom;

public class Watch extends Thread{
    private long run_time;
    private long start_time;
    private NodeState node;


    public Watch(long min,long max, NodeState node) {
        this.run_time = ThreadLocalRandom.current().nextLong(min ,max);
        this.node = node;
    }

    @Override
    public void run() {
       for(this.start_time = 0 ; this.start_time< this.run_time ; this.start_time++){
           try {
               Thread.sleep(1);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
        this.node.call();
    }

    public void reset() {
        this.start_time =0;
    }


}