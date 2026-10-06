package de.yolacraft.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void addRankedButton(CallbackInfo ci) {
		int buttonY = this.height / 4 + 48;
		int buttonX = (this.width / 2) + 104;

		Identifier rankedIcon = new Identifier("learn-mcsr", "textures/gui/TitleButton.png");

		this.addButton(new TexturedButtonWidget(
				buttonX,
				buttonY,
				20,
				20,
				0,
				0,
				20,
				rankedIcon,
				20,
				40,
				(button) -> {
					if (this.client != null) {
						this.client.openScreen(new TitleScreen());
					}
				}
		));
	}
}