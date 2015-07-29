package com.expedia.content.migration.cassandra.util;

import com.expedia.cs.poke.client.Poke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that serves as a wrapper to log and poke at the same time.
 */
public final class PokeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokeLogger.class);

    private PokeLogger() {
    }

    /**
     * Logs an error with the given message and exception if any, while also sending a poke to Hipchat and email at the same time.
     * 
     * @param subject the subject of the poke email
     * @param msg the message to log, also the message of the email sent
     * @param ex the exception as part of the error
     */
    public static void error(String subject, String msg, Exception ex) {
        LOGGER.error(msg, ex);
        Poke.build().email(subject).poke(msg, ex);
        Poke.build().hipchat().poke(msg, ex);
    }

    /**
     * Logs an error with the given message while also sending a poke to Hipchat and email at the same time.
     * 
     * @param subject the subject of the poke email
     * @param msg the message to log, also the message of the email sent
     */
    public static void error(String subject, String msg) {
        LOGGER.error(msg);
        Poke.build().email(subject).poke(msg);
        Poke.build().hipchat().poke(msg);
    }

    /**
     * Logs an information with the given message while also sending a poke to Hipchat and email at the same time.
     * 
     * @param subject the subject of the poke email
     * @param msg the message to log, also the message of the email sent
     */
    public static void info(String subject, String msg) {
        LOGGER.info(msg);
        Poke.build().hipchat().poke(msg);
    }
}
