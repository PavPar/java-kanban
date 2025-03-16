import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    public void shouldHaveInitializedTaskManager(){
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager);
    }

    @Test
    public void shouldHaveInitializedHistoryManager(){
        HistoryManager manager = Managers.getDefaultHistory();
        Assertions.assertNotNull(manager);
    }
}
