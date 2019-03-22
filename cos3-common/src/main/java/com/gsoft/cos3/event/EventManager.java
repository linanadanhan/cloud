/**
 * 
 */
package com.gsoft.cos3.event;

import org.apache.commons.collections.map.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author shencq
 *
 */
public class EventManager {

	private static final MultiValueMap listenerMap = new MultiValueMap();

	private static final MultiValueMap patternListenerMap = new MultiValueMap();

	private static Logger log = LoggerFactory.getLogger(EventManager.class);

	/**
	 * 促发指定事件
	 * 
	 * 优先执行采用on方法注册的监听具体事件名称的监听器，然后执行采用mattcher方法注册的采用通配符匹配的监听器
	 * 
	 * @param eventName
	 *            事件名称
	 * @param event
	 *            事件信息
	 */
	public static void fire(String eventName, Map<String, Object> event) {
		eventName = eventName.toLowerCase();
		log.info("触发事件：{}", eventName);
		// 实名监听
		Collection<?> list = listenerMap.getCollection(eventName);
		if (list != null) {
			for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
				EventListener listener = (EventListener) iter.next();
				if (listener instanceof RemovableEventListener) {
					RemovableEventListener rel = RemovableEventListener.class.cast(listener);
					if (rel.isDisabled()) {
						iter.remove();
						continue;
					}
				}
				if (log.isInfoEnabled()) {
					log.info("执行监听器：{}", listener.getClass().getName());
				}
				listener.onEvent(eventName, event);
			}
		}
		// 匹配监听
		Set<?> patterns = patternListenerMap.keySet();
		for (Object pattern : patterns) {
			if (Pattern.matches((String) pattern, eventName)) {
				list = patternListenerMap.getCollection(pattern);
				if (list != null) {
					for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
						EventListener listener = (EventListener) iter.next();
						if (listener instanceof RemovableEventListener) {
							RemovableEventListener rel = RemovableEventListener.class.cast(listener);
							if (rel.isDisabled()) {
								iter.remove();
								continue;
							}
						}
						if (log.isInfoEnabled()) {
							log.info("执行监听器：{}", listener.getClass().getName());
						}
						listener.onEvent(eventName, event);
					}
				}
			}
		}
	}

	/**
	 * 监听指定事件，并执行相应的监听器代码
	 * 
	 * @param eventName
	 *            事件名称
	 * @param listener
	 *            事件监听器
	 */
	public static void on(String eventName, EventListener... listeners) {
		eventName = eventName.toLowerCase();
		for (EventListener listener : listeners) {
			listenerMap.put(eventName, listener);
		}
	}

	/**
	 * 监听匹配事件名的事件，并执行相应的监听器代码
	 * 
	 * @param eventNamePattern
	 *            事件名称匹配字符串，如：*.add.before，user.save*.end等
	 * @param listener
	 *            监听器
	 */
	public static void mattcher(String eventNamePattern, EventListener... listeners) {
		eventNamePattern = eventNamePattern.toLowerCase();
		eventNamePattern = convertPattern(eventNamePattern);
		for (EventListener listener : listeners) {
			patternListenerMap.put(eventNamePattern, listener);
		}
	}

	/**
	 * 将通配符转换成Java的正则表达式
	 * 
	 * @param eventNamePattern
	 * @return
	 */
	private static String convertPattern(String eventNamePattern) {
		eventNamePattern = eventNamePattern.replaceAll("\\*", "\\\\S+");
		return "^" + eventNamePattern + "$";
	}

	/**
	 * 清理掉所有标记为移除的监听器注册信息
	 */
	public static void gc() {
		Iterator<?> iterator = listenerMap.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Collection<?> list = listenerMap.getCollection(key);
			if (list != null) {
				for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
					EventListener listener = (EventListener) iter.next();
					if (listener instanceof RemovableEventListener) {
						RemovableEventListener rel = RemovableEventListener.class.cast(listener);
						if (rel.isDisabled()) {
							iter.remove();
							continue;
						}
					}
				}
			}
			if(list.size() == 0){
				iterator.remove();
			}
		}
		iterator = patternListenerMap.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Collection<?> list = patternListenerMap.getCollection(key);
			if (list != null) {
				for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
					EventListener listener = (EventListener) iter.next();
					if (listener instanceof RemovableEventListener) {
						RemovableEventListener rel = RemovableEventListener.class.cast(listener);
						if (rel.isDisabled()) {
							iter.remove();
							continue;
						}
					}
				}
			}
			if(list.size() == 0){
				iterator.remove();
			}
		}
	}

}
