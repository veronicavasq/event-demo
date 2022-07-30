package com.event.demo.controller;

import com.event.demo.exception.ErrorInfo;
import com.event.demo.exception.EventException;
import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import com.event.demo.model.ViolationSummary;
import com.event.demo.service.EventService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Event API", version = "1.0"))
@Tag(name = "Event", description = "Event operations")
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Process an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event was processed",
                    content = {@Content(schema = @Schema(implementation = Violation.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid event",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Violation processEvent(@RequestBody Event event) throws EventException {
        return this.eventService.processEvent(event);
    }

    @Operation(summary = "List violations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Violation list",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Violation.class)))}
            )
    })
    @GetMapping(value = "/violation", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Violation> getViolations(@RequestParam(value = "eventType", required = false) EventType eventType,
                                         @RequestParam(value = "paid", required = false) Boolean paid,
                                         @RequestParam(value = "licensePlate", required = false) String licensePlate) {
        return this.eventService.getViolations(eventType, paid, licensePlate);
    }


    @Operation(summary = "Pay a violation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Violation paid"),
            @ApiResponse(responseCode = "400", description = "Invalid violation",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @PostMapping(value = "violation/{violationId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public void violationPayment(@PathVariable String violationId) throws EventException {
        this.eventService.violationPayment(violationId);
    }

    @Operation(summary = "Violation Summary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Violation summary report",
                    content = {@Content(schema = @Schema(implementation = ViolationSummary.class))}
            )
    })
    @GetMapping(value = "violation/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ViolationSummary getViolationSummary() {
        return this.eventService.getViolationSummary();
    }

}
