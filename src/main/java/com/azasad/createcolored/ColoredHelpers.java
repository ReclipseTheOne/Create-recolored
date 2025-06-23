package com.azasad.createcolored;

import net.minecraft.world.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

import java.util.IdentityHashMap;
import java.util.Map;

public class ColoredHelpers {
    public static int getColor(DyeColor color)
    {
        return switch (color) {
            case BLACK -> 1908001;
            case BLUE -> 3949738;
            case BROWN -> 8606770;
            case CYAN -> 1481884;
            case GRAY -> 4673362;
            case GREEN -> 6192150;
            case LIGHT_BLUE -> 3847130;
            case LIGHT_GRAY -> 10329495;
            case LIME -> 8439583;
            case MAGENTA -> 13061821;
            case ORANGE -> 16351261;
            case PINK -> 15961002;
            case PURPLE -> 8991416;
            case RED -> 11546150;
            case WHITE -> 16383998;
            case YELLOW -> 16701501;
            default -> 0;
        };
    }

    private static final Map<DyeColor, Item> colors = new IdentityHashMap<>();
    static {
        colors.put(DyeColor.BLACK, Items.BLACK_DYE);
        colors.put(DyeColor.RED, Items.RED_DYE);
        colors.put(DyeColor.GREEN, Items.GREEN_DYE);
        colors.put(DyeColor.BROWN, Items.BROWN_DYE);
        colors.put(DyeColor.BLUE, Items.BLUE_DYE);
        colors.put(DyeColor.PURPLE, Items.PURPLE_DYE);
        colors.put(DyeColor.CYAN, Items.CYAN_DYE);
        colors.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
        colors.put(DyeColor.GRAY, Items.GRAY_DYE);
        colors.put(DyeColor.PINK, Items.PINK_DYE);
        colors.put(DyeColor.LIME, Items.LIME_DYE);
        colors.put(DyeColor.YELLOW, Items.YELLOW_DYE);
        colors.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
        colors.put(DyeColor.MAGENTA, Items.MAGENTA_DYE);
        colors.put(DyeColor.ORANGE, Items.ORANGE_DYE);
        colors.put(DyeColor.WHITE, Items.WHITE_DYE);
    }

    public static Item getDyeItem(DyeColor color) {
        return colors.get(color);
    }
}
