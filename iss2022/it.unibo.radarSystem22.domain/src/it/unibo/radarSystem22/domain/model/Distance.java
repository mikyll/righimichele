package it.unibo.radarSystem22.domain.model;

import it.unibo.radarSystem22.domain.interfaces.IDistance;

public class Distance implements IDistance {
    private int v;

    public Distance(int d)
    {
        v = d;
    }

    @Override
    public int getVal() {
        return v;
    }

    public String toString()
    {
        return ""+v;
    }
}
