package pl.gabinet.gabinetstomatologiczny.surgery;

import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurgeryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SurgeryService.class);
    private final SurgeryRepository surgeryRepository;

    public SurgeryService(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    public List<Surgery> findAllSurgeries() {
        return Lists.newArrayList(surgeryRepository.findAll());
    }

    public Optional<Surgery> findSurgeryByName(String name) {
        name = name.trim();
        return surgeryRepository.findByName(name);
    }

    public Surgery addSurgery(String name, double price) throws IllegalArgumentException {
        name = name.trim();
        LOGGER.info("Going to add new surgery, name={}, price={}", name, price);
        try {
            return surgeryRepository.save(new Surgery(name, price));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Not able to add new surgery with name=%s and price=%.2f", name, price));
        }
    }

    @Transactional
    public Optional<Surgery> editSurgeryPrice(String name, double price) {
        name = name.trim();
        LOGGER.info("Going to edit surgery, name={}, new price={}", name, price);
        Optional<Surgery> surgery = surgeryRepository.findByName(name);
        return surgery.map(srg -> {
            srg.setPrice(price);
            LOGGER.info("Successfully edited surgery, new surgery={}", srg);
            return Optional.of(srg);
        }).orElse(Optional.empty());
    }

    public boolean deleteSurgery(String name) {
        name = name.trim();
        Optional<Surgery> surgery = surgeryRepository.findByName(name);
        try {
            if (surgery.isPresent()) {
                LOGGER.info("Going to delete surgery={}", surgery);
                surgeryRepository.delete(surgery.get());
                LOGGER.info("Successfully deleted surgery={}", surgery);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public double getPriceForSurgeries(List<Surgery> surgeries) throws IllegalArgumentException {
        int sumToPay = 0;

        List<String> surgeriesAsString = surgeries.stream().map(Surgery::getName).toList();
        for (String surgeryName : surgeriesAsString) {
            Surgery surgery = surgeryRepository.findByName(surgeryName)
                    .orElseThrow(() -> new IllegalArgumentException("There is no surgery with name=" + surgeryName));
            sumToPay += surgery.getPrice();
        }

        return sumToPay;
    }
}
