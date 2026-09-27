package com.nexusmarket;

/**
 * Compatibility entry point. Delegates to the hexagonal bootstrap.
 */
public final class NexusMarketApp {

    private NexusMarketApp() {
    }

    public static void main(String[] args) {
        com.nexusmarket.bootstrap.NexusMarketApp.main(args);
    }
}
