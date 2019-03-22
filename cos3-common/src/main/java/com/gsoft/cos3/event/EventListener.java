/**
 * 
 */
package com.gsoft.cos3.event;

import java.util.Map;

/**
 * 
 * 事件监听器接口
 * @author shencq
 *
 */
public interface EventListener {

	/**
	 * 当事件触发时，执行相应的代码
	 * @param event	事件相关参数
	 */
	void onEvent(String eventName, Map<String, Object> event);
	
}
