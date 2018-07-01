package com.github.nexus.config.cli;

import com.github.nexus.config.Config;
import com.github.nexus.config.ConfigFactory;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

public class CliDelegateTest {

    private CliDelegate cliDelegate;

    public CliDelegateTest() {
    }

    @Before
    public void setUp() {
        cliDelegate = CliDelegate.instance();
    }

    @Test
    public void help() throws Exception {

        CliResult result = cliDelegate.execute("help");
        assertThat(result).isNotNull();
        assertThat(result.getConfig()).isNotPresent();
        assertThat(result.getStatus()).isEqualTo(0);

    }

    @Test
    public void withValidConfig() throws Exception {

        CliResult result = cliDelegate.execute(
                "-configfile",
                getClass().getResource("/sample-config.json").getFile());

        assertThat(result).isNotNull();
        assertThat(result.getConfig().get()).isSameAs(cliDelegate.getConfig());
        assertThat(result.getStatus()).isEqualTo(0);
    }

    @Test
    public void withKeygenMissingKeyPaths() throws Exception {
        
        try {
            cliDelegate.execute(
                    "-keygen",
                    "-configfile",
                    getClass().getResource("/sample-config.json").getFile());
            
                    failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch(ConstraintViolationException ex) {
            assertThat(ex.getConstraintViolations()).hasSize(2);
            
           List<String> paths =  ex.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath())
                    .map(Objects::toString)
                   .sorted()
                   .collect(Collectors.toList());
           
           assertThat(paths).containsExactly("keys[0].privateKey.path","keys[0].publicKey.path");    
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void callApiVersionWithConfigFileDoesnotExist() throws Exception {
        cliDelegate.execute("-configfile", "bogus.json");
    }

    @Test(expected = CliException.class)
    public void processArgsMissing() throws Exception {
        cliDelegate.execute();
    }

    @Test
    public void withConstraintViolations() throws Exception {

        try {
            cliDelegate.execute(
                    "-configfile",
                    getClass().getResource("/missing-config.json").getFile());
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getConstraintViolations()).hasSize(1);
        }

    }

    
    @Test
    public void keygen() throws Exception {
        
        Config c = ConfigFactory.create().create(getClass().getResource("/keygen-sample.json").openStream());
        
        CliResult result =             cliDelegate.execute(
                    "-keygen",
                    "-configfile",
                    getClass().getResource("/keygen-sample.json").getFile());
        
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(0);
        
        assertThat(result.getConfig()).isNotNull();
        
    }

}
