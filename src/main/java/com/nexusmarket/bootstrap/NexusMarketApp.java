package com.nexusmarket.bootstrap;

/**
 * Composition-root entry point. Wires the hexagon with in-memory adapters and starts.
 */
public final class NexusMarketApp {

    private NexusMarketApp() {
    }

    public static void main(String[] args) {
        NexusMarketHexagon hexagon = NexusMarketHexagon.withInMemoryAdapters();
        System.out.println("NexusMarket hexagonal application loaded");
        System.out.println("Inbound use cases wired: " + hexagon.api().useCaseCount());
    }
}
