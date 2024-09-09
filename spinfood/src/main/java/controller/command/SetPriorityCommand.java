package controller.command;

import controller.GroupsController;
import controller.PairController;
import javafx.scene.control.Alert;
import models.service.PairsAndGroupsManager;

import java.util.List;

public class SetPriorityCommand implements Command {
    private final PairsAndGroupsManager pairsAndGroupsManager;
    private final List<Integer> priorities;
    private final PairController pairController;
    private final List<Integer> groupPriorities;
    private final GroupsController groupsController;
    public SetPriorityCommand(PairsAndGroupsManager pairsAndGroupsManager, List<Integer> priorities,
                              PairController pairController, List<Integer> groupPriorities, GroupsController groupsController) {
        this.pairsAndGroupsManager = pairsAndGroupsManager;
        this.priorities = priorities;
        this.pairController = pairController;
        this.groupPriorities = groupPriorities;
        this.groupsController = groupsController;
    }

    /**
 * Executes the command to set the priorities for pairing and grouping operations.
 * This method applies the specified priorities to the PairsAndGroupsManager, which then
 * generates pairs and groups based on these priorities. After setting the priorities,
 * it displays an informational alert to the user indicating the successful setting of criteria.
 * Additionally, it triggers updates in both the PairController and GroupsController to reflect
 * the changes made by the priority settings and initializes the list for comparison in PairController.
 */
    public void execute() {
        pairsAndGroupsManager.generateWithPriorities(priorities, groupPriorities);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Criteria Set");
        alert.setHeaderText(null);
        alert.setContentText("Criteria have been successfully set.");
        alert.showAndWait();
        pairController.update();
        groupsController.update();
        pairController.initialiseListForCompare();
    }

    @Override
    public void undo() {
    }
}