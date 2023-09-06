package customer.capm_erp_simulation.producer;

import customer.capm_erp_simulation.models.config.ErpSimulatorSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicTaskSchedulerService {


    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    private final Map<String, ScheduledFuture<?>> taskFutures = new HashMap<>();


    public void scheduleTasks(final ErpSimulatorSchedules erpSimulatorSchedules) {
        handleSalesOrderCreateEventScheduling(erpSimulatorSchedules.getSalesOrderCreateDuration());
        handleSalesOrderChangeEventScheduling(erpSimulatorSchedules.getSalesOrderChangeDuration());

        handleBusinessPartnerCreateEventScheduling(erpSimulatorSchedules.getBusinessPartnerCreateDuration());
        handleBusinessPartnerChangeEventScheduling(erpSimulatorSchedules.getBusinessPartnerChangeDuration());

        handleMaterialMasterCreateEventScheduling(erpSimulatorSchedules.getMaterialMasterCreateDuration());
        handleMaterialMasterChangeEventScheduling(erpSimulatorSchedules.getMaterialMasterChangeDuration());

        handleChartOfAccountsCreateEventScheduling(erpSimulatorSchedules.getChartOfAccountsCreateDuration());
        handleChartOfAccountsChangeEventScheduling(erpSimulatorSchedules.getChartOfAccountsChangeDuration());

        handleNotificationCreateEventScheduling(erpSimulatorSchedules.getNotificationCreateDuration());
        handleNotificationChangeEventScheduling(erpSimulatorSchedules.getNotificationChangeDuration());
    }

    private void handleSalesOrderCreateEventScheduling(final int salesOrderCreateDuration) {
        //check if the task is already running
        cancelExistingScheduledTask("salesOrderCreateTask");
        if (salesOrderCreateDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateSalesOrderCreateEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (salesOrderCreateDuration * 60000));
            taskFutures.put("salesOrderCreateTask", future);
        }
    }

    private void handleSalesOrderChangeEventScheduling(final int salesOrderChangeDuration) {
        cancelExistingScheduledTask("salesOrderChangeTask");
        if (salesOrderChangeDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateSalesOrderChangeEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (salesOrderChangeDuration * 60000));
            taskFutures.put("salesOrderChangeTask", future);
        }
    }


    private void handleBusinessPartnerCreateEventScheduling(final int businessPartnerCreateDuration) {
        cancelExistingScheduledTask("businessPartnerCreateTask");
        if (businessPartnerCreateDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateBusinessPartnerCreateEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (businessPartnerCreateDuration * 60000));
            taskFutures.put("businessPartnerCreateTask", future);
        }
    }

    private void handleBusinessPartnerChangeEventScheduling(final int businessPartnerChangeDuration) {
        cancelExistingScheduledTask("businessPartnerChangeTask");
        if (businessPartnerChangeDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateBusinessPartnerChangeEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (businessPartnerChangeDuration * 60000));
            taskFutures.put("businessPartnerChangeTask", future);
        }
    }

    private void handleMaterialMasterCreateEventScheduling(final int materialMasterCreateDuration) {
        cancelExistingScheduledTask("materialMasterCreateTask");
        if (materialMasterCreateDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateMaterialMasterCreateEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (materialMasterCreateDuration * 60000));
            taskFutures.put("materialMasterCreateTask", future);
        }
    }

    private void handleMaterialMasterChangeEventScheduling(final int materialMasterChangeDuration) {
        cancelExistingScheduledTask("materialMasterChangeTask");
        if (materialMasterChangeDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateMaterialMasterChangeEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (materialMasterChangeDuration * 60000));
            taskFutures.put("materialMasterChangeTask", future);
        }
    }


    private void handleChartOfAccountsCreateEventScheduling(final int chartOfAccountsCreateDuration) {
        cancelExistingScheduledTask("chartOfAccountsCreateTask");
        if (chartOfAccountsCreateDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateChartOfAccountsCreateEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (chartOfAccountsCreateDuration * 60000));
            taskFutures.put("chartOfAccountsCreateTask", future);
        }
    }

    private void handleChartOfAccountsChangeEventScheduling(final int chartOfAccountsChangeDuration) {
        cancelExistingScheduledTask("chartOfAccountsChangeTask");
        if (chartOfAccountsChangeDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateChartOfAccountsChangeEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (chartOfAccountsChangeDuration * 60000));
            taskFutures.put("chartOfAccountsChangeTask", future);
        }
    }

    private void handleNotificationCreateEventScheduling(final int notificationCreateDuration) {
        cancelExistingScheduledTask("notificationCreateTask");
        if (notificationCreateDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateNotificationCreateEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (notificationCreateDuration * 60000));
            taskFutures.put("notificationCreateTask", future);
        }
    }

    private void handleNotificationChangeEventScheduling(final int notificationChangeDuration) {
        cancelExistingScheduledTask("notificationChangeTask");
        if (notificationChangeDuration > 0) {
            Runnable taskToSchedule = () -> scheduledTaskService.simulateNotificationChangeEvents();
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(taskToSchedule, (notificationChangeDuration * 60000));
            taskFutures.put("notificationChangeTask", future);
        }
    }

    private void cancelExistingScheduledTask(final String taskName) {
        if (taskFutures.containsKey(taskName)) {
            taskFutures.get(taskName).cancel(true);
            taskFutures.remove(taskName);
        }
    }

}
