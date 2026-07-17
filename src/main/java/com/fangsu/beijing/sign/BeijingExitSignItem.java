package com.fangsu.beijing.sign;

import com.fangsu.drawing.sign.SignDrawContext;
import com.fangsu.drawing.sign.SignItem;
import com.fangsu.extraConfig.ConfigEntry;
import com.fangsu.extraConfig.ConfigSpec;
import com.fangsu.extraConfig.StringConfig;
import com.fangsu.mappings.LocalComponent;
import com.fangsu.mappings.ResourceLocation;
import com.fangsu.scripting.G2dTextHelper;
import com.fangsu.scripting.JsFunctions;
import com.fangsu.userScripts.ScriptHolderBase;
import com.fangsu.utils.LocalResourceUtil;
import com.fangsu.utils.ResourceUtil;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeijingExitSignItem extends SignItem {
    private static final String ICON_PATH = "sign/beijing_exit.png";
    public static final String TYPE = "beijing_exit";

    private String no;
    private String name;

    private static Font font;

    public BeijingExitSignItem(JsonObject json) {
        no = json.has("no") && json.get("no").isJsonPrimitive() ? json.get("no").getAsString() : "A";
        name = json.has("name") && json.get("name").isJsonPrimitive() ? json.get("name").getAsString() : "西北口|North-West Exit";
        try {
            font = (Font) JsFunctions.loadResource("font", "fangsu:fonts/source-han-sans-bold.otf");
        } catch (Exception e) {
            font = new Font("Arial", Font.PLAIN, 12);
        }
    }

    @Override
    protected JsonObject saveToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("no", no);
        json.addProperty("name", name);
        return json;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public float getWidth(Graphics2D graphics2D, float u) {
        return u;
    }

    @Override
    public void draw(SignDrawContext ctx) {
        Graphics2D g = ctx.graphics();
        g.setFont(font);
        FontRenderContext frc = g.getFontRenderContext();
        float u = ctx.unit();
        float x = ctx.x();
        float y = ctx.y();
        float gap = u * 0.025f;
        float rectSize = u * 0.65f;
        float sideGap = (u - rectSize) / 2f;
        int size1 = Math.round(rectSize * 0.6f);
        int size2 = Math.round(rectSize * 0.25f);
        int textMaxWidth = Math.round(rectSize * 0.75f);
        var oriTransform = g.getTransform();
        int textBeginX = Math.round(sideGap + rectSize * 0.5f);
        int textBeginY = Math.round(gap + rectSize * 0.8f);
        int nameHeight = Math.round(u - rectSize - gap * 3);
        int nameBeginY = Math.round(gap + rectSize + gap * 2);


        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(u * 0.05f));
        g.drawRoundRect(
                Math.round(x + sideGap), Math.round(y + gap), Math.round(rectSize), Math.round(rectSize), Math.round(gap), Math.round(gap)
        );

        boolean isFormatted = isLetterFollowedByDigits(no);
        var split = splitByFirstDigit(no);
        String str1 = split[0];
        String str2 = split[1];
        g.setFont(font.deriveFont(Font.PLAIN, size1));
        int str1Width = g.getFontMetrics().stringWidth(str1);
        g.setFont(font.deriveFont(Font.PLAIN, size2));
        int str2Width = g.getFontMetrics().stringWidth(str2);
        double stretchRate = Math.min(1f, textMaxWidth / ((str1Width + str2Width) * 1f));
        var transform = new AffineTransform();
        transform.translate(x, y);
        transform.translate(textBeginX, textBeginY);
        transform.scale(stretchRate, 1);
        g.setTransform(transform);
        g.setFont(font.deriveFont(Font.PLAIN, size1));
        g.drawString(str1, -(str1Width + str2Width) / 2, 0);
        g.setFont(font.deriveFont(Font.PLAIN, size2));
        g.drawString(str2, -(str1Width + str2Width) / 2 + str1Width, 0);
        g.setTransform(oriTransform);

        G2dTextHelper.drawStrDistributedAlign(g, font, Math.round(x + u * 0.5f - rectSize / 2), Math.round(y + nameBeginY - nameHeight), Math.round(rectSize), nameHeight, name.split("\\|"));
    }

    @Override
    public ResourceLocation getIconLocation() {
        return new ResourceLocation("fangsu", ICON_PATH);
    }

    @Override
    public BufferedImage getIcon() throws IOException {
        return LocalResourceUtil.loadImage(getIconLocation());
    }

    @Override
    public List<ConfigEntry<?>> getConfigs() {
        List<ConfigEntry<?>> list = new ArrayList<>();
        list.add(StringConfig.fromLocal(
                LocalComponent.translatable("ui.fangsu.beijing.exit.exit_no"),
                new ConfigSpec("str"),
                () -> this.no,
                (no) -> this.no = no
        ));
        list.add(StringConfig.fromLocal(
                LocalComponent.translatable("ui.fangsu.beijing.exit.exit_name"),
                new ConfigSpec("str"),
                () -> this.name,
                (name) -> this.name = name
        ));
        return list;
    }

    private static boolean isLetterFollowedByDigits(String str) {
        if (str == null) return false;
        return str.matches("^[A-Za-z]\\d+$");
    }

    private static String[] splitByFirstDigit(String str) {
        if (str == null || str.isEmpty()) return new String[]{"", ""};

        int splitIndex = -1;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                splitIndex = i;
                break;
            }
        }

        // 如果没找到数字，说明全是字母
        if (splitIndex == -1 || splitIndex == 0) return new String[]{str, ""};

        return new String[]{
                str.substring(0, splitIndex),   // 字母部分
                str.substring(splitIndex)       // 数字部分
        };
    }
}
