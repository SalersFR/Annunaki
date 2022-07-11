package fr.salers.annunaki.data.processor.impl.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EntityMovement {
    private final EntityMovementType type;
    private final double x, y, z;
}
