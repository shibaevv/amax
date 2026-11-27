package au.com.totemsoft.amax.tool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import au.com.totemsoft.amax.model.AbstractEvent;

public abstract class AbstractDisruptor<T extends AbstractEvent<?>> extends Disruptor<T> {

    // Executor that will be used to construct new threads for consumers
    private static final Executor executor = Executors.newCachedThreadPool();

    // Specify the size of the ring buffer, must be power of 2.
    private static final int bufferSize = 64 * 1024;

    public AbstractDisruptor(EventFactory<T> eventFactory, ProducerType producerType, WaitStrategy waitStrategy) {
        super(eventFactory, bufferSize, executor, producerType, waitStrategy);
    }

    public AbstractDisruptor(EventFactory<T> eventFactory) {
        super(eventFactory, bufferSize, executor/*, ProducerType.MULTI, new BlockingWaitStrategy()*/);
    }

}
