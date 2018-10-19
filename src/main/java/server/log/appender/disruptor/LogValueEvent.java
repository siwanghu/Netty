package server.log.appender.disruptor;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.lmax.disruptor.EventFactory;


public class LogValueEvent {
	public LogValueEvent() {

	}

	private ILoggingEvent eventObject;

	private AppenderAttachableImpl<ILoggingEvent> parent;

	public ILoggingEvent getEventObject() {
		return eventObject;
	}

	public void setEventObject(ILoggingEvent eventObject) {
		this.eventObject = eventObject;
	}

	public AppenderAttachableImpl<ILoggingEvent> getParent() {
		return parent;
	}

	public void setParent(AppenderAttachableImpl<ILoggingEvent> parent) {
		this.parent = parent;
	}

	public final static EventFactory<LogValueEvent> EVENT_FACTORY = new EventFactory<LogValueEvent>() {
		@Override
		public LogValueEvent newInstance() {
			return new LogValueEvent();
		}
	};

}
