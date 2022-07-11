package fr.salers.annunaki.task.type;

import fr.salers.annunaki.task.Task;

public interface PostTask extends Task {

    void handlePostTick();
}
