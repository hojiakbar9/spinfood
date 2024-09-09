package controller.command;

import javafx.collections.ObservableList;
import models.data.PairModel;

public class EditPairCommand implements Command {
    private final PairModel pair;
    private final PairModel oldPairState;
    private final PairModel newPairState;

    public EditPairCommand(PairModel pair, PairModel newPairState, ObservableList<PairModel> allPairs) {
        this.pair = pair;
        this.oldPairState = pair;
        this.newPairState = newPairState;
    }

    @Override
    public void execute() {
        updatePair(pair, newPairState);
    }

    @Override
    public void undo() {
        updatePair(pair, oldPairState);
    }

    private void updatePair(PairModel target, PairModel source) {
        target.setJointRegistration(source.isJointRegistration());
        target.setKitchenLongitude(source.getKitchenLongitude());
        target.setKitchenLatitude(source.getKitchenLatitude());
        target.setMainFoodPreference(source.getMainFoodPreference());
        target.setPairNumber(source.getPairNumber());
        target.setAppetizerGroupNumber(source.getAppetizerGroupNumber());
        target.setMainCourseGroupNumber(source.getMainCourseGroupNumber());
        target.setDessertGroupNumber(source.getDessertGroupNumber());
        target.setKitchenSupplier(source.getKitchenSupplier());
        target.setCookingCourse(source.getCookingCourse());
    }
}
