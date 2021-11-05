package mini.spring.ioc.util;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class PackageUtil {

    private static final Logger logger = Logger.getLogger(PackageUtil.class.getName());

    public static Set<Class<?>> getClasses(String packageName) {

        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirPath = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            // get current path resources
            // TODO still has some issue: getResources can't find the jar file, because the static resource path will change.
            // If you need search file in jar, please do more work based on this function.
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirPath);

            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                switch (protocol) {
                    case "file":
                        logger.info("File protocol decode.");
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        findClassesInPackageByFile(packageName, filePath, recursive, classes);
                        break;
                    case "jar":
                        logger.info("Jar protocol decode.");
                        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> jarEntries = jar.entries();
                        findClassesInPackageByJar(packageName, packageDirPath, jarEntries, recursive, classes);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findClassesInPackageByFile(String packageName, String filePath, boolean recursive, Set<Class<?>> classes) {
        File dir = new File(filePath);

        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning("The filepath doesn't exist file or not a directory.");
            return;
        }

        String classFileSuffix = ".class";
        File[] listFiles = dir.listFiles(pathname -> {
            // remain directory && class file (use suffix to judge file type)
            return (pathname.isDirectory() && recursive) || (pathname.getName().endsWith(classFileSuffix));
        });

        for (File file : listFiles) {
            if (file.isDirectory()) {
                // recursive call
                findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - classFileSuffix.length());
                try {
                    Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className);
                    classes.add(loadClass);
                } catch (ClassNotFoundException e) {
                    logger.warning("Not found class: " + className);
                    e.printStackTrace();
                }
            }
        }
    }

    private static void findClassesInPackageByJar(String packageName, String packageDirPath, Enumeration<JarEntry> jarEntries, boolean recursive, Set<Class<?>> classes) {
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();

            String classFileSuffix = ".class";

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(classFileSuffix)) {
                continue;
            }

            String classNameWithPackageName = jarEntry.getName()
                    .substring(0, jarEntry.getName().length() - classFileSuffix.length()).replace('/', '.');
            try {
                Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(classNameWithPackageName);
                classes.add(loadClass);
            } catch (ClassNotFoundException e) {
                logger.warning("Not found class in jar: " + classNameWithPackageName);
                e.printStackTrace();
            }
        }
    }
}
