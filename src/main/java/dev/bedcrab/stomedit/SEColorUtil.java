package dev.bedcrab.stomedit;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum SEColorUtil {

    GENERIC(0xF2F2F2, 0xD4D4D4, true),
    FAIL(0xFF5555, 0xFF0000, true),
    HINT(0xFFFFAA, 0xFFFF55),
    SPECIAL(0xEE38FF, 0xFF2121),
    ;

    private final int value;
    private final int delta;
    private final boolean isSecondary;
    SEColorUtil(int c1, int c2) {
        this(c1, c2, false);
    }
    SEColorUtil(int c1, int c2, boolean isSecondary) {
        this.value = c1;
        this.delta = c2 - c1;
        this.isSecondary = isSecondary;
    }

    public TextColor asTextColor(boolean useSecondary) {
        return asTextColor(0, useSecondary)[0];
    }

   private TextColor[] asTextColor(int length, boolean useSecondary) {
       final int toVal = value + delta;
       if (delta == 0 || length == 0 || isSecondary) return new TextColor[] { TextColor.color(useSecondary ? toVal : value) };
       int fromR = (value >> 16) & 0xFF;
       int fromG = (value >> 8) & 0xFF;
       int fromB = value & 0xFF;

       int toR = (toVal >> 16) & 0xFF;
       int toG = (toVal >> 8) & 0xFF;
       int toB = toVal & 0xFF;

       TextColor[] arr = new TextColor[length];
       for (int i = 0; i < length; i++) {
           double t = (double) i / (length - 1);
           int charColor = (
                   (((int)(fromR + (toR - fromR) * t)) << 16) |
                   (((int)(fromG + (toG - fromG) * t) << 8)) |
                   ((int)(fromB + (toB - fromB) * t))
           );
           arr[i] = TextColor.color(charColor);
       }
       return arr;
   }

   public @NotNull TextComponent text(String text) {
        return text(text, false);
   }

   public @NotNull TextComponent text(String text, boolean useSecondary) {
        if (delta == 0 || isSecondary) return Component.text(text).color(asTextColor(useSecondary));
        TextComponent.@NotNull Builder component = Component.text();
        TextColor[] colors = asTextColor(text.length(), false);
        String[] chars = text.split("");
        for (int i = 0; i < colors.length; i++) component.append(Component.text(chars[i]).color(colors[i]));
        return component.build();
   }

   public @NotNull TranslatableComponent translatable(String key, ComponentLike... args) {
       if (delta == 0) return Component.translatable(key, args).color(asTextColor(false));
       for (int i = 0; i < args.length; i++) args[i] = args[i].asComponent().color(asTextColor(true));
       return Component.translatable(key, args).color(asTextColor(false));
   }

   public @NotNull Component format(String text, ComponentLike... args) {
       for (int i = 0; i < args.length; i++) args[i] = args[i].asComponent().color(asTextColor(true));
       Component result = Component.text(text).color(asTextColor(false));
       for (ComponentLike arg : args) result = result.replaceText(TextReplacementConfig.builder()
           .match("%%")
           .replacement(arg)
           .once()
           .build()
       );
       return result;
   }

   public @NotNull Component format(String text, String... args) {
        return format(text, Arrays.stream(args).map(Component::text).toList().toArray(new Component[args.length]));
   }
}
