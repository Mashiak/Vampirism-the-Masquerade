package net.daanlokdrog.vampirismthemasquerade;

public interface IMasqueradeData {
    int getExposure();
    void setExposure(int value);
    void addExposure(int amount);
    void reduceExposure(int amount);

    int getTickCounter();
    void setTickCounter(int value);
    void incrementTickCounter();
}
