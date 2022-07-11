package fr.salers.annunaki.data.processor.impl.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TrackedEntityContainer {

    private final List<TrackedEntity> positions = new ArrayList<>();

    @Setter
    private boolean split;

    @Setter
    private EntityMovement lastMovement;

    public TrackedEntityContainer(TrackedEntity position) {
        positions.add(position);

        split = true;

        lastMovement = new EntityMovement(EntityMovementType.ABSOLUTE,
                position.getPosX(), position.getPosY(), position.getPosZ());
    }

    public void onPreTick() {
        if (split) {
            if (lastMovement == null) {
                Bukkit.broadcastMessage("error 1");
                return;
            }

            Bukkit.broadcastMessage("" + positions.size());

            positions.stream()
                    .filter(position -> !position.isCloned())
                    .forEach(position
                            -> positions.add(position.clone().handleMovement(lastMovement)));
        }
    }

    public void onPostTick() {
        positions.forEach(TrackedEntity::onPostTick);
    }

    public void onPostTransaction() {
        Bukkit.broadcastMessage("eat my baguette");
        positions.removeIf(position -> positions.size() > 1);
        positions.forEach(position -> position.setCloned(false));

        split = false;
    }

    public void handleMovement(EntityMovement movement) {
        positions.forEach(position
                -> positions.add(position.clone().handleMovement(movement)));
    }
}
