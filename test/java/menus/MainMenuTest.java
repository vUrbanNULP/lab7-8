package menus;

import files.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MainMenuTest {
    @InjectMocks
    private final String clientName = "Volodya";
    private final int clientId = 123;
    private FileHandler fileHandlerMock;

    @BeforeEach
    void setUp() {
        fileHandlerMock = mock(FileHandler.class);
    }

    @Test
    void testIdentifyOrCreateClient_ExistingClient() {
        when(fileHandlerMock.loadData(FileHandler.CLIENTS_FILE)).thenReturn(List.of("122 Max","123 Volodya"));
        int result = MainMenu.identifyOrCreateClient(clientName, fileHandlerMock);

        assertEquals(123, result);
    }

    @Test
    void testIdentifyOrCreateClient_NewClient() {
        when(fileHandlerMock.loadData(FileHandler.CLIENTS_FILE)).thenReturn(List.of());
        when(fileHandlerMock.getNextUniqueId(FileHandler.CLIENTS_FILE)).thenReturn(clientId);

        int result = MainMenu.identifyOrCreateClient(clientName, fileHandlerMock);

        assertEquals(clientId, result);
    }
}