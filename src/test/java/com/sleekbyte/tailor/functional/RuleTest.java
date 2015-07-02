package com.sleekbyte.tailor.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sleekbyte.tailor.Tailor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for functional rule tests.
 */
public abstract class RuleTest {

    protected static final String TEST_INPUT_DIR = "src/test/java/com/sleekbyte/tailor/functional/";
    protected static final String NEWLINE_REGEX = "\\r?\\n";

    protected ByteArrayOutputStream outContent;
    protected File inputFile;
    protected Set<String> expectedMessages;

    protected abstract void addAllExpectedMsgs();

    protected abstract String getInputFilePath();

    @Before
    public void setUp() throws UnsupportedEncodingException {
        inputFile = new File(TEST_INPUT_DIR + getInputFilePath());
        expectedMessages = new HashSet<>();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, false, Charset.defaultCharset().name()));
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

    @Test
    public void testRule() throws IOException {
        String[] command = getCommandArgs();
        addAllExpectedMsgs();

        Tailor.main(command);

        Set<String> actualOutput = new HashSet<>();
        for (String msg : outContent.toString(Charset.defaultCharset().name()).split(NEWLINE_REGEX)) {
            String truncatedMsg = msg.substring(msg.indexOf(inputFile.getName()));
            actualOutput.add(truncatedMsg);
        }

        assertEquals(expectedMessages.size(), actualOutput.size());
        assertTrue(actualOutput.containsAll(expectedMessages));
    }

    protected String[] getCommandArgs() {
        return new String[]{ inputFile.getPath() };
    }

}
