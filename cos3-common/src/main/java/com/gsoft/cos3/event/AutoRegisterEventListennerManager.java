/**
 * 
 */
package com.gsoft.cos3.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

/**
 * 
 * 容器启动后，自动获取容器中的所有AutoRegisterEventListener接口的实现对象，根据对象反馈的监听事件数组，将对象注册为对应事件的监听器
 * 
 * @author shencq
 *
 */
@Service
public class AutoRegisterEventListennerManager implements InitializingBean, SmartLifecycle, ApplicationContextAware {

	private boolean running;
	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] names = applicationContext.getBeanNamesForType(AutoRegisterEventListener.class);
		for (String name : names) {
			AutoRegisterEventListener bean = applicationContext.getBean(name, AutoRegisterEventListener.class);
			String[] patterns = bean.getEventNamePatterns();
			if (patterns != null) {
				for (String pattern : patterns) {
					if (pattern.indexOf('*') > -1) {
						EventManager.mattcher(pattern, bean);
					} else {
						EventManager.on(pattern, bean);
					}
				}
			}
		}
	}

	@Override
	public void start() {
		running = true;
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		running = false;
		callback.run();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
