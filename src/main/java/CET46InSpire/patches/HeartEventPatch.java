package CET46InSpire.patches;

import CET46InSpire.events.CallOfCETRoom;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import downfall.events.HeartEvent;
import downfall.vfx.CustomAnimatedNPC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeartEventPatch {
    private static final Logger logger = LogManager.getLogger(HeartEventPatch.class.getName());
    @SpirePatch(clz = HeartEvent.class, method = "buttonEffect", paramtypez = {int.class}, requiredModId = "downfall")
    public static class InstantToCETEvent{
        @SpireInsertPatch(rloc = 67)
        public static SpireReturn<Void> Insert() {
            logger.info("Floor num: {}", AbstractDungeon.floorNum);
            if (AbstractDungeon.floorNum == 0) {
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                logger.info("To call of CET room");
                RoomEventDialog.optionList.clear();
                MapRoomNode node = new MapRoomNode(0, -1);
                node.room = new CallOfCETRoom();
                AbstractDungeon.player.releaseCard();
                AbstractDungeon.overlayMenu.hideCombatPanels();
                AbstractDungeon.previousScreen = null;
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.dungeonMapScreen.closeInstantly();
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.topPanel.unhoverHitboxes();
                AbstractDungeon.fadeIn();
                AbstractDungeon.effectList.clear();
                AbstractDungeon.topLevelEffects.clear();
                AbstractDungeon.topLevelEffectsQueue.clear();
                AbstractDungeon.effectsQueue.clear();
                AbstractDungeon.dungeonMapScreen.dismissable = true;
                AbstractDungeon.nextRoom = node;
                AbstractDungeon.setCurrMapNode(node);
                AbstractDungeon.getCurrRoom().onPlayerEntry();
                AbstractDungeon.rs = (node.room.event instanceof com.megacrit.cardcrawl.events.AbstractImageEvent) ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;
                AbstractDungeon.nextRoom = null;

                return SpireReturn.Return();
            } else {
                return SpireReturn.Continue();
            }
        }

    }

    @SpirePatch(clz = HeartEvent.class, method = "update", paramtypez = {void.class}, requiredModId = "downfall")
    public static class fixUpdate{
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<Void> Insert(HeartEvent __instance, CustomAnimatedNPC ___npc) {
            if (___npc == null) {
                return SpireReturn.Return();
            } else {
                return SpireReturn.Continue();
            }

        }
    }

}
