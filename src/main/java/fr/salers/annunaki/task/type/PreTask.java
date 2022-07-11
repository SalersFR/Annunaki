package fr.salers.annunaki.task.type;

import fr.salers.annunaki.task.Task;

public interface PreTask extends Task {

    void handlePreTick();
}
