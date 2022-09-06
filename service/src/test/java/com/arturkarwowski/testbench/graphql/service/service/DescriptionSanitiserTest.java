package com.arturkarwowski.testbench.graphql.service.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DescriptionSanitiserTest {

    @Test
    void shouldSanitise() {

        var raw = "lorem ipsum blabla bla Lorem LOREM loREMlORem";
        var sanitiser = new DescriptionSanitiser();

        var expected = "PAIN IPSUM BLABLA BLA PAIN PAIN PAINPAIN";

        assertThat(sanitiser.sanitise(raw)).isEqualTo(expected);
    }
}