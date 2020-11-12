package de.peacepunkt.mcpvpminigame.rounds;

import de.peacepunkt.mcpvpminigame.Main;
import sun.tools.jstat.RawOutputFormatter;

import java.util.ArrayList;
import java.util.List;

public class RoundHandler {
    private Round round;
    private Main main;

    public RoundHandler(Main main) {
        round = new Round();
        this.main = main;
    }

    public void startRound() {
        // get all people from lobby that are in teams
        // give round instance it the people and their teams
        // and say  and round.start oder so
    }
}
