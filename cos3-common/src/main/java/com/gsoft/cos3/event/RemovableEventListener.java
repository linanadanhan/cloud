/**
 * 
 */
package com.gsoft.cos3.event;

/**
 * @author shencq
 *
 */
public interface RemovableEventListener extends EventListener {

	/**
	 * 标记监听器是否失效，失效后将被自动删除监听
	 * @return
	 */
	Boolean isDisabled();
	
}
