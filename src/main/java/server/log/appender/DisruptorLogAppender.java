package server.log.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import server.log.appender.disruptor.LogProducerTranslator;


public class DisruptorLogAppender extends DisruptorLogAppenderBase<ILoggingEvent> {
	boolean includeCallerData = false;

	protected boolean isDiscardable(ILoggingEvent event) {
		Level level = event.getLevel();
		return level.toInt() <= Level.INFO_INT;
	}

	protected void preprocess(ILoggingEvent eventObject) {
		eventObject.prepareForDeferredProcessing();
		if (includeCallerData)
			eventObject.getCallerData();
	}

	protected void put(ILoggingEvent event) {
		ringBuffer.publishEvent(LogProducerTranslator.TRANSLATOR, event,aai);
	}

	public boolean isIncludeCallerData() {
		return includeCallerData;
	}

	public void setIncludeCallerData(boolean includeCallerData) {
		this.includeCallerData = includeCallerData;
	}

}
