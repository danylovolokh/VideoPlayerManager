package com.volokh.danylo.video_player_manager.player_messages;

public interface Message {
    void runMessage();
    void polledFromQueue();
    void messageFinished();
}
