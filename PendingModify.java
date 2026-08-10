import java.util.concurrent.atomic.AtomicInteger;
// Imported utility to calculate atomic integers

public class PendingModify 
{
    String filePath;
    long lastEventTime;
    AtomicInteger count = new AtomicInteger(1);

    public PendingModify(String filePath)
    {
        this.filePath = filePath;
        this.lastEventTime = System.currentTimeMillis();
    }

    public void update()
    {
        lastEventTime = System.currentTimeMillis();
        count.incrementAndGet();
    }
    // Updating the last time of the event and incrementing the count
}