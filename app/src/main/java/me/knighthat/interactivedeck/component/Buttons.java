package me.knighthat.interactivedeck.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.knighthat.interactivedeck.component.ibutton.IButton;

public class Buttons {

    private static final @NotNull Map<UUID, IButton> BUTTONS = new HashMap<>();

    public static @NotNull Collection<IButton> list() {
        return Collections.unmodifiableCollection(BUTTONS.values());
    }

    public static void add( @NotNull IButton button ) {
        BUTTONS.put(button.uuid(), button);
    }

    public static void clear() {
        BUTTONS.clear();
    }

    public static @Nullable IButton get( @NotNull UUID uuid ) {
        return BUTTONS.getOrDefault(uuid, null);
    }

    public static @Nullable IButton get( @NotNull String id ) {
        return get(UUID.fromString(id));
    }

    public static void remove( @NotNull UUID uuid ) {
        BUTTONS.remove(uuid);
    }

    public static void remove( @NotNull String id ) {
        remove(UUID.fromString(id));
    }
}
