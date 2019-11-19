package org.fuzzitdev.javafuzz.maven;


import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import dev.fuzzit.javafuzz.core.AbstractFuzzTarget;
import dev.fuzzit.javafuzz.core.Fuzzer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Mojo( name = "fuzz", defaultPhase = LifecyclePhase.INITIALIZE, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class FuzzGoal extends AbstractMojo
{

    @Parameter(defaultValue="${project}", required=true, readonly=true)
    MavenProject project;

    @Parameter( property = "className", required = true)
    private String className;

    @Parameter( property = "dirs", required = false)
    private String dirs;

    @Parameter( property = "exactArtifactPath", required = false)
    private String exactArtifactPath;

    @Parameter( property = "rssLimitMb", required = false)
    private String rssLimitMb;

//    @Parameter( property = "timeout", required = false)
//    private String timeout;

    public void execute()
    {
        try {
            List<URL> pathUrls = new ArrayList<>();
            for(String mavenCompilePath: project.getTestClasspathElements()) {
                pathUrls.add(new File(mavenCompilePath).toURI().toURL());
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);
            System.out.println("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

// need to define parent classloader which knows all dependencies of the plugin
            URLClassLoader classLoader = new URLClassLoader(urlsForClassLoader, FuzzGoal.class.getClassLoader());
            AbstractFuzzTarget fuzzTarget =
                    (AbstractFuzzTarget) classLoader.loadClass(className).getDeclaredConstructor().newInstance();
            Fuzzer fuzzer = new Fuzzer(fuzzTarget, this.dirs);
            fuzzer.start();
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | DependencyResolutionRequiredException
                | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("hello mojo");
    }
}
