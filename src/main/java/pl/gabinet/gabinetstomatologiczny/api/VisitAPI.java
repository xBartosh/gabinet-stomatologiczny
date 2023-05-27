package pl.gabinet.gabinetstomatologiczny.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gabinet.gabinetstomatologiczny.visit.VisitService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visit")
@Tag(name = "Visit API")
public class VisitAPI {
    private final VisitService visitService;

    public VisitAPI(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visit by id")
    public ResponseEntity<?> findVisitById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(visitService.findVisitById(id));
        } catch(Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all visits for logged user")
    public ResponseEntity<?> findVisitsForUser(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(visitService.findVisitsForUser(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all/doctor")
    @Operation(summary = "Get all visits for doctor")
    public ResponseEntity<?> findVisitsForDoctor(@RequestParam Long doctorId) {
        try {
            return ResponseEntity.ok(visitService.findVisitsForDoctor(doctorId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all/patient")
    @Operation(summary = "Get all visits for patient")
    public ResponseEntity<?> findVisitsForPatient(@RequestParam Long patientId) {
        try {
            return ResponseEntity.ok(visitService.findVisitsForPatient(patientId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/timeframe")
    @Operation(summary = "Get visits for doctor within timeframe")
    public ResponseEntity<?> findVisitsForDoctorWithin(@RequestParam Long doctorId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  @Parameter(description = "String in format: yyyy-MM-ddTHH:mm:ssZ", example = "2017-07-21T17:32:28Z") LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  @Parameter(description = "String in format: yyyy-MM-ddTHH:mm:ssZ", example = "2017-07-21T18:32:28Z")LocalDateTime end) {
        try {
            return ResponseEntity.ok(visitService.findVisitsForDoctorWithin(doctorId, start, end));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/schedule/patient")
    @Operation(summary = "Schedule a visit as a patient")
    public ResponseEntity<?> scheduleVisitAsPatient(@RequestParam String doctorId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "String in format: yyyy-MM-ddTHH:mm:ssZ", example = "2017-07-21T17:32:28Z") LocalDateTime start,
                                               @RequestParam List<String> surgeries,
                                               Principal principal){
        String email = principal.getName();
        try {
            return ResponseEntity.ok(visitService.scheduleVisit(email, Long.valueOf(doctorId), surgeries, start, false));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/schedule/doctor")
    @Operation(summary = "Schedule a visit as a doctor")
    public ResponseEntity<?> scheduleVisitAsDoctor(@RequestParam Long patientId,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "String in format: yyyy-MM-ddTHH:mm:ssZ", example = "2017-07-21T17:32:28Z") LocalDateTime start,
                                           @RequestParam List<String> surgeries,
                                           Principal principal){
        String email = principal.getName();
        try {
            return ResponseEntity.ok(visitService.scheduleVisit(email, patientId, surgeries, start, true));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pay")
    @Operation(summary = "Pay for a visit")
    public ResponseEntity<?> payForVisit(@RequestParam Long visitId) {
        try {
            visitService.payForVisit(visitId);
            return ResponseEntity.ok("Successfully paid for a visit with id=" + visitId);
        }  catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reschedule")
    @Operation(summary = "Reschedule a visit")
    public ResponseEntity<?> rescheduleVisit(@RequestParam Long visitId,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "String in format: yyyy-MM-ddTHH:mm:ssZ", example = "2017-07-21T17:32:28Z") LocalDateTime newStart) {
        try {
            return ResponseEntity.ok(visitService.rescheduleVisit(visitId, newStart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "Cancel a visit")
    public ResponseEntity<?> cancelVisit(@RequestParam Long visitId) {
        try {
            visitService.cancelVisit(visitId);
            return ResponseEntity.ok("Successfully canceled a visit with id=" + visitId);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
