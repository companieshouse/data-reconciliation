package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElasticsearchSlicedScrollValidatorTest {

    private ElasticsearchSlicedScrollValidator validator;

    @BeforeEach
    void setUp() {
        this.validator = new ElasticsearchSlicedScrollValidator();
    }

    @Test
    void testReturnTrueIfMultipleSlicedScrollRequestValid() {
        //when
        boolean actual = validator.validateSliceConfiguration(1, 2);

        //then
        assertTrue(actual);
    }

    @Test
    void testReturnTrueIfSingleSlicedScrollRequestValid() {
        //when
        boolean actual = validator.validateSliceConfiguration(0, 1);

        //then
        assertTrue(actual);
    }

    @Test
    void testReturnFalseIfSliceIdGreaterThanOrEqualToNumberOfSlices() {
        //when
        boolean actual = validator.validateSliceConfiguration(2, 2);

        //then
        assertFalse(actual);
    }

    @Test
    void testReturnFalseIfSliceIdNegative() {
        //when
        boolean actual = validator.validateSliceConfiguration(-1, 2);

        //then
        assertFalse(actual);
    }

    @Test
    void testReturnFalseIfNumberOfSlicesLessThanOne() {
        //when
        boolean actual = validator.validateSliceConfiguration(0, 0);

        //then
        assertFalse(actual);
    }

    @Test
    void testReturnFalseIfSliceIdNonzeroAndNumberOfSlicesIsOne() {
        //when
        boolean actual = validator.validateSliceConfiguration(1, 1);

        //then
        assertFalse(actual);
    }
}
