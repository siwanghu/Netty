package server.log.appender.disruptor;

import com.lmax.disruptor.EventHandler;


public class LogDisruptorEventHandle implements EventHandler<LogValueEvent> {

	public LogDisruptorEventHandle() {

	}

	@Override
	public void onEvent(LogValueEvent event, long sequence, boolean endOfBatch) {
		event.getParent().appendLoopOnAppenders(event.getEventObject());
		
	}

}
