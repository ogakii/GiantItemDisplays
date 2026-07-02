package me.codex.giantitemdisplays.display;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;

final class DisplayRuntime {
    private ItemDisplay itemDisplay;
    private Interaction interaction;

    ItemDisplay itemDisplay() {
        return itemDisplay;
    }

    void itemDisplay(ItemDisplay itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    Interaction interaction() {
        return interaction;
    }

    void interaction(Interaction interaction) {
        this.interaction = interaction;
    }

    boolean hasValidItemDisplay() {
        return isValid(itemDisplay);
    }

    boolean hasValidInteraction() {
        return isValid(interaction);
    }

    private boolean isValid(Entity entity) {
        return entity != null && entity.isValid() && !entity.isDead();
    }

    void removeEntities() {
        if (itemDisplay != null && itemDisplay.isValid()) {
            itemDisplay.remove();
        }
        if (interaction != null && interaction.isValid()) {
            interaction.remove();
        }
        itemDisplay = null;
        interaction = null;
    }
}
