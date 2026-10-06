package de.yolacraft.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class MainScreen extends Screen {
    private static final String MOD_ID = "learn-mcsr";

    private static final Identifier BACKGROUND = new Identifier(MOD_ID, "textures/background.png");
    private static final int BACKGROUND_WIDTH = 2560;
    private static final int BACKGROUND_HEIGHT = 1440;
    private static final int BACKGROUND_DIM = 0xB0000000;

    private static final String[] CATEGORIES = {
            "overworld", "nether", "bastion", "fortress",
            "blind", "stronghold", "end", "misc"
    };
    private static final int CATEGORY_TEXTURE_SIZE = 1024;
    private static final int COLUMNS = 4;

    // TODO: replace with the real links
    private static final String DISCORD_URL = "https://discord.gg/";
    private static final String SUPPORT_URL = "https://ko-fi.com/";

    private static final float TITLE_SCALE = 3.0F;
    private static final int GAP = 6;
    private static final int MARGIN = 10;
    private static final int MAX_TILE_SIZE = 100;
    private static final int SMALL_BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final String version;

    private int gridLeft;
    private int titleY;

    public MainScreen(Screen parent) {
        super(new TranslatableText("learn-mcsr.title"));
        this.parent = parent;
        this.version = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("");
    }

    @Override
    protected void init() {
        int titleHeight = (int) (this.textRenderer.fontHeight * TITLE_SCALE);
        int gridTop = MARGIN + titleHeight + GAP;
        int smallButtonsY = this.height - MARGIN - SMALL_BUTTON_HEIGHT;

        int availableHeight = smallButtonsY - GAP - gridTop;
        int maxByHeight = (availableHeight - GAP) / 2;
        int maxByWidth = (this.width - 2 * MARGIN - (COLUMNS - 1) * GAP) / COLUMNS;
        int tileSize = Math.min(MAX_TILE_SIZE, Math.min(maxByHeight, maxByWidth));

        int gridWidth = COLUMNS * tileSize + (COLUMNS - 1) * GAP;
        this.gridLeft = (this.width - gridWidth) / 2;
        this.titleY = gridTop - GAP - titleHeight;

        for (int i = 0; i < CATEGORIES.length; i++) {
            String category = CATEGORIES[i];
            int x = this.gridLeft + (i % COLUMNS) * (tileSize + GAP);
            int y = gridTop + (i / COLUMNS) * (tileSize + GAP);
            Identifier icon = new Identifier(MOD_ID, "textures/categorys/" + category + ".png");

            this.addButton(new CategoryButton(x, y, tileSize,
                    new TranslatableText("learn-mcsr.category." + category), icon, CATEGORY_TEXTURE_SIZE, CATEGORY_TEXTURE_SIZE,
                    button -> {
                        // TODO: open category screen
                    }));
        }

        int y = gridTop + 2 * tileSize + 2 * GAP;
        this.addSmallButton(0, y, tileSize, "learn-mcsr.menu.back", button -> this.onClose());
        this.addSmallButton(1, y, tileSize, "learn-mcsr.menu.progress", button -> {
            // TODO: open progress screen
        });
        this.addSmallButton(2, y, tileSize, "learn-mcsr.menu.discord", button -> this.openLink(DISCORD_URL));
        this.addSmallButton(3, y, tileSize, "learn-mcsr.menu.support", button -> this.openLink(SUPPORT_URL));
    }

    private void addSmallButton(int column, int y, int width, String key, ButtonWidget.PressAction onPress) {
        int x = this.gridLeft + column * (width + GAP);
        this.addButton(new ButtonWidget(x, y, width, SMALL_BUTTON_HEIGHT, new TranslatableText(key), onPress));
    }

    private void openLink(String url) {
        this.client.openScreen(new ConfirmChatLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(url);
            }
            this.client.openScreen(this);
        }, url, true));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderDimmedBackground(matrices);
        this.renderTitle(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderDimmedBackground(MatrixStack matrices) {
        this.client.getTextureManager().bindTexture(BACKGROUND);
        AbstractTexture texture = this.client.getTextureManager().getTexture(BACKGROUND);
        if (texture != null) {
            texture.setFilter(true, false);
        }

        float scale = Math.max((float) this.width / BACKGROUND_WIDTH, (float) this.height / BACKGROUND_HEIGHT);
        int regionWidth = (int) (this.width / scale);
        int regionHeight = (int) (this.height / scale);
        float u = (BACKGROUND_WIDTH - regionWidth) / 2.0F;
        float v = (BACKGROUND_HEIGHT - regionHeight) / 2.0F;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 0, 0, this.width, this.height, u, v,
                regionWidth, regionHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        fill(matrices, 0, 0, this.width, this.height, BACKGROUND_DIM);
    }

    private void renderTitle(MatrixStack matrices) {
        matrices.push();
        matrices.translate(this.gridLeft, this.titleY, 0.0D);
        matrices.scale(TITLE_SCALE, TITLE_SCALE, 1.0F);
        this.textRenderer.drawWithShadow(matrices, this.title, 0.0F, 0.0F, 0xFFFFFF);
        matrices.pop();

        int versionX = this.gridLeft + (int) (this.textRenderer.getWidth(this.title) * TITLE_SCALE) + 4;
        int versionY = this.titleY + (int) (this.textRenderer.fontHeight * TITLE_SCALE) - this.textRenderer.fontHeight - 2;
        this.textRenderer.drawWithShadow(matrices, "v" + this.version, versionX, versionY, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
