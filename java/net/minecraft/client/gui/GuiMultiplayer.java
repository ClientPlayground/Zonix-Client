package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import javax.swing.JOptionPane;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.util.ProxyData;
import us.zonix.client.util.RenderUtil;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {

    private static final ResourceLocation[] BARS = new ResourceLocation[]{
            new ResourceLocation("icon/bars/bars_1.png"),
            new ResourceLocation("icon/bars/bars_2.png"),
            new ResourceLocation("icon/bars/bars_3.png"),
            new ResourceLocation("icon/bars/bars_4.png"),
            new ResourceLocation("icon/bars/bars_5.png")
    };

    private static final ResourceLocation PLAY_BUTTON = new ResourceLocation("icon/play.png");

    private static final ThreadPoolExecutor serverPingExecutor = new ScheduledThreadPoolExecutor(5,
            new ThreadFactoryBuilder().setNameFormat("Server " + "Pinger #%d").setDaemon(true).build());

    private static final Logger logger = LogManager.getLogger();
    private final OldServerPinger field_146797_f = new OldServerPinger();
    private GuiScreen field_146798_g;
    private ServerSelectionList field_146803_h;
    private ServerList field_146804_i;
    private GuiButton field_146810_r;
    private GuiButton field_146809_s;
    private GuiButton field_146808_t;
    private boolean field_146807_u;
    private boolean field_146806_v;
    private boolean field_146805_w;
    private boolean field_146813_x;
    private String field_146812_y;
    private ServerData field_146811_z;
    private LanServerDetector.LanServerList field_146799_A;
    private LanServerDetector.ThreadLanServerFind field_146800_B;
    private boolean field_146801_C;
    private static final String __OBFID = "CL_00000814";

    public GuiMultiplayer(GuiScreen p_i1040_1_) {
        this.field_146798_g = p_i1040_1_;
    }

    public static boolean multiPlayerOpen;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        multiPlayerOpen = true;

        this.scrollAmount = 0;

        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (!this.field_146801_C) {
            this.field_146801_C = true;
            this.field_146804_i = new ServerList(this.mc);
            this.field_146804_i.loadServerList();

            this.field_146799_A = new LanServerDetector.LanServerList();

            try {
                this.field_146800_B = new LanServerDetector.ThreadLanServerFind(this.field_146799_A);
                this.field_146800_B.start();
            } catch (Exception var2) {
                logger.warn("Unable to start LAN server detection: " + var2.getMessage());
            }

            this.field_146803_h = new ServerSelectionList(this, this.mc, this.width, this.height
                    , 32, this.height - 64, 36);
            this.field_146803_h.func_148195_a(this.field_146804_i);
        } else {
            this.field_146803_h.func_148122_a(this.width, this.height, 32, this.height - 64);
        }

//        new Thread(() -> {
//            try {
//                URL url = new URL("https://www.zonix.us/api/get_proxy_data");
//
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
//                    ProxyData data = new Gson().fromJson(reader, ProxyData.class);
//
//                    for (int i = 0; i < this.field_146804_i.pinnedServers.size(); i++) {
//                        field_146804_i.pinnedServers.get(i).populationInfo = String.valueOf(data.getPlayerCount(i));
//                    }
//
//                    for (ServerData serverData : field_146804_i.servers) {
//                        field_146797_f.init(serverData);
//                        field_146797_f.func_147224_a(serverData, null, null);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();

        //		this.func_146794_g();
    }

    public void func_146794_g() {
        this.buttonList.add(this.field_146810_r = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20,
                I18n.format("selectServer.edit", new Object[0])));
        this.buttonList.add(this.field_146808_t = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20,
                I18n.format("selectServer.delete", new Object[0])));
        this.buttonList.add(this.field_146809_s = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20,
                I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20,
                I18n.format("selectServer.direct", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20,
                I18n.format("selectServer.add", new Object[0])));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20,
                I18n.format("selectServer.refresh", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20,
                I18n.format("gui.cancel", new Object[0])));
        this.func_146790_a(this.field_146803_h.func_148193_k());
    }

    private boolean initialised;
    private boolean updating;

    private long lastUpdateTime;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();

        if (this.field_146799_A.getWasUpdated()) {
            List var1 = this.field_146799_A.getLanServers();
            this.field_146799_A.setWasNotUpdated();
            this.field_146803_h.func_148194_a(var1);
        }

        this.field_146797_f.func_147223_a();

		if (!this.updating && this.lastUpdateTime + 5000L < System.currentTimeMillis()) {
            this.lastUpdateTime = System.currentTimeMillis();
			this.updating = true;

			new Thread(new Runnable() {
				@Override public void run() {
					List<ServerData> allServers = new LinkedList<>();
					allServers.addAll(GuiMultiplayer.this.field_146804_i.pinnedServers);
					allServers.addAll(GuiMultiplayer.this.field_146804_i.servers);

					final List<ServerData> pinged = new ArrayList<>();
					for (final ServerData serverData : allServers) {
						serverPingExecutor.execute(new Runnable() {
							@Override public void run() {
								try {
									if (!initialised) {
										func_146789_i().init(serverData);
									} else {
										func_146789_i().func_147224_a(serverData, null, null);
									}
								} catch (UnknownHostException e) {
									serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
									serverData.pingToServer = -1L;
								} catch (Exception e) {
									serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
									serverData.pingToServer = -1L;
								}
								pinged.add(serverData);
							}
						});
					}

					while (pinged.size() < allServers.size()) {
						try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					GuiMultiplayer.this.initialised = true;
					GuiMultiplayer.this.updating = false;
				}
			}).start();
		}
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed() {
        multiPlayerOpen = false;

        field_146803_h.close();

        Keyboard.enableRepeatEvents(false);

        if (this.field_146800_B != null) {
            this.field_146800_B.interrupt();
            this.field_146800_B = null;
        }

        this.field_146797_f.func_147226_b();
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        //		if (p_146284_1_.enabled) {
        //			GuiListExtended.IGuiListEntry var2 = this.field_146803_h.func_148193_k() < 0 ? null :
        //					this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
        //
        //			if (p_146284_1_.id == 2 && var2 instanceof ServerListEntryNormal) {
        //				String var9 = ((ServerListEntryNormal) var2).func_148296_a().serverName;
        //
        //				if (var9 != null) {
        //					this.field_146807_u = true;
        //					String var4 = I18n.format("selectServer.deleteQuestion", new Object[0]);
        //					String var5 = "\'" + var9 + "\' " + I18n.format("selectServer.deleteWarning", new
        // Object[0]);
        //					String var6 = I18n.format("selectServer.deleteButton", new Object[0]);
        //					String var7 = I18n.format("gui.cancel", new Object[0]);
        //					GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, this.field_146803_h
        // .func_148193_k());
        //					this.mc.displayGuiScreen(var8);
        //				}
        //			} else if (p_146284_1_.id == 1) {
        //				this.func_146796_h();
        //			} else if (p_146284_1_.id == 4) {
        //				this.field_146813_x = true;
        //				this.mc.displayGuiScreen(new GuiScreenServerList(this,
        //						this.field_146811_z = new ServerData(I18n.format("selectServer.defaultName", new
        // Object[0]),
        //								"")));
        //			} else if (p_146284_1_.id == 3) {
        //				this.field_146806_v = true;
        //				this.mc.displayGuiScreen(new GuiScreenAddServer(this,
        //						this.field_146811_z = new ServerData(I18n.format("selectServer.defaultName", new
        // Object[0]),
        //								"")));
        //			} else if (p_146284_1_.id == 7 && var2 instanceof ServerListEntryNormal) {
        //				this.field_146805_w = true;
        //				ServerData var3 = ((ServerListEntryNormal) var2).func_148296_a();
        //				this.field_146811_z = new ServerData(var3.serverName, var3.serverIP);
        //				this.field_146811_z.func_152583_a(var3);
        //				this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.field_146811_z));
        //			} else if (p_146284_1_.id == 0) {
        //				this.mc.displayGuiScreen(this.field_146798_g);
        //			} else if (p_146284_1_.id == 8) {
        //				this.func_146792_q();
        //			}
        //		}
    }

    private void func_146792_q() {
        this.mc.displayGuiScreen(new GuiMultiplayer(this.field_146798_g));
    }

    public void confirmClicked(boolean p_73878_1_, int p_73878_2_) {
        GuiListExtended.IGuiListEntry var3 = this.field_146803_h.func_148193_k() < 0 ? null :
                this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

        if (this.field_146807_u) {
            this.field_146807_u = false;

            //			if (p_73878_1_ && var3 instanceof ServerListEntryNormal) {
            //				this.field_146804_i.removeServerData(this.field_146803_h.func_148193_k());
            //				this.field_146804_i.saveServerList();
            //				this.field_146803_h.func_148192_c(-1);
            //				this.field_146803_h.func_148195_a(this.field_146804_i);
            //			}

            if (this.deleting != null) {
                this.field_146804_i.servers.remove(this.deleting);
                this.field_146804_i.saveServerList();
            }

            this.mc.displayGuiScreen(this);
        } else if (this.field_146813_x) {
            this.field_146813_x = false;

            if (p_73878_1_) {
                this.func_146791_a(this.field_146811_z);
            } else {
                this.mc.displayGuiScreen(this);
            }
        } else if (this.field_146806_v) {
            this.field_146806_v = false;

            if (p_73878_1_) {
                this.field_146804_i.addServerData(this.field_146811_z);
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148192_c(-1);
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.mc.displayGuiScreen(this);
        } else if (this.field_146805_w) {
            this.field_146805_w = false;

            //			if (p_73878_1_ && var3 instanceof ServerListEntryNormal) {
            //				ServerData var4 = ((ServerListEntryNormal) var3).func_148296_a();
            //				var4.serverName = this.field_146811_z.serverName;
            //				var4.serverIP = this.field_146811_z.serverIP;
            //				var4.func_152583_a(this.field_146811_z);
            //				this.field_146804_i.saveServerList();
            //				this.field_146803_h.func_148195_a(this.field_146804_i);
            //			}

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        int var3 = this.field_146803_h.func_148193_k();
        GuiListExtended.IGuiListEntry var4 = var3 < 0 ? null : this.field_146803_h.func_148180_b(var3);

        if (p_73869_2_ == 63) {
            this.func_146792_q();
        } else {
            if (var3 >= 0) {
                if (p_73869_2_ == 200) {
                    if (isShiftKeyDown()) {
                        //						if (var3 > 0 && var4 instanceof ServerListEntryNormal) {
                        //							this.field_146804_i.swapServers(var3, var3 - 1);
                        //							this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                        //							this.field_146803_h.func_148145_f(-this.field_146803_h
                        // .func_148146_j());
                        //							this.field_146803_h.func_148195_a(this.field_146804_i);
                        //						}
                    } else if (var3 > 0) {
                        this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                        this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());

                        if (this.field_146803_h
                                .func_148180_b(this.field_146803_h.func_148193_k()) instanceof
                                ServerListEntryLanScan) {
                            if (this.field_146803_h.func_148193_k() > 0) {
                                this.func_146790_a(this.field_146803_h.getSize() - 1);
                                this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                            } else {
                                this.func_146790_a(-1);
                            }
                        }
                    } else {
                        this.func_146790_a(-1);
                    }
                } else if (p_73869_2_ == 208) {
                    if (isShiftKeyDown()) {
                        if (var3 < this.field_146804_i.countServers() - 1) {
                            this.field_146804_i.swapServers(var3, var3 + 1);
                            this.func_146790_a(var3 + 1);
                            this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            this.field_146803_h.func_148195_a(this.field_146804_i);
                        }
                    } else if (var3 < this.field_146803_h.getSize()) {
                        this.func_146790_a(this.field_146803_h.func_148193_k() + 1);
                        this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());

                        if (this.field_146803_h
                                .func_148180_b(this.field_146803_h.func_148193_k()) instanceof
                                ServerListEntryLanScan) {
                            if (this.field_146803_h.func_148193_k() < this.field_146803_h.getSize() - 1) {
                                this.func_146790_a(this.field_146803_h.getSize() + 1);
                                this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            } else {
                                this.func_146790_a(-1);
                            }
                        }
                    } else {
                        this.func_146790_a(-1);
                    }
                } else if (p_73869_2_ != 28 && p_73869_2_ != 156) {
                    super.keyTyped(p_73869_1_, p_73869_2_);
                } else {
                    this.actionPerformed((GuiButton) this.buttonList.get(2));
                }
            } else {
                super.keyTyped(p_73869_1_, p_73869_2_);
            }
        }
    }

    private String[] split(String[] lines, float width) {
        List<String> list = new ArrayList<>();

        for (String line : lines) {
            StringBuilder builder = new StringBuilder();
            for (String s : line.split(" ")) {
                String temp = builder.toString() + " " + s;
                if (Client.getInstance().getRegularFontRenderer().getStringWidth(temp) >= width) {
                    list.add(builder.toString());
                    builder = new StringBuilder();
                }

                if (builder.length() > 0) {
                    builder.append(" ");
                }
                builder.append(s);
            }
            if (builder.length() > 0) {
                list.add(builder.toString());
            }
        }

        return list.toArray(new String[0]);
    }

    private int scrollAmount;

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) {
            return;
        }

        this.scroll(scroll);
    }

    private void scroll(int scroll) {
        int before = this.scrollAmount;

        this.scrollAmount += scroll;
        if (this.scrollAmount > 0) {
            this.scrollAmount = 0;
        }

        ScaledResolution resolution = new ScaledResolution(this.mc);

        List<ServerData> serverDataList = new LinkedList<>();
        serverDataList.addAll(this.field_146804_i.servers);

        float maxY = resolution.getScaledHeight() - 59.0F;

        float translate = this.scrollAmount / 10.0F;

        float startY = 130.0F + translate;

        boolean move = false;
        for (int i = 0; i < serverDataList.size(); i++) {
            if (startY + 60.0F > maxY) {
                move = true;
                break;
            }

            startY += 60.0F;
        }

        if (!move) {
            this.scrollAmount = before;
        }
    }

    private ServerData hoveringData;
    private ServerData selectedData;

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution resolution = new ScaledResolution(this.mc);

        this.field_146812_y = null;

        RenderUtil.drawRect(0, 0, resolution.getScaledWidth(),
                resolution.getScaledHeight(), new Color(23, 23, 23).getRGB());
        RenderUtil.drawRect(0, 0, resolution.getScaledWidth(),
                30, new Color(12, 12, 12).getRGB());
        RenderUtil.drawRect(0, resolution.getScaledHeight(), resolution.getScaledWidth(),
                resolution.getScaledHeight() - 50.0F, new Color(12, 12, 12).getRGB());

        float pinnedWidth = 70.0F;
        float startX = resolution.getScaledWidth() / 2 - 155.0F;

        for (ServerData serverData : this.field_146804_i.pinnedServers) {
            GL11.glPushMatrix();
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderUtil.drawRect(startX, 45, startX + pinnedWidth, 110, new Color(16, 16, 16).getRGB());

                GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
                RenderUtil.drawSquareTexture(PLAY_BUTTON, 10, startX + 24.5F, 83.75F);

                Color color = serverData.tintColor;
                GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 0.4F);
                RenderUtil.drawSquareTexture(PLAY_BUTTON, 10, startX + 25, 83);
            }
            GL11.glPopMatrix();

            int boxMiddle = (int) (startX + pinnedWidth / 2);

            RenderUtil.drawCenteredString(Client.getInstance().getHugeBoldFontRenderer(),
                    serverData.serverName.replace("Zonix ", ""), boxMiddle, 60, 0xFFFFFFFF);

            String population;

            if (serverData.populationInfo == null) {
                population = "N/A";
            } else {
                population = serverData.populationInfo; //EnumChatFormatting.getTextWithoutFormattingCodes(serverData.populationInfo.split("/")[0]);
            }

            RenderUtil.drawCenteredString(Client.getInstance().getTinyFontRenderer(),
                    population, boxMiddle, 73, 0xFFFFFFFF);

            startX += pinnedWidth + 10.0F;
        }

        List<ServerData> serverDataList = new LinkedList<>();
        serverDataList.addAll(this.field_146804_i.servers);

        float boxWidth = 300.0F;
        float startY = 130.0F;

        float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
        float maxY = resolution.getScaledHeight() - 59.0F;

        RenderUtil.startScissorBox(startY, maxY, 0, resolution.getScaledWidth());

        GL11.glPushMatrix();

        float currentMaxY = startY + (serverDataList.size() * 50.0F) + ((serverDataList.size() - 1) * 10.0F);

        if (currentMaxY > maxY) {
            float translate = this.scrollAmount / 10.0F;
            startY += translate;
        } else {
            this.scrollAmount = 0;
        }

        this.hoveringData = null;

        for (ServerData pinnedServer : serverDataList) {
            FontRenderer fontRenderer = this.mc.fontRenderer;

            if (this.selectedData == pinnedServer) {
                RenderUtil.drawRect(minX, startY, minX + boxWidth, startY + 48.0F, new Color(40, 40, 40).getRGB());
            }

            fontRenderer.drawString(pinnedServer.serverName, (int) minX + 10, (int) startY + 7, 0xFFFFFFFF);

            if (mouseX >= minX && mouseX <= minX + boxWidth && mouseY >= startY && mouseY <= startY + 50.0F) {
                this.hoveringData = pinnedServer;
            }

            int bars;
            if (pinnedServer.pingToServer < 0L) {
                bars = -1;
            } else if (pinnedServer.pingToServer < 100L) {
                bars = 4;
            } else if (pinnedServer.pingToServer < 200L) {
                bars = 3;
            } else if (pinnedServer.pingToServer < 350L) {
                bars = 2;
            } else if (pinnedServer.pingToServer < 500L) {
                bars = 1;
            } else {
                bars = 0;
            }

            if (bars != -1) {
                RenderUtil.drawTexture(this.BARS[bars], minX + boxWidth - 20.0F, startY + 7.0F, 10.5F, 7.5F);
            }

            String players = EnumChatFormatting.getTextWithoutFormattingCodes(pinnedServer.populationInfo);

            fontRenderer.drawString(players, (int) (minX + boxWidth - 23.0F - fontRenderer.getStringWidth(players)),
                    (int) (startY + 7), 0xFFCBCBCB);

            if (pinnedServer.serverMOTD != null) {
                String[] lines = pinnedServer.serverMOTD.split("\n");

                float stringWidth = fontRenderer.getStringWidth(pinnedServer.serverMOTD);
                if (stringWidth + 10.0F > boxWidth - 15.0F) {
                    lines = this.split(lines, boxWidth - 15.0F);
                }

                for (int j = 0; j < lines.length; j++) {
                    if (j > 1) {
                        break;
                    }

                    fontRenderer.drawString(lines[j], (int) (minX + 10), (int) (startY + 20 + (12 * j)), 0xFFCBCBCB);
                }
            } else {
                fontRenderer.drawString("Pinging...", (int) (minX + 10), (int) (startY + 20), 0xFFCBCBCB);
            }

            startY += 50.0F;
        }
        GL11.glPopMatrix();

        RenderUtil.endScissorBox();

        if (this.mousePressY != -1 && this.mousePressY != mouseY) {
            this.scroll((this.mousePressY - mouseY) * -10);
            this.mousePressY = mouseY;
        }


        RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "Multiplayer",
                resolution.getScaledWidth() / 2, 15, 0xFFF7FFFF);

        float buttonHeight = 35.0F;
        float buttonWidth = 100.0F;
        float startMinX = resolution.getScaledWidth() / 2 - (buttonWidth + 12.5F) / 2 * 3;

        minX = startMinX;

        startY = resolution.getScaledHeight() - buttonHeight - 10.0F;

        for (int j = 0; j < 6; j++) {
            int color = new Color(33, 33, 33).getRGB();
            int bColor = new Color(23, 23, 23).getRGB();

            if ((j == 0 || j == 3 || j == 4) && this.selectedData == null) {
                color = new Color(40, 40, 40).getRGB();
                bColor = new Color(23, 23, 23).getRGB();
            }

            RenderUtil.drawBorderedRect(minX, startY, minX + buttonWidth, startY + buttonHeight / 2,
                    1.0F, bColor, color);

            String text;

            switch (j) {
                case 0:
                    text = "Join Server";
                    break;
                case 1:
                    text = "Direct Connect";
                    break;
                case 2:
                    text = "Add Server";
                    break;
                case 3:
                    text = "Edit";
                    break;
                case 4:
                    text = "Delete";
                    break;
                default:
                    text = "Back";
                    break;
            }

            RenderUtil.drawCenteredString(Client.getInstance().getRegularFontRenderer(), text,
                    (int) (minX + buttonWidth / 2), (int) (startY + buttonHeight / 4) + 1, 0xFFFFFFFF, false);

            minX += buttonWidth + 20.0F;

            if (j == 2) {
                startY += buttonHeight / 2 + 4.0F;
                minX = startMinX;
            }
        }

        if (this.field_146812_y != null) {
            this.func_146283_a(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), mouseX, mouseY);
        }
    }

    public void func_146796_h() {
        //		GuiListExtended.IGuiListEntry var1 = this.field_146803_h.func_148193_k() < 0 ? null :
        //				this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
        //
        //		if (var1 instanceof ServerListEntryNormal) {
        //			this.func_146791_a(((ServerListEntryNormal) var1).func_148296_a());
        //		} else if (var1 instanceof ServerListEntryLanDetected) {
        //			LanServerDetector.LanServer var2 = ((ServerListEntryLanDetected) var1).func_148289_a();
        //			this.func_146791_a(new ServerData(var2.getServerMotd(), var2.getServerIpPort(), true));
        //		}
    }

    private void func_146791_a(ServerData p_146791_1_) {
        if (this.field_146798_g instanceof GuiIngameMenu) {
            if (this.mc.theWorld != null) {
                this.mc.theWorld.sendQuittingDisconnectingPacket();
            }
            this.mc.loadWorld(null);
        }

        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, p_146791_1_));
    }

    public void func_146790_a(int p_146790_1_) {
        this.field_146803_h.func_148192_c(p_146790_1_);
        GuiListExtended.IGuiListEntry var2 = p_146790_1_ < 0 ? null : this.field_146803_h.func_148180_b(p_146790_1_);
        this.field_146809_s.enabled = false;
        this.field_146810_r.enabled = false;
        this.field_146808_t.enabled = false;

        if (var2 != null && !(var2 instanceof ServerListEntryLanScan)) {
            this.field_146809_s.enabled = true;

            //			if (var2 instanceof ServerListEntryNormal) {
            //				this.field_146810_r.enabled = true;
            //				this.field_146808_t.enabled = true;
            //			}
        }
    }

    public OldServerPinger func_146789_i() {
        return this.field_146797_f;
    }

    public void func_146793_a(String p_146793_1_) {
        this.field_146812_y = p_146793_1_;
    }

    private ServerData deleting;
    private long lastSelectTime;
    private int mousePressY;

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button > 1) {
            return;
        }

        ServerData selectedData = this.selectedData;
        this.selectedData = null;

        ScaledResolution resolution = new ScaledResolution(this.mc);

        float buttonHeight = 35.0F;
        float buttonWidth = 100.0F;
        float startMinX = resolution.getScaledWidth() / 2 - (buttonWidth + 12.5F) / 2 * 3;
        float minX = startMinX;

        float startY = resolution.getScaledHeight() - buttonHeight - 10.0F;

        for (int j = 0; j < 6; j++) {
            if (mouseX >= minX && mouseX <= minX + buttonWidth &&
                    mouseY >= startY && mouseY <= startY + buttonHeight / 2) {
                switch (j) {
                    case 0:
                        if (selectedData != null) {
                            this.func_146791_a(selectedData);
                        }
                        break;
                    case 1:
                        this.field_146813_x = true;
                        this.field_146811_z = new ServerData("Minecraft Server", "");
                        this.mc.displayGuiScreen(new GuiScreenServerList(this, this.field_146811_z));
                        break;
                    case 2:
                        this.field_146806_v = true;
                        this.field_146811_z = new ServerData("Minecraft Server", "");
                        this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.field_146811_z));
                        break;
                    case 3:
                        if (selectedData != null) {
                            this.field_146805_w = true;

                            this.field_146811_z = new ServerData(selectedData.serverName, selectedData.serverIP);
                            this.field_146811_z.func_152583_a(selectedData);

                            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.field_146811_z));
                        }
                        break;
                    case 4:
                        if (selectedData != null) {
                            String var9 = selectedData.serverName;

                            if (var9 != null) {
                                this.field_146807_u = true;
                                String var4 = "Are you sure you want to remove this server?";
                                String var5 = "\'" + var9 + "\' will be lost forever! (A long time!)";
                                String var6 = "Delete";
                                String var7 = "Cancel";
                                int id = this.field_146803_h.func_148193_k();
                                GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, id);
                                this.mc.displayGuiScreen(var8);
                                this.deleting = selectedData;
                            }
                        }
                        break;
                    case 5:
                        this.mc.displayGuiScreen(this.field_146798_g);
                        break;
                }

                return;
            }

            minX += buttonWidth + 20.0F;

            if (j == 2) {
                startY += buttonHeight / 2 + 4.0F;
                minX = startMinX;
            }
        }

        float pinnedWidth = 70.0F;
        float startX = resolution.getScaledWidth() / 2 - 155.0F;

        for (ServerData serverData : this.field_146804_i.pinnedServers) {
            if (mouseX >= startX + 25.0F && mouseX <= startX + 45.0F &&
                    mouseY >= 83.0F && mouseY <= 103.0F) {
                this.func_146791_a(serverData);
                return;
            }

            startX += pinnedWidth + 10.0F;
        }

        if (this.hoveringData != null) {
            if (selectedData == this.hoveringData) {
                if (this.lastSelectTime + 500L > System.currentTimeMillis()) {
                    this.func_146791_a(selectedData);
                    return;
                }
            }

            this.lastSelectTime = System.currentTimeMillis();
            this.selectedData = this.hoveringData;
        }

        this.mousePressY = mouseY;
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        this.mousePressY = -1;
    }

    public ServerList func_146795_p() {
        return this.field_146804_i;
    }
}
