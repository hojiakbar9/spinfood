package controller.command;

import javafx.collections.ObservableList;
import models.data.PairModel;
import models.data.ParticipantModel;

public class DissolvePairCommand implements Command {
    private final PairModel pair;
    private final ObservableList<PairModel> allPairs;
    private final ObservableList<ParticipantModel> successorParticipants;
    private int index;

    public DissolvePairCommand(PairModel pair, ObservableList<PairModel> allPairs, ObservableList<ParticipantModel> successorParticipants) {
        this.pair = pair;
        this.allPairs = allPairs;
        this.successorParticipants = successorParticipants;
    }

    @Override
    public void execute() {
        index = allPairs.indexOf(pair);
        allPairs.remove(pair);
        successorParticipants.add(pair.getParticipant1());
        successorParticipants.add(pair.getParticipant2());
    }

    @Override
    public void undo() {
        allPairs.add(index, pair);
        successorParticipants.removeIf(p -> p.getName().equals(pair.getParticipant1Name()) || p.getName().equals(pair.getParticipant2Name()));
    }

}

