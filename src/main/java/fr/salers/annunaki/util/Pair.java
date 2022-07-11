package fr.salers.annunaki.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Pair<X, Y> {
    // Taken from Frequency

    private X x;
    private Y y;
}
