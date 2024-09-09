package controller.command;

import models.data.GroupModel;
import models.data.PairModel;

import java.util.List;

public class DissolveGroupCommand implements Command {
    private final GroupModel group;
    private final List<GroupModel> allGroups;
    private final List<PairModel> successorPairs;
    private int index;

    public DissolveGroupCommand(GroupModel group, List<GroupModel> allGroups, List<PairModel> successorPairs) {
        this.group = group;
        this.allGroups = allGroups;
        this.successorPairs = successorPairs;
    }


    @Override
    public void execute() {
        index = allGroups.indexOf(group);
        allGroups.remove(group);
        successorPairs.addAll(group.getPairs());

    }

    @Override
    public void undo() {
        allGroups.add(index, group);
        successorPairs.removeAll(group.getPairs());
    }
}
