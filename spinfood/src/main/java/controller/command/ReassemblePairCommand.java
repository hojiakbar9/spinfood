package controller.command;

import javafx.collections.ObservableList;
import models.data.PairModel;
import models.data.ParticipantModel;

public class ReassemblePairCommand implements Command {
    private final ParticipantModel participant1;
    private final ParticipantModel participant2;
    private final PairModel pair;
    private final ObservableList<PairModel> allPairs;
    private final ObservableList<ParticipantModel> successorParticipants;

    public ReassemblePairCommand(ParticipantModel participant1, ParticipantModel participant2, ObservableList<PairModel> allPairs, ObservableList<ParticipantModel> successorParticipants) {
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.allPairs = allPairs;
        this.successorParticipants = successorParticipants;
        this.pair = new PairModel(participant1, participant2);
    }

    @Override
    public void execute() {
        allPairs.add(pair);
        successorParticipants.remove(participant1);
        successorParticipants.remove(participant2);
    }

    @Override
    public void undo() {
        allPairs.remove(pair);
        successorParticipants.add(participant1);
        successorParticipants.add(participant2);
    }

}
