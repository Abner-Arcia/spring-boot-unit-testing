package com.luv2code.junitdemo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;

class ConditionalTest {

    @Test
    @Disabled("Don't run until JIRA #123 is resolved")
    void basicTest() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testForWindowsOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testForMacOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.WINDOWS})
    void testForMacAndWindowsOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testForLinuxOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void testForJava17Only() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledOnJre(JRE.JAVA_13)
    void testForJava13Only() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledForJreRange(min=JRE.JAVA_13, max=JRE.JAVA_18)
    void testForJavaRange13To18Only() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledForJreRange(min=JRE.JAVA_11)
    void testForJavaRange11MinOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledIfEnvironmentVariable(named="LUV2CODE_ENV", matches="DEV")
    void testForDevEnvironmentOnly() {
        // Execute method and perform asserts
    }

    @Test
    @EnabledIfSystemProperty(named="LUV2CODE_SYS_PROP", matches="CI_CD_DEPLOY")
    void testForSystemPropertyOnly() {
        // Execute method and perform asserts
    }
}
