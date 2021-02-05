package org.supcom.event;
import java.security.Permission;
import java.util.Map;
import java.util.Set;
import org.eclipse.osgi.framework.eventmgr.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;





public class EventAdminImpl implements EventAdmin {
	private final LogTracker log;
	private final EventHandlerTracker handlers;
	private volatile EventManager eventManager;

	 
	@param context BundleContext
	EventAdminImpl(BundleContext context) {
		super();
		log = new LogTracker(context, System.out);
		handlers = new EventHandlerTracker(context, log);
	}

	void start() {
		log.open();
		ThreadGroup eventGroup = new ThreadGroup("Event Admin"); 
		eventGroup.setDaemon(true);
		eventManager = new EventManager(EventAdminMsg.EVENT_ASYNC_THREAD_NAME, eventGroup);
		handlers.open();
	}

	
	void stop() {
		handlers.close();
		eventManager.close();
		eventManager = null;
		log.close();
	}

	@Override
	public void postEvent(Event event) {
		dispatchEvent(event, true);
	}

	@Override
	public void sendEvent(Event event) {
		dispatchEvent(event, false);
	}

	 
	@param event
	@param isAsync 
	 
	private void dispatchEvent(Event event, boolean isAsync) {
		EventManager currentManager = eventManager;
		if (currentManager == null) {
			return;
		}
		if (event == null) {
			log.log(LogService.LOG_ERROR, EventAdminMsg.EVENT_NULL_EVENT);
		}

		String topic = event.getTopic();

		try {
			checkTopicPermissionPublish(topic);
		} catch (SecurityException e) {
			String msg = NLS.bind(EventAdminMsg.EVENT_NO_TOPICPERMISSION_PUBLISH, event.getTopic());
			log.log(LogService.LOG_ERROR, msg);
			throw e;
		}

		Set<EventHandlerWrapper> eventHandlers = handlers.getHandlers(topic);
		if (eventHandlers.isEmpty()) {
			return;
		}

		SecurityManager sm = System.getSecurityManager();
		Permission perm = (sm == null) ? null : new TopicPermission(topic, TopicPermission.SUBSCRIBE);

		Map<EventHandlerWrapper, Permission> listeners = new CopyOnWriteIdentityMap<>();
		for (EventHandlerWrapper wrapper : eventHandlers)
			listeners.put(wrapper, perm);

		ListenerQueue<EventHandlerWrapper, Permission, Event> listenerQueue = new ListenerQueue<>(currentManager);
		listenerQueue.queueListeners(listeners.entrySet(), handlers);
		if (isAsync) {
			listenerQueue.dispatchEventAsynchronous(0, event);
		} else {
			listenerQueue.dispatchEventSynchronous(0, event);
		}
	}
	private void checkTopicPermissionPublish(String topic) throws SecurityException {
		SecurityManager sm = System.getSecurityManager();
		if (sm == null)
			return;
		sm.checkPermission(new TopicPermission(topic, TopicPermission.PUBLISH));
	}

}
