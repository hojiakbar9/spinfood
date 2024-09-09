package controller.command;

import javafx.collections.ObservableList;
import models.data.GroupModel;
import models.data.PairModel;

import java.util.List;


public class ReassembleGroupCommand implements Command {
    private final PairModel pair1;
    private final PairModel pair2;
    private final PairModel pair3;
    private final GroupModel group;
    private final ObservableList<GroupModel> allGroups;
    private final ObservableList<PairModel> successorPairs;

    public ReassembleGroupCommand(PairModel pair1, PairModel pair2, PairModel pair3, ObservableList<GroupModel> allGroups, ObservableList<PairModel> successorPairs) {
        this.pair1 = pair1;
        this.pair2 = pair2;
        this.pair3 = pair3;
        List<PairModel> pairs = List.of(pair1, pair2, pair3);
        this.group = new GroupModel(pairs);
        this.allGroups = allGroups;
        this.successorPairs = successorPairs;
    }

    @Override
    public void execute() {
        allGroups.add(group);
        successorPairs.remove(pair1);
        successorPairs.remove(pair2);
        successorPairs.remove(pair3);

    }

    @Override
    public void undo() {
        allGroups.remove(group);
        successorPairs.add(pair1);
        successorPairs.add(pair2);
        successorPairs.add(pair3);
    }
}
