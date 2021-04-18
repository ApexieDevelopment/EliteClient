package org.apexie.eliteclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class DevBot extends Bot {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevBot.class);

    public DevBot() throws LoginException {
        super();
    }

    public static void main(String[] args) throws LoginException {
        LOGGER.warn("You are running a development build and you may find some issues");
        LOGGER.warn("while using it. If you want to help us with making this stable, report");
        LOGGER.warn("all the issues you have with this build on our Discord server, thank you.");
        new DevBot();
    }
}
