package stormpot;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * In this test, we make sure that the shut down process takes precautions
 * against the possibility that it might poll a null from the dead queue.
 * @author Chris Vest &lt;mr.chrisvest@gmail.com&gt;
 */
public abstract class AllocThread_ShutdownNullsPool_TestTemplate<
  SLOT,
  ALLOC_THREAD extends Thread> {
  @Rule public final TestRule failurePrinter = new FailurePrinterTestRule();
  
  protected Config<Poolable> config;

  @Before
  public void setUp() {
    config = new Config<Poolable>();
    config.setAllocator(new CountingAllocator());
    config.setSize(2);
  }

  protected abstract ALLOC_THREAD createAllocThread(
      BlockingQueue<SLOT> live, BlockingQueue<SLOT> dead);
  
  protected abstract SLOT createSlot(BlockingQueue<SLOT> live);

  @Test(timeout = 300) public void
  mustHandleDeadNullsInShutdown() throws InterruptedException {
    BlockingQueue<SLOT> live = createInterruptingBlockingQueue();
    BlockingQueue<SLOT> dead = new LinkedBlockingQueue<SLOT>();
    Thread thread = createAllocThread(live, dead);
    thread.run();
    // must complete before test times out, and not throw NPE
  }

  @Test(timeout = 300) public void
  mustHandleLiveNullsInShutdown() throws InterruptedException {
    BlockingQueue<SLOT> live = createInterruptingBlockingQueue();
    BlockingQueue<SLOT> dead = new LinkedBlockingQueue<SLOT>();
    dead.add(createSlot(live));
    Thread thread = createAllocThread(live, dead);
    thread.run();
    // must complete before test times out, and not throw NPE
  }

  @SuppressWarnings("serial")
  protected LinkedBlockingQueue<SLOT> createInterruptingBlockingQueue() {
    return new LinkedBlockingQueue<SLOT>() {
      public boolean offer(SLOT e) {
        Thread.currentThread().interrupt();
        return super.offer(e);
      }
    };
  }
}
