package me.knighthat.interactivedeck.component;

import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.knighthat.interactivedeck.activity.DefaultActivity;
import me.knighthat.interactivedeck.component.ibutton.IButton;

public class Buttons {

    private static final @NotNull List<IButton> buttons = new ArrayList<>();

    public static @NotNull List<IButton> buttons() {
        return Collections.unmodifiableList(buttons);
    }

    public static void add( @NotNull String id, @NotNull JsonElement element ) {
        IButton btn = new IButton(DefaultActivity.INSTANCE, id);
        btn.update(element.getAsJsonObject());
        buttons.add(btn);
    }

    public static void clear() {
        buttons.clear();
    }
}
