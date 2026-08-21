package com.github.ermeyasgirma.jgal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

class PackagedJarIT {
    @Test
    void packagedJarPrintsUsageWithNoArguments() throws Exception {
        ProcessResult result = run();

        assertEquals(0, result.exitCode);
        assertTrue(result.output.startsWith("Usage: java -jar jgal.jar"));
    }

    @Test
    void packagedJarRunsBundledKnapsackProblem() throws Exception {
        ProcessResult result = run("com.github.ermeyasgirma.jgal.demo.KnapsackProblem",
                "--generations", "0", "--quiet");

        assertEquals(0, result.exitCode);
        assertTrue(result.output.contains("Fittest solution is:"));
    }

    @Test
    void packagedJarLoadsAnExternalProblemJar() throws Exception {
        ProcessResult result = run(createExternalProblemJar().toString(), "example.ExternalProblem",
                "--generations", "0", "--quiet");

        assertEquals(0, result.exitCode);
        assertTrue(result.output.contains("Fittest solution is:"));
    }

    @Test
    void packagedJarRejectsAnUnreadableProblemJar() throws Exception {
        ProcessResult result = run("missing-problem.jar", "example.ExternalProblem", "--quiet");

        assertEquals(1, result.exitCode);
        assertTrue(result.output.contains("Problem JAR is not readable: missing-problem.jar"));
    }

    private static Path createExternalProblemJar() throws Exception {
        Path directory = Files.createTempDirectory("jgal-external-problem");
        Path source = directory.resolve("ExternalProblem.java");
        Files.write(source, externalProblemSource().getBytes(StandardCharsets.UTF_8));
        Path classes = directory.resolve("classes");
        Files.createDirectories(classes);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "A JDK is required to run packaged JAR integration tests");
        assertEquals(0, compiler.run(null, null, null, "-classpath", new File("target/jgal.jar").getAbsolutePath(),
                "-d", classes.toString(), source.toString()));

        Path jar = directory.resolve("external-problem.jar");
        try (OutputStream output = Files.newOutputStream(jar); JarOutputStream archive = new JarOutputStream(output)) {
            addClasses(archive, classes, classes.resolve("example"));
        }
        return jar;
    }

    private static void addClasses(JarOutputStream archive, Path classes, Path directory) throws Exception {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
            for (Path path : paths) {
                if (Files.isDirectory(path)) {
                    addClasses(archive, classes, path);
                } else {
                    archive.putNextEntry(new JarEntry(classes.relativize(path).toString().replace(File.separatorChar, '/')));
                    Files.copy(path, archive);
                    archive.closeEntry();
                }
            }
        }
    }

    private static String externalProblemSource() {
        return "package example;\n"
                + "import java.util.ArrayList;\n"
                + "import java.util.List;\n"
                + "import java.util.Random;\n"
                + "import com.github.ermeyasgirma.jgal.FitnessFunc;\n"
                + "import com.github.ermeyasgirma.jgal.GAProblem;\n"
                + "import com.github.ermeyasgirma.jgal.Popmember;\n"
                + "import com.github.ermeyasgirma.jgal.Population;\n"
                + "public final class ExternalProblem implements GAProblem<Integer> {\n"
                + "  public Popmember<Integer> createPrototype() { return new Member(new Integer[] { 1 }); }\n"
                + "  public static final class Member extends Popmember<Integer> {\n"
                + "    public Member(Integer[] genes) { super(genes, new FitnessFunc<Integer>() {\n"
                + "      public double fitnessScore(Integer[] values) { return values[0].doubleValue(); } }); }\n"
                + "    public Population<Integer> createInitialPopulation(int size, Random random) {\n"
                + "      List<Popmember<Integer>> members = new ArrayList<Popmember<Integer>>();\n"
                + "      for (int i = 0; i < size; i++) members.add(new Member(new Integer[] { 1 }));\n"
                + "      return new Population<Integer>(members); }\n"
                + "    public Popmember<Integer> createChild(Integer[] genes) { return new Member(genes); }\n"
                + "    public Integer[] mutate(Integer[] genes, double rate, Random random) { return genes; }\n"
                + "  }\n"
                + "}\n";
    }

    private static ProcessResult run(String... arguments) throws Exception {
        List<String> command = new ArrayList<String>();
        command.add(new File(System.getProperty("java.home"), "bin/java").getPath());
        command.add("-jar");
        command.add(new File("jgal.jar").getAbsolutePath());
        command.addAll(Arrays.asList(arguments));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(readAll(process.getInputStream()), StandardCharsets.UTF_8);
        return new ProcessResult(process.waitFor(), output);
    }

    private static byte[] readAll(InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static final class ProcessResult {
        private final int exitCode;
        private final String output;

        ProcessResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}
