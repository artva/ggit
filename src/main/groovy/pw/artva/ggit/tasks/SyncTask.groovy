package pw.artva.ggit.tasks

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor

import pw.artva.ggit.config.RepositoryConfig
import pw.artva.ggit.operation.base.AbstractOperation
import pw.artva.ggit.operation.SyncOperation

import javax.inject.Inject

/**
 * Created by Artur Vakhrameev on 18.02.2018.
 */
class SyncTask extends DefaultTask {

    private final WorkerExecutor workerExecutor

    @Inject
    public SyncTask(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor
    }

    @TaskAction
    void action() {
        RepositoryConfig config = project.ggit.gitRepository
        new SyncOperation(config, project).execute()
        executeChild(config.subModules)
    }

    private void executeChild(NamedDomainObjectContainer<RepositoryConfig> subModules) {
        subModules.each {
            workerExecutor.submit(SyncOperation, new Action<WorkerConfiguration>() {
                @Override
                void execute(WorkerConfiguration workerConfiguration) {
                    workerConfiguration.params = [it, project] as Serializable[]
                }
            })
            executeChild(it.subModules)
        }
    }
}
