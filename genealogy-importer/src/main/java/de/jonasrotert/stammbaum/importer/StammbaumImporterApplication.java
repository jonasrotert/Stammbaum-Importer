package de.jonasrotert.stammbaum.importer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.jonasrotert.stammbaum.importer.service.Importer;

@SpringBootApplication
public class StammbaumImporterApplication implements CommandLineRunner
{

    private static Logger LOGGER = LoggerFactory.getLogger(StammbaumImporterApplication.class);

    public static void main(final String[] args)
    {
        LOGGER.info("Starting app");
        SpringApplication.run(StammbaumImporterApplication.class, args);
    }

    @Autowired
    private Importer importer;

    @Override
    public void run(final String... args) throws Exception
    {
        String fileName = null;
        for (int i = 0; i < args.length; ++i)
        {
            if ("-i".equals(args[i]) && i < args.length - 1)
            {
                fileName = args[i + 1];
                LOGGER.info("Got filename {}", fileName);
            }
        }

        if (StringUtils.isNotBlank(fileName))
        {
            this.importer.importPerson(fileName);

        }

        LOGGER.info("Done");
    }
}
