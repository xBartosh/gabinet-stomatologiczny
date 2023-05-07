package pl.gabinet.gabinetstomatologiczny.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/surgery")
@Tag(name = "Surgery API")
public class SurgeryAPI {
    private final SurgeryService surgeryService;

    public SurgeryAPI(SurgeryService surgeryService) {
        this.surgeryService = surgeryService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all surgeries")
    public ResponseEntity<List<Surgery>> findAllSurgeries() {
        return ResponseEntity.ok().body(surgeryService.findAllSurgeries());
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get surgery by name")
    public ResponseEntity<Surgery> findSurgeryByName(@PathVariable String name) {
        Optional<Surgery> surgery = surgeryService.findSurgeryByName(name);
        return surgery
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Edit surgery")
    @PatchMapping("/edit")
    public ResponseEntity<Surgery> editSurgeryPrice(@RequestParam String name, @RequestParam double price) {
        Optional<Surgery> surgery = surgeryService.editSurgeryPrice(name, price);
        return surgery
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Add surgery")
    @PostMapping("/add")
    public ResponseEntity<?> addSurgery(@RequestParam String name, @RequestParam double price) {
        try {
            Surgery surgery = surgeryService.addSurgery(name, price);
            return ResponseEntity.ok(surgery);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete surgery")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSurgery(@RequestParam String name) {
        boolean isDeleted = surgeryService.deleteSurgery(name);
        return isDeleted ?
                ResponseEntity.ok(String.format("Successfully deleted surgery with name=%s", name)) :
                ResponseEntity.badRequest().body(String.format("Could not delete surgery with name=%s", name));
    }

}
