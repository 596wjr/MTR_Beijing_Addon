package com.fangsu.beijing.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

public class TextWrapper {

    /**
     * 先按 \n 分割，再对每段按视觉宽度换行，最终返回完整的行数组
     * （保留空行，即连续 \n 会生成空字符串元素）
     *
     * @param g2d      当前的 Graphics2D 对象（必须在调用前 setFont）
     * @param text     原始文本
     * @param maxWidth 每行最大像素宽度
     * @return 换行后的完整行数组
     */
    public static List<String> wrapTextPreserveNewlines(Graphics2D g2d, String text, Font font, float maxWidth, float height) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        g2d.setFont(font.deriveFont(Font.PLAIN, height));

        // 1. 先用 \n 切分成多段（使用 -1 保留末尾空行和连续空行）
        String[] paragraphs = text.split("\\r?\\n", -1);

        FontRenderContext frc = g2d.getFontRenderContext();
        List<String> result = new ArrayList<>();

        for (String paragraph : paragraphs) {
            // 如果是空段落（即遇到了 \n），直接添加空字符串表示空行
            if (paragraph.isEmpty()) {
                result.add("");
                continue;
            }

            // 2. 对当前段落按视觉宽度再次分割
            AttributedString attributedString = new AttributedString(paragraph);
            attributedString.addAttribute(TextAttribute.FONT, g2d.getFont());

            LineBreakMeasurer measurer = new LineBreakMeasurer(
                    attributedString.getIterator(),
                    frc
            );

            int textLength = paragraph.length();
            while (measurer.getPosition() < textLength) {
                int start = measurer.getPosition();
                measurer.nextLayout(maxWidth);
                int end = measurer.getPosition();
                result.add(paragraph.substring(start, end));
            }
        }

        return result;
    }
}