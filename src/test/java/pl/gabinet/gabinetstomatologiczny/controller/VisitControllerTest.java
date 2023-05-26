package pl.gabinet.gabinetstomatologiczny.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import pl.gabinet.gabinetstomatologiczny.visit.Visit;
import pl.gabinet.gabinetstomatologiczny.visit.VisitService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {
    private VisitController visitController;

    @Mock
    private VisitService visitService;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @Test
    void testVisits() {
        // given
        String email = "test@example.com";
        List<Visit> expectedVisits = new ArrayList<>();
        expectedVisits.add(new Visit());
        expectedVisits.add(new Visit());

        // when
        when(principal.getName()).thenReturn(email);
        when(visitService.findVisitsForUser(email)).thenReturn(expectedVisits);

        String viewName = visitController.visits(principal, model);

        // then
        assertEquals("visits", viewName);
        verify(model).addAttribute("visits", expectedVisits);
        verify(visitService).findVisitsForUser(email);
    }
}
