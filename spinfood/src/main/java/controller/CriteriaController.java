package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import controller.command.Command;
import controller.command.CriteriaInvoker;
import controller.command.SetPriorityCommand;
import models.service.PairsAndGroupsManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CriteriaController implements Initializable {
    public ChoiceBox<Integer> pairingFirstPriority;
    public ChoiceBox<Integer> pairingSecondPriority;
    public ChoiceBox<Integer> pairingThirdPriority;
    public ChoiceBox<Integer> groupingFirstPriority;
    public ChoiceBox<Integer> groupingSecondPriority;
    public ChoiceBox<Integer> groupingThirdPriority;

    private PairController pairController;
    private GroupsController groupsController;
    private CriteriaInvoker criteriaInvoker;

    public void setPairController(PairController pairController) {
        this.pairController = pairController;
        this.criteriaInvoker = new CriteriaInvoker();
    }
    public void setGroupsController(GroupsController groupsController) {
        this.groupsController = groupsController;
        this.criteriaInvoker = new CriteriaInvoker();
    }


    /**
 * Initializes the controller class.
 * This method is automatically called after the FXML file has been loaded. It initializes the choice boxes
 * for pairing and grouping priorities with predefined criteria values. The criteria values represent
 * different metrics or considerations that can be prioritized by the user for pairing and grouping operations.
 *
 * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
 * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Integer[]  pairingCriteria = new Integer[]{6,7,8};
        Integer[] groupingCriteria = new Integer[]{6,8,9};
        pairingFirstPriority.getItems().addAll( pairingCriteria);
        pairingSecondPriority.getItems().addAll( pairingCriteria);
        pairingThirdPriority.getItems().addAll( pairingCriteria);

        groupingFirstPriority.getItems().addAll( groupingCriteria);
        groupingSecondPriority.getItems().addAll( groupingCriteria);
        groupingThirdPriority.getItems().addAll( groupingCriteria);
    }

    /**
 * Sets the priorities for pairing and grouping operations based on user selections.
 * This method collects the selected priorities from the UI, creates a command to set these priorities,
 * and executes the command using the CriteriaInvoker. It affects how pairs and groups are formed
 * by influencing the sorting and selection process according to the specified priorities.
 *
 * The priorities for pairing are collected from three choice boxes representing the first, second,
 * and third priorities, respectively. Similarly, priorities for grouping are collected from another
 * set of three choice boxes. These priorities are then used to configure the PairsAndGroupsManager,
 * which is responsible for the logic behind pairing and grouping operations.
 *
 * This method demonstrates the use of the Command design pattern by encapsulating a request as an object,
 * thereby allowing for parameterization of clients with queues, requests, and operations.
 */
    @FXML
    public void setPriorities() {
        List<Integer> priorities = new ArrayList<>();
        priorities.add(pairingFirstPriority.getValue());
        priorities.add(pairingSecondPriority.getValue());
        priorities.add(pairingThirdPriority.getValue());

        List<Integer> groupingPriorities = new ArrayList<>();
        groupingPriorities.add(groupingFirstPriority.getValue());
        groupingPriorities.add(groupingSecondPriority.getValue());
        groupingPriorities.add(groupingThirdPriority.getValue());

        Command setPrioritiesCommand = new SetPriorityCommand(
                PairsAndGroupsManager.getInstance(),
                priorities,
                pairController, groupingPriorities,groupsController
        );

        criteriaInvoker.setCommand(setPrioritiesCommand);
        criteriaInvoker.executeCommand();

    }
    }


