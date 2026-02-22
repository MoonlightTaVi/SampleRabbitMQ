package srmq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.tavi.srmq.dto.RequestDTO;

import jakarta.validation.Validator;


/**
 * Tests for {@link RequestDTO} validation.
 */
public class ValidationTest {

    private static LocalValidatorFactoryBean factory;
    private static Validator validator;


    /**
     * The tests use the default Spring Validator bean.
     * The factory must be closed <b>after</b> the tests complete.
     */
    @BeforeAll
    public static void setup() {
        factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void teardown() {
        factory.close();
    }


    @Test
    void allValid() throws Exception {
        RequestDTO dto = getNotNullDto();
        dto.setAmount(0.01f);

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void allInvalid() throws Exception {
        RequestDTO dto = new RequestDTO();
        dto.setAmount(0.001f);

        var violations = validator.validate(dto);
        assertEquals(4, violations.size());
    }

    @Test
    void decimalPlacesInvalid() throws Exception {
        RequestDTO dto = getNotNullDto();
        dto.setAmount(0.001f);

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    void negativeAmountInvalid() throws Exception {
        RequestDTO dto = getNotNullDto();
        dto.setAmount(-0.01f);

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }


    /**
     * @return A new {@code RequestDTO} object with some values assigned to
     *         all of its {@code @NotNull} annotated fields.
     */
    private RequestDTO getNotNullDto() {
        RequestDTO dto = new RequestDTO();
        dto.setSenderName("Sender");
        dto.setReceiverName("Receiver");
        dto.setCurrentDate();
        return dto;
    }
}
