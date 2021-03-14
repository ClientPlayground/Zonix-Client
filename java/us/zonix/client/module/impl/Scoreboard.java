package us.zonix.client.module.impl;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import us.zonix.client.gui.ModScreen;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;

public final class Scoreboard extends AbstractModule {

	private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);

	public Scoreboard() {
		super("Scoreboard");

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(new BooleanSetting("Show numbers", false));
		this.addSetting(DRAW_BACKGROUND);
	}

	public void render(ScoreObjective objective, int height, int width, FontRenderer fontRenderer) {
		if (this.mc.currentScreen instanceof ModScreen && objective == null) {
			net.minecraft.scoreboard.Scoreboard scoreboard = new net.minecraft.scoreboard.Scoreboard();
			objective = new ScoreObjective(scoreboard, "Zonix", IScoreObjectiveCriteria.field_96641_b);

			objective.setDisplayName(EnumChatFormatting.RED + EnumChatFormatting.BOLD.toString() + "ZONIX");

			scoreboard.func_96529_a(EnumChatFormatting.RESET + EnumChatFormatting.GRAY.toString() +
			                        EnumChatFormatting.STRIKETHROUGH.toString() + "------------------",
					objective).func_96647_c(4);

			scoreboard.func_96529_a(EnumChatFormatting.RED + "Online" + EnumChatFormatting.DARK_GRAY + ": " +
			                        EnumChatFormatting.WHITE + "1337", objective).func_96647_c(3);
			scoreboard.func_96529_a(EnumChatFormatting.RED + "Fighting" + EnumChatFormatting.DARK_GRAY + ": " +
			                        EnumChatFormatting.WHITE + "420", objective).func_96647_c(2);
			scoreboard.func_96529_a(EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString() +
			                        "------------------", objective).func_96647_c(1);
		}

		if (objective == null) {
			return;
		}

		net.minecraft.scoreboard.Scoreboard var5 = objective.getScoreboard();
		Collection var6 = var5.func_96534_i(objective);

		if (var6.size() <= 15) {
			int var7 = fontRenderer.getStringWidth(objective.getDisplayName());

			String var11;

			for (Iterator var8 = var6.iterator(); var8.hasNext();
			     var7 = Math.max(var7, fontRenderer.getStringWidth(var11))) {
				Score var9 = (Score) var8.next();
				ScorePlayerTeam var10 = var5.getPlayersTeam(var9.getPlayerName());
				String prefix = ScorePlayerTeam.formatPlayerName(var10, var9.getPlayerName());
				String suffix = ": " + EnumChatFormatting.RED + var9.getScorePoints();
				if (this.isEnabled() && !this.getBooleanSetting("Show numbers").getValue()) {
					suffix = "";
				}

				var11 = prefix + suffix;
			}

			int var22 = var6.size() * fontRenderer.FONT_HEIGHT;
			this.setHeight(var22 + fontRenderer.FONT_HEIGHT + 2);
			this.setWidth(var7 + 5);

			int var23 = (int) (this.y + var22 + fontRenderer.FONT_HEIGHT + 2);
			int var25 = (int) (this.x + 2);

			int var12 = 0;
			for (Object aVar6 : var6) {
				Score var14 = (Score) aVar6;
				++var12;

				ScorePlayerTeam var15 = var5.getPlayersTeam(var14.getPlayerName());
				String var16 = ScorePlayerTeam.formatPlayerName(var15, var14.getPlayerName());
				String var17 = EnumChatFormatting.RED + "" + var14.getScorePoints();
				if (this.isEnabled() && !this.getBooleanSetting("Show numbers").getValue()) {
					var17 = "";
				}

				int var19 = var23 - var12 * fontRenderer.FONT_HEIGHT;

				if (DRAW_BACKGROUND.getValue()) {
					GuiIngame.drawRect(var25 - 2, var19, var25 + var7 + 2,
							var19 + fontRenderer.FONT_HEIGHT, 1342177280);
				}

				fontRenderer.drawString(var16, var25, var19, 553648127);
				fontRenderer.drawString(var17, var25 + var7 -1 - fontRenderer.getStringWidth(var17), var19,
						553648127);

				if (var12 == var6.size()) {
					String var21 = objective.getDisplayName();

					if (DRAW_BACKGROUND.getValue()) {
						GuiIngame.drawRect(var25 - 2, var19 - fontRenderer.FONT_HEIGHT - 1,
								var25 + var7 + 2, var19 - 1, 1610612736);

						GuiIngame.drawRect(var25 - 2, var19 - 1,
								var25 + var7 + 2, var19, 1342177280);
					}

					fontRenderer.drawString(var21, var25 + var7 / 2 - fontRenderer.getStringWidth(var21) / 2,
							var19 - fontRenderer.FONT_HEIGHT, 553648127);
				}
			}
		}
	}

}
