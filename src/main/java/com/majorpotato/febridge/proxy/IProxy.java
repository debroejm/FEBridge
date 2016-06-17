package com.majorpotato.febridge.proxy;

public interface IProxy {

    // Client-Side
    void registerRenderThings();

    // Server-Side
    void registerCoinLootHandler();
    void registerPermissionHandler();
    void registerServerLoadThings();

}
