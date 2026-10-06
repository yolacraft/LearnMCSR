package de.yolacraft.mixin;

import de.yolacraft.gui.MainScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	@Unique
	private static final Identifier BUTTON_IMAGE = new Identifier("minecraft", "textures/item/compass_20.png");

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void addLearnMCSRButton(CallbackInfo ci) {
		this.addButton(new ButtonWidget(this.width / 2 + 104, this.height / 4 + 48, 20, 20, LiteralText.EMPTY, button -> {
			MinecraftClient.getInstance().openScreen(new MainScreen(TitleScreenMixin.this));
		}) {
			@Override
			public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

				super.renderButton(matrices, mouseX, mouseY, delta);

				MinecraftClient.getInstance().getTextureManager().bindTexture(BUTTON_IMAGE);
				DrawableHelper.drawTexture(matrices, this.x + 2, this.y + 2, 0.0F, 0.0F, 16, 16, 16, 16);
				if (this.isHovered()) {
					this.drawCenteredText(matrices, TitleScreenMixin.this.textRenderer, new TranslatableText("learn-mcsr.open"), this.x + this.width / 2, this.y - 15, 16777215);
				}
			}
		});
	}
}