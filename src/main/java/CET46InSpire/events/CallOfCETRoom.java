package CET46InSpire.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.EventRoom;

public class CallOfCETRoom extends EventRoom {
    public void onPlayerEntry() {
        AbstractDungeon.overlayMenu.proceedButton.hide();
        this.event = new CallOfCETEvent();
        this.event.onEnterRoom();
    }
}