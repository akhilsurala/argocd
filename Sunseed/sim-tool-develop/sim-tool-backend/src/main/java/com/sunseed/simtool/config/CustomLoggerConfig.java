package com.sunseed.simtool.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

@Configuration
public class CustomLoggerConfig {

	@Bean
    public Logger simulationLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger("com.sunseed.simtool.util.LogUtils");
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        logger.setAdditive(false);

        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(logger.getLoggerContext());
        fileAppender.setFile("logs/simulation.log");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(logger.getLoggerContext());
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern("logs/simulation.%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.start();

        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(logger.getLoggerContext());
        fileEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n");
        fileEncoder.start();

        fileAppender.setEncoder(fileEncoder);
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();
        logger.addAppender(fileAppender);
        
     // Console Appender
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(logger.getLoggerContext());

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(logger.getLoggerContext());
        consoleEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n");
        consoleEncoder.start();

        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        logger.addAppender(consoleAppender);

        return logger;
    }
}
