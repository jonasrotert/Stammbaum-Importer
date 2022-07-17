package de.jonasrotert.stammbaum.importer.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImporterTest
{

    @Autowired
    private Importer importer;

    @Test
    void importPerson_Positive_01()
    {
        final File file = new File(this.getClass().getClassLoader().getResource("Zimmer.ged").getFile());

        assertDoesNotThrow(() -> {
            final var testee = this.importer.importPerson(file.getAbsolutePath());

            assertNotNull(testee);
            assertFalse(testee.isEmpty());
        });

    }

}
