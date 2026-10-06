package de.yolacraft.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Large square tile button with a layered outline, an image filling the whole button and a label at the bottom.
 */
public class CategoryButton extends ButtonWidget {
    // Outer border
    private static final int OUTER = 0xFF000000;
    private static final int OUTER_HOVER = 0xFFFFFFFF;

    // Middle border
    private static final int MID_LIGHT = 0xFFAAAAAA;
    private static final int MID_DARK = 0xFF555555;
    private static final int MID_CORNER = 0xFF6D6D6D;

    // Inner border
    private static final int IN_TOP = 0xFF6F6F6F;
    private static final int IN_BOTTOM = 0xFF6D6D6D;
    private static final int IN_LEFT = 0xFF6E6E6E;
    private static final int IN_RIGHT = 0xFF6C6C6C;

    // Overlays on top of the image
    private static final int DIM = 0x40000000;
    private static final int DIM_HOVER = 0x00000000;
    private static final int LABEL_STRIP = 0x99000000;

    private static final int BORDER = 3;
    private static final int LABEL_STRIP_HEIGHT = 16;

    private final Identifier image;
    private final int imageWidth;
    private final int imageHeight;

    /**
     * @param image       texture that fills the whole button (cropped to fit)
     * @param imageWidth  pixel width of the texture file
     * @param imageHeight pixel height of the texture file
     */
    public CategoryButton(int x, int y, int size, Text message, Identifier image, int imageWidth, int imageHeight, PressAction onPress) {
        super(x, y, size, size, message, onPress);
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int labelY = this.y + this.height - BORDER - (LABEL_STRIP_HEIGHT + client.textRenderer.fontHeight) / 2;

        drawFrame(matrices);
        drawImage(matrices, client);
        drawCenteredText(matrices, client.textRenderer, this.getMessage(), this.x + this.width / 2, labelY, 0xFFFFFF);
    }

    private void drawFrame(MatrixStack matrices) {
        int x = this.x;
        int y = this.y;
        int w = this.width;
        int h = this.height;

        int outer = this.isHovered() ? OUTER_HOVER : OUTER;
        fill(matrices, x, y, x + w, y + 1, outer);
        fill(matrices, x, y + h - 1, x + w, y + h, outer);
        fill(matrices, x, y, x + 1, y + h, outer);
        fill(matrices, x + w - 1, y, x + w, y + h, outer);

        fill(matrices, x + 2, y + 1, x + w - 2, y + 2, MID_LIGHT);
        fill(matrices, x + 1, y + 2, x + 2, y + h - 2, MID_LIGHT);
        fill(matrices, x + w - 2, y + 2, x + w - 1, y + h - 2, MID_DARK);
        fill(matrices, x + 2, y + h - 2, x + w - 2, y + h - 1, MID_DARK);
        fill(matrices, x + 1, y + 1, x + 2, y + 2, MID_CORNER);
        fill(matrices, x + w - 2, y + 1, x + w - 1, y + 2, MID_CORNER);
        fill(matrices, x + 1, y + h - 2, x + 2, y + h - 1, MID_CORNER);
        fill(matrices, x + w - 2, y + h - 2, x + w - 1, y + h - 1, MID_CORNER);

        fill(matrices, x + 2, y + 2, x + w - 2, y + 3, IN_TOP);
        fill(matrices, x + 2, y + h - 3, x + w - 2, y + h - 2, IN_BOTTOM);
        fill(matrices, x + 2, y + 3, x + 3, y + h - 3, IN_LEFT);
        fill(matrices, x + w - 3, y + 3, x + w - 2, y + h - 3, IN_RIGHT);

    }

    private void drawImage(MatrixStack matrices, MinecraftClient client) {
        int left = this.x + BORDER;
        int top = this.y + BORDER;
        int innerWidth = this.width - 2 * BORDER;
        int innerHeight = this.height - 2 * BORDER;

        client.getTextureManager().bindTexture(this.image);
        AbstractTexture texture = client.getTextureManager().getTexture(this.image);
        if (texture != null) {
            texture.setFilter(true, false);
        }

        // Scale like CSS "cover": fill the whole button and crop the overflowing side
        float scale = Math.max((float) innerWidth / this.imageWidth, (float) innerHeight / this.imageHeight);
        int regionWidth = (int) (innerWidth / scale);
        int regionHeight = (int) (innerHeight / scale);
        float u = (this.imageWidth - regionWidth) / 2.0F;
        float v = (this.imageHeight - regionHeight) / 2.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        drawTexture(matrices, left, top, innerWidth, innerHeight, u, v,
                regionWidth, regionHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();

        // Darken the image a bit (less on hover) and add a darker strip behind the label
        fill(matrices, left, top, left + innerWidth, top + innerHeight, this.isHovered() ? DIM_HOVER : DIM);
        int labelTop = this.y + this.height - BORDER - LABEL_STRIP_HEIGHT;
        fill(matrices, left, labelTop, left + innerWidth, top + innerHeight, LABEL_STRIP);
    }
}
