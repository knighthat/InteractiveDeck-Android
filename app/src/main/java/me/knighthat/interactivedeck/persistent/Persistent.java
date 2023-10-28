/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.persistent;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import me.knighthat.interactivedeck.component.ibutton.IButton;
import me.knighthat.interactivedeck.file.Profile;
import me.knighthat.lib.logging.Log;
import me.knighthat.lib.observable.Observable;
import me.knighthat.lib.observable.Observer;
import me.knighthat.lib.util.ShortUUID;

public class Persistent {

    private static final @NotNull Persistent INTERNAL = new Persistent();

    private static Profile defaultProfile;

    public static Profile getDefaultProfile() { return defaultProfile; }

    public static void setDefaultProfile( @NotNull Profile defaultProfile ) { Persistent.defaultProfile = defaultProfile; }

    public static void free() {
        INTERNAL.profiles.clear();
        INTERNAL.buttons.clear();
        INTERNAL.active.setValue( null );
        defaultProfile = null;
    }

    /* ============================ Profile ============================ */

    /**
     * Gets the first {@link Profile} that has its {@link UUID} matches the given one.<br>
     * If {@link UUID} is not found in the list, a {@link Optional} of null is returned.
     *
     * @param uuid to find {@link Profile}
     *
     * @return nullable profile that is wrapped by {@link Optional} class
     */
    public static @NotNull Optional<Profile> findProfile( @NotNull UUID uuid ) {
        Profile profile = null;
        for (Profile p : INTERNAL.profiles)
            if (p.getUuid().equals( uuid )) {
                profile = p;
                break;
            }
        return Optional.ofNullable( profile );
    }

    public static void add( @NotNull Profile profile ) {
        INTERNAL.profiles.add( profile );
        INTERNAL.buttons.addAll( profile.getButtons() );

        if (profile.isDefault())
            defaultProfile = profile;
    }

    public static void remove( @NotNull Profile profile ) {
        profile.getButtons().forEach( INTERNAL.buttons::remove );
        INTERNAL.profiles.remove( profile );
    }


    /* ============================ IButton ============================ */

    /**
     * Gets the first {@link Profile} that has its {@link UUID} matches the given one.<br>
     * If {@link UUID} is not found in the list, a {@link Optional} of null is returned.
     *
     * @param uuid to find {@link Profile}
     */
    public static @NotNull Optional<IButton> findButton( @NotNull UUID uuid ) {
        IButton result = null;
        for (IButton btn : INTERNAL.buttons)
            if (btn.getUuid().equals( uuid )) {
                result = btn;
                break;
            }
        return Optional.ofNullable( result );
    }

    public static void add( @NotNull IButton button ) { INTERNAL.buttons.add( button ); }

    public static void remove( @NotNull IButton button ) { INTERNAL.buttons.remove( button ); }


    /* ============================ Active Profile ============================ */

    /**
     * @return currently showing {@link Profile}, null {@link Optional} if none is set.
     */
    public static @NotNull Optional<Profile> getActive() { return INTERNAL.active.getValue(); }

    /**
     * Sets active {@link Profile} then notifies all observers about the change.<br>
     *
     * @param profile new active {@link Profile}
     */
    public static void setActive( @NotNull Profile profile ) {
        Log.info(
                String.format(
                        "Now showing %s (%s) with %s button(s)",
                        profile.getDisplayName(),
                        ShortUUID.from( profile.getUuid() ),
                        profile.getButtons().size()
                )
        );

        INTERNAL.active.setValue( profile );
    }

    /**
     * Add a "watcher" to active {@link Profile}. When new active profile is set,<br>
     * this function gets called to action.
     *
     * @param observer "watcher" to active {@link Profile}
     */
    public static void observeActive( @NotNull Observer<Profile> observer ) { INTERNAL.active.observe( observer ); }


    private final @NotNull Set<Profile> profiles;
    private final @NotNull Set<IButton> buttons;
    private final @NotNull Observable<Profile> active;

    private Persistent() {
        this.profiles = new HashSet<>();
        this.buttons = new HashSet<>();
        this.active = Observable.of( null );
    }
}
