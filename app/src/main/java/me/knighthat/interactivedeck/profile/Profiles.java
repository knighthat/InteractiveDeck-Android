package me.knighthat.interactivedeck.profile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.knighthat.interactivedeck.file.Profile;

public class Profiles {

    public static final @NotNull List<Profile> PROFILES = new ArrayList<>();

    public static @Nullable Profile get( @NotNull UUID uuid ) {
        for ( Profile p : PROFILES )
            if ( p.uuid().equals( uuid ) )
                return p;
        return null;
    }
}
