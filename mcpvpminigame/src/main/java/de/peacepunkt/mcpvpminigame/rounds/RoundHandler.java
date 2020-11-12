package de.peacepunkt.mcpvpminigame.rounds;

import de.peacepunkt.mcpvpminigame.Main;
import sun.tools.jstat.RawOutputFormatter;

import java.util.ArrayList;
import java.util.List;

public class RoundHandler {
    private List<Round> rounds;
    private Main main;

    public RoundHandler(Main main) {
        rounds = new ArrayList<Round>();
        this.main = main;
    }

    public void startRound() {

    }
}
