package me.knighthat.interactivedeck.connection.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import me.knighthat.interactivedeck.activity.DefaultActivity;
import me.knighthat.interactivedeck.component.Buttons;
import me.knighthat.interactivedeck.component.ibutton.IButton;

public class RequestHandler {

    public static void process( @NotNull Request request ) {
        JsonElement content = request.getContent();

        switch (request.getType()) {
            case ADD:
                handleAdd(content);
                break;
            case REMOVE:
                handleRemove(content);
                break;
            case UPDATE:
                handleUpdate(content);
                break;
            case PAIR:
                handlePairing(content);
                break;
        }
    }

    static void handleAdd( @NotNull JsonElement content ) {

    }

    static void handleRemove( @NotNull JsonElement content ) {

    }

    static void handleUpdate( @NotNull JsonElement content ) {
        JsonObject json = content.getAsJsonObject();

        String idStr = json.get("uuid").getAsString();
        IButton button = Buttons.get(idStr);

        DefaultActivity.HANDLER.post(() -> {
            if (button != null)
                button.update(json);
        });
    }

    static void handlePairing( @NotNull JsonElement content ) {
        JsonArray btnArray = content.getAsJsonArray();

        btnArray.forEach(btn -> {
            JsonObject btnJson = btn.getAsJsonObject();
            IButton button = new IButton(DefaultActivity.INSTANCE, btnJson);
            Buttons.add(button);
        });

        DefaultActivity.INSTANCE.startBtnLayout();
    }
}
