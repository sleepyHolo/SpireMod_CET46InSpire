package CET46InSpire.ui;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class UIButton {
    private final float pos_x;
    private final float pos_y;
    private final float hide_y;
    public float current_x;
    public float current_y;
    private float target_y;
    public float y_move_speed = 9.0F;
    public boolean attached = false;
    public boolean isHidden = true;
    public String buttonText = "NOT_SET";
    public Color fontColor;
    public float font_x;
    public float font_y;
    public boolean font_center = false;
    private final float width;
    private final float height;
    public Hitbox hb;
    public BitmapFont defaultFont = FontHelper.buttonLabelFont;

    public UIButton(float pos_x, float pos_y, float hitbox_w, float hitbox_h, Color fontColor, float font_x, float font_y) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.hide_y = 1.1F * Settings.HEIGHT;
        this.current_x = this.pos_x;
        this.current_y = this.target_y = this.hide_y;
        this.fontColor = fontColor;

        this.width = hitbox_w;
        this.height = hitbox_h;
        this.hb = new Hitbox(0, 0, hitbox_w, hitbox_h);
        this.moveHb();
    }

    public UIButton(float pos_x, float pos_y, float hitbox_w, float hitbox_h) {
        this(pos_x, pos_y, hitbox_w, hitbox_h, Color.BLACK.cpy(), 0.5F * hitbox_w, 0.5F * hitbox_h);
    }

    public void update() {
        if (this.isHidden) {
            return;
        }
        this.hb.update();

        // 手柄点击反馈 (直接播放音效，不修改 clickStarted 以保持高亮)
        boolean isControllerSelect = this.hb.hovered && CInputActionSet.select.isJustPressed();
        if (isControllerSelect) {
            CardCrawlGame.sound.play("UI_CLICK_1");
        }

        if (InputHelper.justClickedLeft && this.hb.hovered) {
            this.hb.clickStarted = true;
            CardCrawlGame.sound.play("UI_CLICK_1");
        }
        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (this.hb.clicked || isControllerSelect || ((InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed()) && this.current_y != hide_y)) {
            AbstractDungeon.screenSwap = false;
            InputHelper.pressedEscape = false;
            this.hb.clicked = false;
            this.buttonClicked();
        }

        if (this.attached) {
            return;
        }
        if (this.current_y != this.target_y) {
            this.current_y = MathUtils.lerp(this.current_y, this.target_y, Gdx.graphics.getDeltaTime() * this.y_move_speed);
            if (Math.abs(this.current_y - this.target_y) < Settings.UI_SNAP_THRESHOLD) {
                this.current_y = this.target_y;
            }
            this.moveHb();
        }
    }

    public void attachedUpdate(float attachedY) {
        this.update();
        this.current_y = attachedY;
        this.moveHb();
    }

    public void attachedRelUpdate(float relAttachedY) {
        this.update();
        this.current_y = relAttachedY + this.pos_y;
        this.moveHb();
    }

    private void moveHb() {
        this.hb.move(this.current_x + 0.5F * this.width, this.current_y + 0.5F * this.height);
    }

    public void render(SpriteBatch sb) {
        this.render(sb, defaultFont);
    }

    public void render(SpriteBatch sb, BitmapFont font) {
        if (this.font_center) {
            FontHelper.renderFontCentered(sb, font, this.buttonText,
                    this.current_x + 0.5F * this.width, this.current_y + 0.5F * this.height, this.fontColor);
        } else {
            FontHelper.renderFontLeft(sb, font, this.buttonText,
                    this.current_x + this.font_x, this.current_y + this.font_y, this.fontColor);
        }
        if (Settings.isDebug) {
            this.hb.render(sb);
        }
    }

    public void buttonClicked() {
        hide();
        AbstractDungeon.closeCurrentScreen();
    }

    public boolean hovered() {
        return this.hb.hovered;
    }

    public void hide() {
        if (!this.isHidden) {
            this.hb.hovered = false;
            InputHelper.justClickedLeft = false;
            this.target_y = hide_y;
            this.isHidden = true;
        }
    }

    public void hideInstantly() {
        if (!this.isHidden) {
            this.hb.hovered = false;
            InputHelper.justClickedLeft = false;
            this.target_y = hide_y;
            this.current_y = this.target_y;
            this.isHidden = true;
        }
    }

    public void show(String buttonText) {
        if (this.isHidden) {
            this.current_y = hide_y;
            this.target_y = pos_y;
            this.isHidden = false;
            this.buttonText = buttonText;
        } else {
            this.current_y = hide_y;
            this.buttonText = buttonText;
        }
        this.hb.hovered = false;
    }

    public void showInstantly(String buttonText) {
        this.current_y = pos_y;
        this.target_y = pos_y;
        this.isHidden = false;
        this.buttonText = buttonText;
        this.hb.hovered = false;
    }
}
