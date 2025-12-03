package tetris.network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        CONNECTION_REQUEST,
        CONNECTION_ACCEPTED,
        GAME_START,
        GAME_STATE,
        GAME_STATE_UPDATE,  // 실시간 게임 상태 업데이트
        PLAYER_INPUT,       // 플레이어 입력 동기화
        ATTACK,             // 공격 블록 전송
        LINES_CLEARED,      // 줄 삭제 정보 전송
        PAUSE,              // 일시정지 동기화
        GAME_OVER,
        PLAYER_ACTION,
        DISCONNECT,
        PING,
        PONG,
        REMATCH_REQUEST,    // 재시합 요청
        REMATCH_RESPONSE,   // 재시합 응답 (수락/거부)
        TIME_SYNC,          // 시간제한 모드 타이머 동기화
        TIME_UP,            // 시간 종료 알림
        LOBBY_READY,        // 로비 진입 완료 알림 (상태 동기화용)
        CHAT
    }
    
    private MessageType type;
    private Object data;
    private long timestamp;
    
    public NetworkMessage(MessageType type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MessageType getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
