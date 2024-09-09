import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.data.GroupModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Objects;

import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(ApplicationExtension.class)
public class GUICriteriaTest extends ApplicationTest  {

    @Override
    public void start(Stage stage) throws Exception {
        // Laden Sie Ihre Haupt-FXML-Datei
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/pairs.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
    }
    /**
     *  Tests the criteria functionality.
     *  This test simulates the user interaction for setting the pairing and grouping criteria in the "Criteria" tab.
     *  It selects different criteria for the first, second, and third priorities for both pairing and grouping
     *  and verifies that the groups list is updated accordingly.
     * */
    @Test
    void testCriteria(FxRobot robot){
        robot.clickOn("Groups");
        TableView<GroupModel> before = robot.lookup("#allGroupsTable").queryTableView();

        robot.clickOn("Criteria");

        robot.clickOn("#pairingFirstPriority");
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#pairingSecondPriority");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#pairingThirdPriority");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#groupingFirstPriority");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#groupingSecondPriority");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#groupingThirdPriority");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Set");
        waitForFxEvents();
        robot.clickOn("OK");
        //check if the groups list changed
        robot.clickOn("Groups");
        TableView<GroupModel> after = robot.lookup("#allGroupsTable").queryTableView();
        assertThat(before).isNotEqualTo(after);
    }
}
