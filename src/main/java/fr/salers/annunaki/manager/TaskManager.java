package fr.salers.annunaki.manager;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.task.Task;
import fr.salers.annunaki.task.impl.TransactionTask;
import fr.salers.annunaki.task.type.PostTask;
import fr.salers.annunaki.task.type.PreTask;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TaskManager extends BukkitRunnable {

    private final List<Task> tasks = new ArrayList<>();
    private final List<PreTask> preTasks = new ArrayList<>();
    private final List<PostTask> postTasks = new ArrayList<>();

    private boolean running;

    private int tick;

    public void setup() {
        tasks.add(new TransactionTask());

        preTasks.addAll(tasks.stream()
                .filter(task -> task instanceof PreTask)
                .map(task -> (PreTask) task)
                .collect(Collectors.toList()));

        postTasks.addAll(tasks.stream()
                .filter(task -> task instanceof PostTask)
                .map(task -> (PostTask) task)
                .collect(Collectors.toList()));
    }

    public void start() {
        running = true;

        this.runTaskTimer(Annunaki.getInstance(), 0L, 1L);

        Annunaki.getInstance().getNmsManager().getNmsImplementation().insertPostTask(this);
    }

    @Override
    public void run() {
        ++tick;

        preTasks.forEach(PreTask::handlePreTick);
    }

    public void handlePostTick() {
        postTasks.forEach(PostTask::handlePostTick);
    }

    public void stop() {
        if (!running) return;

        this.cancel();
        running = false;
    }
}
