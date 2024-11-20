package com.example.intelligentwebscrapping.infrastructure;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static volatile ApplicationContext applicationContext;

    public static ApplicationContextProvider instance() {
        return applicationContext.getBean(ApplicationContextProvider.class);
    }

    public ApplicationContext applicationContext() {
        return ApplicationContextProvider.applicationContext;
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextProvider.applicationContext == null) {
            ApplicationContextProvider.applicationContext = applicationContext;
        }
    }
}
