package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

@DisplayName("NetworkMessage 테스트")
public class NetworkMessageTest {

    @Test
    @DisplayName("NetworkMessage 생성 및 기본 속성 테스트")
    void testNetworkMessageCreation() {
        // Given
        NetworkMessage.MessageType type = NetworkMessage.MessageType.CONNECTION_REQUEST;
        String data = "test data";

        // When
        NetworkMessage message = new NetworkMessage(type, data);

        // Then
        assertNotNull(message);
        assertEquals(type, message.getType());
        assertEquals(data, message.getData());
        assertTrue(message.getTimestamp() > 0);
    }

    @Test
    @DisplayName("다양한 MessageType 테스트")
    void testDifferentMessageTypes() {
        // Test all message types
        NetworkMessage.MessageType[] types = {
            NetworkMessage.MessageType.CONNECTION_REQUEST,
            NetworkMessage.MessageType.CONNECTION_ACCEPTED,
            NetworkMessage.MessageType.GAME_START,
            NetworkMessage.MessageType.GAME_STATE,
            NetworkMessage.MessageType.GAME_STATE_UPDATE,
            NetworkMessage.MessageType.PLAYER_INPUT,
            NetworkMessage.MessageType.ATTACK,
            NetworkMessage.MessageType.LINES_CLEARED,
            NetworkMessage.MessageType.GAME_OVER,
            NetworkMessage.MessageType.PLAYER_ACTION,
            NetworkMessage.MessageType.DISCONNECT
        };

        for (NetworkMessage.MessageType type : types) {
            NetworkMessage message = new NetworkMessage(type, "data");
            assertEquals(type, message.getType());
        }
    }

    @Test
    @DisplayName("null 데이터로 NetworkMessage 생성 테스트")
    void testNetworkMessageWithNullData() {
        // Given
        NetworkMessage.MessageType type = NetworkMessage.MessageType.GAME_START;

        // When
        NetworkMessage message = new NetworkMessage(type, null);

        // Then
        assertNotNull(message);
        assertEquals(type, message.getType());
        assertNull(message.getData());
    }

    @Test
    @DisplayName("NetworkMessage 직렬화 테스트")
    void testNetworkMessageSerialization() throws IOException, ClassNotFoundException {
        // Given
        NetworkMessage.MessageType type = NetworkMessage.MessageType.GAME_STATE;
        String data = "serialization test data";
        NetworkMessage originalMessage = new NetworkMessage(type, data);

        // When - Serialize
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(originalMessage);
        out.flush();

        // When - Deserialize
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        NetworkMessage deserializedMessage = (NetworkMessage) in.readObject();

        // Then
        assertNotNull(deserializedMessage);
        assertEquals(originalMessage.getType(), deserializedMessage.getType());
        assertEquals(originalMessage.getData(), deserializedMessage.getData());
        assertEquals(originalMessage.getTimestamp(), deserializedMessage.getTimestamp());

        // Cleanup
        out.close();
        in.close();
    }

    @Test
    @DisplayName("NetworkMessage 타임스탬프 순서 테스트")
    void testNetworkMessageTimestampOrdering() throws InterruptedException {
        // Given
        NetworkMessage message1 = new NetworkMessage(NetworkMessage.MessageType.GAME_START, "first");

        // Small delay to ensure different timestamps
        Thread.sleep(10);

        // When
        NetworkMessage message2 = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE, "second");

        // Then
        assertTrue(message2.getTimestamp() > message1.getTimestamp(),
                "두 번째 메시지의 타임스탬프가 첫 번째 메시지보다 커야 합니다");
    }

    @Test
    @DisplayName("복잡한 객체 데이터로 NetworkMessage 생성 테스트")
    void testNetworkMessageWithComplexData() {
        // Given
        int[][] boardData = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        NetworkMessage.MessageType type = NetworkMessage.MessageType.GAME_STATE_UPDATE;

        // When
        NetworkMessage message = new NetworkMessage(type, boardData);

        // Then
        assertNotNull(message);
        assertEquals(type, message.getType());
        assertArrayEquals(boardData, (int[][]) message.getData());
    }

    @Test
    @DisplayName("여러 NetworkMessage의 독립성 테스트")
    void testMultipleNetworkMessagesIndependence() {
        // Given & When
        NetworkMessage message1 = new NetworkMessage(
            NetworkMessage.MessageType.CONNECTION_REQUEST,
            "data1"
        );
        NetworkMessage message2 = new NetworkMessage(
            NetworkMessage.MessageType.CONNECTION_ACCEPTED,
            "data2"
        );

        // Then
        assertNotEquals(message1.getType(), message2.getType());
        assertNotEquals(message1.getData(), message2.getData());
    }
}
