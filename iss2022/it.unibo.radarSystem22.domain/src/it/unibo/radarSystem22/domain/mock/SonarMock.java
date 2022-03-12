package it.unibo.radarSystem22.domain.mock;

import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonar;

public class SonarMock implements ISonar {


    public SonarMock()
    {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public IDistance getDistance() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
