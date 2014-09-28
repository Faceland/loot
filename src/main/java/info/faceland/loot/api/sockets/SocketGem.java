package info.faceland.loot.api.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public interface SocketGem {

    String getName();

    double getWeight();

    String getPrefix();

    String getSuffix();

    List<String> getLore();

    List<SocketEffect> getSocketEffects();

    HiltItemStack toItemStack(int amount);

}
