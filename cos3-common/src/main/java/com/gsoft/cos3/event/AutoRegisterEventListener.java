/**
 * 
 */
package com.gsoft.cos3.event;

/**
 * 
 * 自动注册匹配事件的监听器接口
 * 
 * 实现此接口，getEventNamePatterns方法返回的事件名称，系统启动后将自动注册监听
 * 
 * @author shencq
 *
 */
public interface AutoRegisterEventListener extends EventListener {
	
	/**
	 * 获取需要自动注册的事件名称或名称的匹配规则数组
	 * @return
	 */
	public String[] getEventNamePatterns();
	
}
