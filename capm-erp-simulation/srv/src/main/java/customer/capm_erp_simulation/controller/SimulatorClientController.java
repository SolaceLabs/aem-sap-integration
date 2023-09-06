package customer.capm_erp_simulation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import customer.capm_erp_simulation.models.config.ErpSimulatorSchedules;
import customer.capm_erp_simulation.models.config.SolaceConnectionParameters;
import customer.capm_erp_simulation.producer.DynamicTaskSchedulerService;
import customer.capm_erp_simulation.service.SolaceEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@Slf4j
public class SimulatorClientController {

    @Autowired
    private SolaceEventPublisher solaceEventPublisher;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DynamicTaskSchedulerService dynamicTaskSchedulerService;

    @GetMapping
    public String homePage(final Model model) {
        model.addAttribute("appName", "CAPM EPR Simulator");
        model.addAttribute("solaceConnectionParameters", new SolaceConnectionParameters());
        return "home";
    }

    @PostMapping(path = "/connectToBroker")
    public String connectToBroker(@Valid @ModelAttribute("solaceConnectionParameters") SolaceConnectionParameters solaceConnectionParameters, BindingResult bindingResult, final Model model) {
        if (!bindingResult.hasErrors()) {
            boolean brokerConnected = solaceEventPublisher.connectToBroker(solaceConnectionParameters);
            model.addAttribute("brokerConnected", brokerConnected);
            model.addAttribute("erpSimulatorSchedules", new ErpSimulatorSchedules());
        }
        return "home";
    }

    @PostMapping(path = "/simulateERPMessages")
    public String simulateERPMessages(final Model model, final ErpSimulatorSchedules erpSimulatorSchedules, @RequestParam(name = "solaceConnectionParametersJson", required = false) String solaceConnectionParametersJson) {
        log.info("Incoming schedules :{}", erpSimulatorSchedules);
        dynamicTaskSchedulerService.scheduleTasks(erpSimulatorSchedules);
        model.addAttribute("erpSimulatorSchedules", erpSimulatorSchedules);
        model.addAttribute("brokerConnected", true);
        try {
            model.addAttribute("solaceConnectionParameters", objectMapper.readValue(solaceConnectionParametersJson, SolaceConnectionParameters.class));
        } catch (JsonProcessingException e) {
            log.error("Error encountered while parsing solaceConnectionParametersJson, error :", e);
        }
        return "home";
    }
}
