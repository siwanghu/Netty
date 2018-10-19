package server.log.appender;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import server.log.appender.disruptor.LogDisruptorEventHandle;
import server.log.appender.disruptor.LogValueEvent;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DisruptorLogAppenderBase<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

	AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
	RingBuffer<LogValueEvent> ringBuffer;
	public static final int DEFAULT_QUEUE_SIZE = 1024;
	int queueSize = DEFAULT_QUEUE_SIZE;
	int appenderCount = 0;
	static final int UNDEFINED = -1;
	int discardingThreshold = UNDEFINED;

	protected boolean isDiscardable(E eventObject) {
		return false;
	}

	protected void preprocess(E eventObject) {
	}

	@Override
	public void start() {
		if (appenderCount == 0) {
			addError("No attached appenders found.");
			return;
		}
		if (queueSize < 1) {
			addError("Invalid queue size [" + queueSize + "]");
			return;
		}
		addInfo("环形缓冲区的大小： " + queueSize);
		Executor executor = Executors.newCachedThreadPool();
		Disruptor<LogValueEvent> disruptor = new Disruptor<LogValueEvent>(
				LogValueEvent.EVENT_FACTORY, queueSize, executor,
				ProducerType.MULTI, new SleepingWaitStrategy());
		disruptor.handleEventsWith(new LogDisruptorEventHandle());
		disruptor.start();
		ringBuffer = disruptor.getRingBuffer();
		super.start();
	}

	@Override
	public void stop() {
		if (!isStarted())
			return;
		super.stop();
	}

	@Override
	protected void append(E eventObject) {
		preprocess(eventObject);
		put(eventObject);
	}
	
	protected void put(E eventObject) {
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getDiscardingThreshold() {
		return discardingThreshold;
	}

	public void setDiscardingThreshold(int discardingThreshold) {
		this.discardingThreshold = discardingThreshold;
	}

	public void addAppender(Appender<E> newAppender) {
			appenderCount++;
			addInfo("Attaching appender named [" + newAppender.getName()
					+ "] to DisruptorLogAppender.");
			aai.addAppender(newAppender);
	}

	public Iterator<Appender<E>> iteratorForAppenders() {
		return aai.iteratorForAppenders();
	}

	public Appender<E> getAppender(String name) {
		return aai.getAppender(name);
	}

	public boolean isAttached(Appender<E> eAppender) {
		return aai.isAttached(eAppender);
	}

	public void detachAndStopAllAppenders() {
		aai.detachAndStopAllAppenders();
	}

	public boolean detachAppender(Appender<E> eAppender) {
		return aai.detachAppender(eAppender);
	}

	public boolean detachAppender(String name) {
		return aai.detachAppender(name);
	}
}
