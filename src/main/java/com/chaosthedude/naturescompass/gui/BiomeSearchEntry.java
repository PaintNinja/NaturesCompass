package com.chaosthedude.naturescompass.gui;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchEntry extends ObjectSelectionList.Entry<BiomeSearchEntry> {

	private final Minecraft mc;
	private final NaturesCompassScreen parentScreen;
	private final Biome biome;
	private final BiomeSearchList biomesList;
	private long lastClickTime;

	public BiomeSearchEntry(BiomeSearchList biomesList, Biome biome) {
		this.biomesList = biomesList;
		this.biome = biome;
		parentScreen = biomesList.getParentScreen();
		mc = Minecraft.getInstance();
	}

	@Override
	public void render(PoseStack poseStack, int par1, int par2, int par3, int par4, int par5, int par6, int par7, boolean par8, float par9) {
		String precipitationState = I18n.get("string.naturescompass.none");
		if (biome.getPrecipitation() == Precipitation.SNOW) {
			precipitationState = I18n.get("string.naturescompass.snow");
		} else if (biome.getPrecipitation() == Precipitation.RAIN) {
			precipitationState = I18n.get("string.naturescompass.rain");
		}

		String title = parentScreen.getSortingCategory().getLocalizedName();
		Object value = parentScreen.getSortingCategory().getValue(biome);
		if (value == null) {
			title = I18n.get("string.naturescompass.topBlock");
			//value = I18n.get(biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().getBlock().getDescriptionId());
			value = "?";
		}

		mc.font.draw(poseStack, new TextComponent(BiomeUtils.getBiomeNameForDisplay(parentScreen.world, biome)), par3 + 1, par2 + 1, 0xffffff);
		mc.font.draw(poseStack, new TextComponent(title + ": " + value), par3 + 1, par2 + mc.font.lineHeight + 3, 0x808080);
		mc.font.draw(poseStack, new TextComponent(I18n.get("string.naturescompass.precipitation") + ": " + precipitationState), par3 + 1, par2 + mc.font.lineHeight + 14, 0x808080);
		mc.font.draw(poseStack, new TextComponent(I18n.get("string.naturescompass.source") + ": " + BiomeUtils.getBiomeSource(parentScreen.world, biome)), par3 + 1, par2 + mc.font.lineHeight + 25, 0x808080);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			biomesList.selectBiome(this);
			if (Util.getMillis() - lastClickTime < 250L) {
				searchForBiome();
				return true;
			} else {
				lastClickTime = Util.getMillis();
				return false;
			}
		}
		return false;
	}

	public void searchForBiome() {
		mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		parentScreen.searchForBiome(biome);
	}

	public void viewInfo() {
		mc.setScreen(new BiomeInfoScreen(parentScreen, biome));
	}

	@Override
	public Component getNarration() {
		return new TextComponent(BiomeUtils.getBiomeNameForDisplay(parentScreen.world, biome));
	}

}
