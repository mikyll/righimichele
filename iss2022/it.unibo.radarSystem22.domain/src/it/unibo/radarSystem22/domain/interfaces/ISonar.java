package it.unibo.radarSystem22.domain.interfaces;

public interface ISonar {
    public void activate();
    public void deactivate();
    public IDistance getDistance(); // fornisce il valore corrente di distanza misurata
    public boolean isActive();
}