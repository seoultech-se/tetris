package tetris.network;

import org.junit.jupiter.api.Test;
import tetris.network.NetworkMessage.MessageType;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMessageTest {

    @Test
    void testNetworkMessageCreation() {
        NetworkMessage message = new NetworkMessage(MessageType.GAME_START, "test data");
        
        assertNotNull(message);
        assertEquals(MessageType.GAME_START, message.getType());
        assertEquals("test data", message.getData());
        assertTrue(message.getTimestamp() > 0);
    }

    @Test
    void testNetworkMessageWithNullData() {
        NetworkMessage message = new NetworkMessage(MessageType.CONNECTION_REQUEST, null);
        
        assertNotNull(message);
        assertEquals(MessageType.CONNECTION_REQUEST, message.getType());
        assertNull(message.getData());
    }

    @Test
    void testNetworkMessageWithIntegerData() {
        NetworkMessage message = new NetworkMessage(MessageType.ATTACK, 5);
        
        assertEquals(MessageType.ATTACK, message.getType());
        assertEquals(5, message.getData());
    }

    @Test
    void testNetworkMessageWithObjectData() {
        GameStateData stateData = new GameStateData(
            new int[20][10], new int[20][10], 100, 1, 5, false,
            new int[4][4], 5, 10, 1,
            new int[4][4], 2, 0, new java.util.ArrayList<>()
        );
        
        NetworkMessage message = new NetworkMessage(MessageType.GAME_STATE, stateData);
        
        assertEquals(MessageType.GAME_STATE, message.getType());
        assertTrue(message.getData() instanceof GameStateData);
    }

    @Test
    void testTimestamp() throws InterruptedException {
        long beforeTime = System.currentTimeMillis();
        Thread.sleep(10);
        NetworkMessage message = new NetworkMessage(MessageType.PING, null);
        Thread.sleep(10);
        long afterTime = System.currentTimeMillis();
        
        assertTrue(message.getTimestamp() >= beforeTime);
        assertTrue(message.getTimestamp() <= afterTime);
    }

    @Test
    void testAllMessageTypes() {
        for (MessageType type : MessageType.values()) {
            NetworkMessage message = new NetworkMessage(type, "test");
            assertEquals(type, message.getType());
        }
    }

    @Test
    void testConnectionRequestMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.CONNECTION_REQUEST, "player1");
        assertEquals(MessageType.CONNECTION_REQUEST, message.getType());
        assertEquals("player1", message.getData());
    }

    @Test
    void testConnectionAcceptedMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.CONNECTION_ACCEPTED, true);
        assertEquals(MessageType.CONNECTION_ACCEPTED, message.getType());
        assertEquals(true, message.getData());
    }

    @Test
    void testGameOverMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.GAME_OVER, "Player 1 wins");
        assertEquals(MessageType.GAME_OVER, message.getType());
        assertEquals("Player 1 wins", message.getData());
    }

    @Test
    void testPingPongMessages() {
        NetworkMessage ping = new NetworkMessage(MessageType.PING, null);
        NetworkMessage pong = new NetworkMessage(MessageType.PONG, null);
        
        assertEquals(MessageType.PING, ping.getType());
        assertEquals(MessageType.PONG, pong.getType());
    }

    @Test
    void testRematchMessages() {
        NetworkMessage request = new NetworkMessage(MessageType.REMATCH_REQUEST, null);
        NetworkMessage response = new NetworkMessage(MessageType.REMATCH_RESPONSE, true);
        
        assertEquals(MessageType.REMATCH_REQUEST, request.getType());
        assertEquals(MessageType.REMATCH_RESPONSE, response.getType());
        assertEquals(true, response.getData());
    }

    @Test
    void testTimeRelatedMessages() {
        NetworkMessage timeSync = new NetworkMessage(MessageType.TIME_SYNC, 180);
        NetworkMessage timeUp = new NetworkMessage(MessageType.TIME_UP, null);
        
        assertEquals(MessageType.TIME_SYNC, timeSync.getType());
        assertEquals(MessageType.TIME_UP, timeUp.getType());
        assertEquals(180, timeSync.getData());
    }

    @Test
    void testDisconnectMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.DISCONNECT, "Connection lost");
        assertEquals(MessageType.DISCONNECT, message.getType());
        assertEquals("Connection lost", message.getData());
    }

    @Test
    void testPlayerActionMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.PLAYER_ACTION, "MOVE_LEFT");
        assertEquals(MessageType.PLAYER_ACTION, message.getType());
        assertEquals("MOVE_LEFT", message.getData());
    }

    @Test
    void testLinesClearedMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.LINES_CLEARED, 4);
        assertEquals(MessageType.LINES_CLEARED, message.getType());
        assertEquals(4, message.getData());
    }

    @Test
    void testPauseMessage() {
        NetworkMessage message = new NetworkMessage(MessageType.PAUSE, true);
        assertEquals(MessageType.PAUSE, message.getType());
        assertEquals(true, message.getData());
    }
}
