package CET46InSpire.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.FontHelper;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.screens.QuizScreen;

public class WordButton extends UIButton {
    private static final float IMG_W;
    private static final float IMG_H;
    private static final float HB_W;
    private static final float HB_H;
    private static final float DELTA_X;
    private static final float DELTA_Y;
    private static final Color HOVER_BLEND_COLOR;
    private static final Color DEFAULT_GLOW_COLOR;
    public boolean lockGlowState = false;
    public boolean glowing = false;
    private float glowAlpha = 0.0F;
    private Color glowColor = DEFAULT_GLOW_COLOR;

    public WordButton(float pos_cx, float pos_cy) {
        super(pos_cx - 0.5F * HB_W, pos_cy - 0.5F * HB_H, HB_W, HB_H);
    }

    @Override
    public void update() {
        if (this.isHidden) {
            return;
        }
        this.updateGlow();
        super.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.glowing) {
            sb.setColor(glowColor);
            sb.draw(ImageElements.WORD_BUTTON_OUTLINE, this.current_x - DELTA_X, this.current_y - DELTA_Y, IMG_W, IMG_H);
        }
        sb.setColor(Color.WHITE);
        sb.draw(ImageElements.WORD_BUTTON, this.current_x - DELTA_X, this.current_y - DELTA_Y, IMG_W, IMG_H);
        if (this.hb.hovered && !this.hb.clickStarted && !this.lockGlowState) {
            sb.setBlendFunction(770, 1);
            sb.setColor(HOVER_BLEND_COLOR);
            sb.draw(ImageElements.WORD_BUTTON, this.current_x - DELTA_X, this.current_y - DELTA_Y, IMG_W, IMG_H);
            sb.setBlendFunction(770, 771);
        }
        super.render(sb, FontHelper.charDescFont);
    }

    public void setGlowColor(Color newGlowColor) {
        this.glowAlpha = 0.0F;
        this.glowColor = newGlowColor;
    }

    @Override
    public void buttonClicked() {
        if (!this.lockGlowState) {
            this.glowing = !this.glowing;
        }
    }

    private void updateGlow() {
        if (!glowing) {
            return;
        }
        this.glowAlpha += Gdx.graphics.getDeltaTime() * 3.0F;
        if (this.glowAlpha < 0.0F)
            this.glowAlpha *= -1.0F;
        float tmp = MathUtils.cos(this.glowAlpha);
        if (tmp < 0.0F) {
            this.glowColor.a = -tmp / 2.0F + 0.3F;
        } else {
            this.glowColor.a = tmp / 2.0F + 0.3F;
        }
    }

    public void reset() {
        hideInstantly();
        this.lockGlowState = false;
        this.glowing = false;
        this.glowColor = DEFAULT_GLOW_COLOR;
    }

    static {
        IMG_W = QuizScreen.WORD_BUT_W;
        IMG_H = QuizScreen.WORD_BUT_H;
        HB_W = 0.933F * IMG_W;
        HB_H = 0.55F * IMG_H;
        DELTA_X = 0.5F * (IMG_W - HB_W);
        DELTA_Y = 0.5F * (IMG_H - HB_H);
        HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
        DEFAULT_GLOW_COLOR = Color.SKY.cpy();
    }
}